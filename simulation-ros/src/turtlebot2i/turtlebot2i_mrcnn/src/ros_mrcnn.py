#!/usr/bin/env python3

""" 
    Copyright 2018-03-02 Alberto Hata
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
"""
# TODO
# Make the code work with cv2 from python2
# Import cv2 just from visualize_cv2 

# Force loading python 3 version of cv2
import importlib.util
#spec = importlib.util.spec_from_file_location("cv2", "/usr/local/lib/python3.5/dist-packages/cv2/python-3.5/cv2.cpython-35m-x86_64-linux-gnu.so")
spec = importlib.util.spec_from_file_location("cv2", "/usr/local/lib/python3.5/dist-packages/cv2/cv2.cpython-35m-x86_64-linux-gnu.so")
cv2 = importlib.util.module_from_spec(spec)
spec.loader.exec_module(cv2)
#import cv2
import time

import rospy
import message_filters
from cv_bridge import CvBridge, CvBridgeError
from sensor_msgs.msg import Image

import re
from graphviz import Digraph, Source
from shapely.geometry import box

import numpy as np
from visualize_cv import display_instances, class_names

# Root directory of the project
import os
import sys
ROOT_DIR = os.path.abspath(os.path.join(os.path.realpath(__file__), '../..')) 
LIB_PATH = os.path.join(ROOT_DIR, "mrcnn/lib/mask_rcnn-2.1-py3.6.1.egg")
print (LIB_PATH)
sys.path.append(LIB_PATH)

dir(sys.modules[__name__])
# Import Mask RCNN
from mrcnn import model as modellib
from mrcnn.config import Config

import std_msgs.msg
from turtlebot2i_scene_graph.msg import SceneGraph
from turtlebot2i_safety.msg import VelocityScale

# Path to trained weights file 
LOG_DIR = os.path.join(ROOT_DIR, "logs")
#MODEL_PATH = os.path.join(ROOT_DIR, "models/mask_rcnn_coco.h5")
MODEL_PATH = os.path.join(ROOT_DIR,"models/mrcnn.h5")


class ShapesConfig(Config):
    """Configuration for training on the toy shapes dataset.
    Derives from the base Config class and overrides values specific
    to the toy shapes dataset.
    """
    # Give the configuration a recognizable name
    NAME = "vrepAll"

    # Train on 1 GPU and 8 images per GPU. We can put multiple images on each
    # GPU because the images are small. Batch size is 8 (GPUs * images/GPU).
    GPU_COUNT = 1
    IMAGES_PER_GPU = 1

    # Number of classes (including background)
    NUM_CLASSES = 1 + 8  # background + 3 shapes

    # Use small images for faster training. Set the limits of the small side
    # the large side, and that determines the image shape.
    IMAGE_MIN_DIM = 480
    IMAGE_MAX_DIM = 640

    # Use smaller anchors because our image and objects are small
    RPN_ANCHOR_SCALES = (8 * 4, 16 * 4, 32 * 4, 64 * 4, 128 * 4)  # anchor side in pixels

    # Reduce training ROIs per image because the images are small and have
    # few objects. Aim to allow ROI sampling to pick 33% positive ROIs.
    TRAIN_ROIS_PER_IMAGE = 40

    # Use a small epoch since the data is simple
    STEPS_PER_EPOCH = 100

    # use small validation steps since the epoch is small
    VALIDATION_STEPS = 5

class InferenceConfig(ShapesConfig):
    GPU_COUNT = 1
    IMAGES_PER_GPU = 1

class ros_mask_rcnn:

    def __init__(self):

        # Load model
        config = InferenceConfig()
        config.display()
        
        self.model = modellib.MaskRCNN(
            mode="inference", model_dir=LOG_DIR, config=config
        )
        
        self.model.load_weights(MODEL_PATH, by_name=True)
        self.dot = Digraph(comment='warehouse', format='svg')

        # Set topics
        self.bridge = CvBridge()
        self.check = False
        self.to_display = True
       
        self.to_display = rospy.get_param('/mrcnn/display_results', False)

        #option = input("Do you want to display the inference result and scene graph? (yes/no): ") 
        #if option.lower() != 'yes':
        #    self.to_display = False

        # Use ApproximateTimeSynchronizer if depth and rgb camera doesn't havse same timestamp, otherwise use Time Synchronizer if both cameras have same timestamp.
        self.image_sub = message_filters.Subscriber('/turtlebot2i/camera/rgb/raw_image', Image)
        self.image_depth_sub = message_filters.Subscriber('/turtlebot2i/camera/depth/raw_image', Image)
        #self.ts = message_filters.TimeSynchronizer([self.image_sub, self.image_depth_sub], queue_size=1)
        self.ts = message_filters.ApproximateTimeSynchronizer([self.image_sub, self.image_depth_sub], 10, 0.1)
        self.ts.registerCallback(self.callback)

        self.image_pub = rospy.Publisher("/turtlebot2i/mrcnn_out", Image, queue_size=1)
        self.scenegraph_pub = rospy.Publisher('/turtlebot2i/scene_graph', SceneGraph, queue_size=10)
        self.time_start_list = []
        self.time_sg_end_list = []
        self.time_all_end_list = []

    def get_overlap_bbox(self, rec1, rec2):
        #y1, x1, y2, x2 = boxes
        y1min, x1min, y1max, x1max = rec1[0], rec1[1], rec1[2], rec1[3]
        y2min, x2min, y2max, x2max = rec2[0], rec2[1], rec2[2], rec2[3]
        # s.view()

        box1 = box(x1min, y1min, x1max, y1max)
        box2 = box(x2min, y2min, x2max, y2max)
        isOverlapping = box1.intersects(box2)
        intersection_area = box1.intersection(box2).area/box1.area*100
        #print (pol_overl, intersection_area)
        return isOverlapping, intersection_area

        #isOverlapping = (i[1] < j[3] and j[1] < i[3] and i[0] < j[2] and j[0] < i[2])
        #isOverlapping = (x1min < x2max and x2min < x1max and y1min < y2max and y2min < y1max)
        #print (isOverlapping)
        #return isOverlapping

    def get_type(self, i):
        if re.match(r'Wall', i):
            obj_type = 3 #wall
        elif re.match(r'Human', i):
            obj_type = 2 #human
        elif re.match(r'robot', i):
            obj_type = 1 # robot # non-human dynamic objects
        else:
            obj_type = 0 # static objects
        return obj_type

    def callback(self, image, depth_image):

        try:
            self.time_start_list.append(time.time())
            farClippingPlane = 3.5
            nearClippingPlane = 0.0099999
            cv_depth_image = self.bridge.imgmsg_to_cv2(depth_image,"passthrough")
            cv_depth_image = cv2.flip(cv_depth_image, 0)

            #print ("Depth Image size: ", cv_depth_image.shape)
            #print ('min', min(cv_depth_image))
            #cv2.imshow("depth image", cv_depth_image)
            #cv2.waitKey(0)

            cv_depth_image = nearClippingPlane  + (cv_depth_image * (farClippingPlane - nearClippingPlane))
            #print ("Depth Image size: ", cv_depth_image.shape)
            if self.check == True:
                self.dot.clear()
            end = time.time()
            cv_image = self.bridge.imgmsg_to_cv2(image, "rgb8")
            results = self.model.detect([cv_image], verbose=1)

            r = results[0]
            #if self.to_display == True:
            img_out = display_instances(
                cv_image, r['rois'], r['masks'], r['class_ids'], class_names, r['scores'], show_window=self.to_display 
            )
            
            #if len(r['class_ids']) > 0:

            count_objects = [0] * len(class_names)
            detected_objects = []
            distances_from_mask = []
            cropped_roi_distances = []

            for i in range(len(r['class_ids'])):
                detected_objects.append(class_names[r['class_ids'][i]]+'#'+str(count_objects[r['class_ids'][i]]))
                count_objects[r['class_ids'][i]] += 1
                print ('Object : ',r['class_ids'][i], detected_objects[i], r['rois'][i])

            self.dot.node_attr['shape']='record'
            #robot_velocity = get_velocity(robot_list[robot_num])
            #robot_label = '{%s|%s|velocity: %.2f}'%(robot_list[robot_num].name, robot_list[robot_num].vision_sensor.name, robot_velocity)
            robot_label = "turtlebot2i"
            
            self.dot.node('robot', label=robot_label)
            self.dot.node('warehouse', label='warehouse')
            self.dot.node('floor', label='{floor|size: 25*25}')
            self.dot.edge('warehouse','floor')


            scene_dot = Digraph(comment='warehouse', format='svg')
            scene_dot.node_attr['shape']='record'
            scene_dot.node('robot', label=robot_label)
            scene_dot.node('warehouse', label='warehouse')
            scene_dot.node('floor', label='{floor|size: 25*25}')
            scene_dot.edge('warehouse','floor')

            for i in range(len(r['class_ids'])):
                #_id = r['class_ids'][i]
                node_label = detected_objects[i]
                direction = 0
                y1min, x1min, y1max, x1max = r['rois'][i][0], r['rois'][i][1], r['rois'][i][2], r['rois'][i][3]
                distances_from_mask.append(cv_depth_image[r['masks'][:,:,i]])
                min_distance = min(distances_from_mask[i])
                #min_index = distances_from_mask[i].index(min(min_distance))
                #min_indices = [i for i, x in enumerate(distances_from_mask[i]) if x == min_distance]

                min_indices = np.where(np.array(distances_from_mask[i]) == min_distance)[0]
                #print ('Min Index : ', min_indices[0], ' Min distance: ', min_distance)

                #print ('Mask Shape: ',r['masks'][:,:,i].shape)
                #print ('Mask Shape: ',r['masks'][:,:,i])

                cropped_roi_distances.append(cv_depth_image[y1min:y1max, x1min:x1max])

                if re.match(r'Wall*', detected_objects[i]):
                    self.dot.node(detected_objects[i], label=node_label)
                    self.dot.edge('warehouse', detected_objects[i], label='on')

                    node_label_scene_graph = '{%s|type: %s|distance: %.2f|orientation: %.2f|direction: %.2f|velocity: %.2f|size_x: %.2f|size_y: %.2f}'%(detected_objects[i], self.get_type(detected_objects[i]), min(distances_from_mask[i]), 0, 0, 0, 1, 1)
                    scene_dot.node(detected_objects[i], label=node_label_scene_graph)
                    scene_dot.edge('warehouse', detected_objects[i], label='on')

                elif re.match(r'Product*', detected_objects[i]):
                    overlapping_check = False
                    intersection_area = 0.0
                    for j in range(len(r['class_ids'])):
                        if j != i:
                            isOverlapping, intersection_area = self.get_overlap_bbox(r['rois'][i], r['rois'][j])
                            #print ('Comparing :',detected_objects[i],' => ', detected_objects[j], ' Result: ', isOverlapping, ' Intersection Area: ', intersection_area)

                            if isOverlapping and intersection_area > 25.0:
                                #print ("distances_from_mask : ", distances_from_mask[i].shape, 'Min: ', min(distances_from_mask[i]), 'Max: ', max(distances_from_mask[i]), 'Mean: ', np.mean(np.array(distances_from_mask[i])))
                                node_label = "%s|{Distance|Min: %.2f|Max: %.2f|Mean: %.2f}|intersection area: %.2f"%( detected_objects[i],  min(distances_from_mask[i]), max(distances_from_mask[i]), np.mean(np.array(distances_from_mask[i])), intersection_area)
                                self.dot.node(detected_objects[i], label=node_label)
                                self.dot.edge(detected_objects[j], detected_objects[i], label='on')
                                overlapping_check = True
                                node_label_scene_graph = '{%s|type: %s|distance: %.2f|orientation: %.2f|direction: %.2f|velocity: %.2f|size_x: %.2f|size_y: %.2f}'%( detected_objects[i], self.get_type(detected_objects[i]), min(distances_from_mask[i]), 0, 0, 0, 1, 1)
                                scene_dot.node(detected_objects[i], label=node_label_scene_graph)
                                scene_dot.edge(detected_objects[j], detected_objects[i], label='on')

                                break
                    if overlapping_check == False:
                        node_label = "%s|{Distance|Min: %.2f|Max: %.2f|Mean: %.2f}|intersection area: %.2f"%( detected_objects[i],  min(distances_from_mask[i]), max(distances_from_mask[i]), np.mean(np.array(distances_from_mask[i])), intersection_area)
                        self.dot.node(detected_objects[i], label=node_label)
                        self.dot.edge('floor', detected_objects[i], label='on')

                        node_label_scene_graph = '{%s|type: %s|distance: %.2f|orientation: %.2f|direction: %.2f|velocity: %.2f|size_x: %.2f|size_y: %.2f}'%( detected_objects[i], self.get_type(detected_objects[i]), min(distances_from_mask[i]), 0, 0, 0, 1, 1)
                        scene_dot.node(detected_objects[i], label=node_label_scene_graph)
                        scene_dot.edge('floor', detected_objects[i], label='on')
                else:
                    node_label = "%s|{Distance|Min: %.2f|Max: %.2f|Mean: %.2f}"%( detected_objects[i],  min(distances_from_mask[i]), max(distances_from_mask[i]), np.mean(np.array(distances_from_mask[i])))
                    self.dot.node(detected_objects[i], label=node_label)
                    self.dot.edge('floor', detected_objects[i], label='on')

                    node_label_scene_graph = '{%s|type: %s|distance: %.2f|orientation: %.2f|direction: %.2f|velocity: %.2f|size_x: %.2f|size_y: %.2f}'%( detected_objects[i], self.get_type(detected_objects[i]), min(distances_from_mask[i]), 0, 0, 0, 1, 1)
                    scene_dot.node(detected_objects[i], label=node_label_scene_graph)
                    scene_dot.edge('floor', detected_objects[i], label='on')
                    #cv2.imshow(node_label, cv_depth_image[y1min:y1max, x1min:x1max])
                    #cv2.waitKey(0)

            #cv2.imshow('cv_depth_image', cv_depth_image)
            #cv2.waitKey(0)
            # s = Source(dot, filename="scene_graph", format="png")
            if self.to_display == True:
                self.dot.render('scene_graph.gv', view= not self.check)

            if self.check == False:
                # s.view()
                self.check = True
            

            sg_message=SceneGraph()
            sg_message.header = std_msgs.msg.Header()
            sg_message.header.stamp = rospy.Time.now()
            sg_message.sg_data=scene_dot.source
            print ('Time taken to decribe: ', time.time() - end)

            self.scenegraph_pub.publish(sg_message)
            self.image_pub.publish(self.bridge.cv2_to_imgmsg(img_out, "bgr8"))

            self.time_sg_end_list.append(time.time())
            last_duration = self.time_sg_end_list[-1] - self.time_start_list[-1]
            print("ROS MRCNN last duration:",last_duration)

        except CvBridgeError as e:
            print(e)



#    def vel_scale_callback(self, data):
#        self.time_all_end_list.append(time.time())
#        if len(self.time_all_end_list) == 100:
#            np.savez('/home/etrrhmd/duration_result/time_duration_sg_camera.npz', time_start_list=self.time_start_list, time_sg_end_list=self.time_sg_end_list, time_all_end_list=self.time_all_end_list)
#            print("saving file")

if __name__ == '__main__':
    rospy.init_node('mask_rcnn_py')
    detector = ros_mask_rcnn()
    #rospy.Subscriber('/turtlebot2i/safety/vel_scale', VelocityScale, detector.vel_scale_callback)
    rospy.spin()
