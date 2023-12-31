#!/usr/bin/env python

import json
import yaml
import numpy as np
import rospy
import rospkg
import tf
import actionlib
from move_base_msgs.msg import MoveBaseAction, MoveBaseGoal
import moveit_msgs.msg
import geometry_msgs.msg
from moveit_commander import PlanningSceneInterface
from moveit_commander import roscpp_initialize, RobotCommander
from tf.transformations import quaternion_from_euler
import time
import vrep
# TODO: refactor classes


class Plan:
    def __init__(self):
        self.id = 0
        self.init = ''
        self.goal = ''
        self.task_list = list()


class Task:
    def __init__(self):
        self.action = ''
        self.robot = ''
        self.target = ''
        self.product = ''


class PlanInterpreter:
    def __init__(self, robot, scene):
        self.shelves = dict()
        self.products = dict()
        self.listener = tf.TransformListener()
        self.robot = robot
        self.scene = scene
        rospy.loginfo('Load moveit_commander')
        rospy.sleep(1)

        rospack = rospkg.RosPack()
        self.path = rospack.get_path('turtlebot2i_warehouse')
        print(self.path)

        self.waypoints = self.load_waypoint(self.path + '/scene.yaml')
        json_plan = self.load_json(self.path + '/plan.json')
        self.plan = self.parse_plan(json_plan)
        self.load_objects(self.path + '/scene.yaml')

        self.storage = list(([[0.00, 0.1, 0.215], 0],
                             [[0.00, 0.0, 0.215], 0],
                             [[0.00, -0.1, 0.215], 0]))

        # Init remote API
        self.clientID = self.init_remote_api('127.0.0.1')


    def init_remote_api(self, address, port=20001):
        address = str(address)
        port = int(port)
        clientID = vrep.simxStart(address, port, True, True, 5000, 5)
        attemp = 0 
        while clientID == -1 and attemp < 20:
            clientID = vrep.simxStart(address, port, True, True, 5000, 5)
            rospy.loginfo('Connecting to Vrep, attemp #%s', attemp)
            attemp += 1
        if clientID != -1:
            rospy.loginfo('Connected to Vrep')
        else:
            rospy.logerr('Vrep connection Failed')
            exit()
        return clientID

    def get_object_id(self, shelf, product):
        clientID = self.clientID
        matching_prods = []
        childs = []
        c, shelfHandle = vrep.simxGetObjectHandle(clientID, shelf, vrep.simx_opmode_blocking)
       
        for i in range(0, 9):
            _, child = vrep.simxGetObjectChild(clientID, shelfHandle, i, vrep.simx_opmode_blocking)
            if child == -1:
                break
            childs.append(child)


        for i in childs:
            code, _, _, name, _ = vrep.simxCallScriptFunction(clientID, shelf, vrep.sim_scripttype_childscript, 'getObjectName', [i], [], [], bytearray(), vrep.simx_opmode_blocking)
            name = name[0]
            if code != 0:
                continue
            if product['name'] in name:
                code = int(name.split('#')[1])
                matching_prods.append((code, name))

        if len(matching_prods) == 0:
            rospy.logerr('No [%s] in [%s]', product['name'], shelf)
            return 
        matching_prods.sort(key=lambda tup: tup[0])
        return  matching_prods[0][1]

        
    def get_object_pose(self, product_id):
        clientID = self.clientID
        c, productHandle = vrep.simxGetObjectHandle(clientID, product_id, vrep.simx_opmode_blocking)
        _, pos = vrep.simxGetObjectPosition(clientID, productHandle, -1, vrep.simx_opmode_blocking)
        _, ori = vrep.simxGetObjectOrientation(clientID, productHandle, -1, vrep.simx_opmode_blocking)
        ori = np.asarray(ori)/np.pi*180        
        return list(pos)+list(ori)

        
    def load_json(self, path):
        return json.load(open(path))

    def load_yaml(self, path):
        stream = file(path, 'r')
        return yaml.load(stream)

    def load_waypoint(self, path):
        yaml = self.load_yaml(path)

        waypoints = {}

        for line in yaml:
            waypoints[line['node']['name']] = line['node']['waypoint']

        return waypoints

    def to_pose(self, waypoint, frame="/map"):
        p = geometry_msgs.msg.PoseStamped()
        p.header.frame_id = frame
        p.header.stamp = rospy.Time.now()
        p.pose.position.x = waypoint[0]
        p.pose.position.y = waypoint[1]
        p.pose.position.z = waypoint[2]
        p.pose.orientation.w = 1
        if len(waypoint) == 7:
            p.pose.orientation.x = waypoint[3]
            p.pose.orientation.y = waypoint[4]
            p.pose.orientation.z = waypoint[5]
            p.pose.orientation.w = waypoint[6]
        elif len(waypoint) == 6:
            q = quaternion_from_euler(waypoint[3]/180.0*np.pi,
                                      waypoint[4]/180.0*np.pi,
                                      waypoint[5]/180.0*np.pi)
            p.pose.orientation.x = q[0]
            p.pose.orientation.y = q[1]
            p.pose.orientation.z = q[2]
            p.pose.orientation.w = q[3]
        return p

    def add_mesh(self, node):
        try:
            self.scene.add_mesh(node['name'],
                                self.to_pose(node['obj_position']),
                                self.path + '/' + node['mesh'])
            rospy.loginfo('New object added: %s', node['name'])
        except:
            rospy.logwarn('Failed to add object: %s %s',
                          node['name'],
                          self.path + '/' + node['mesh'])

    def add_box(self, node, size=[0.03, 0.03, 0.03]):
        try:
            self.scene.add_box(node['name'],
                               self.to_pose(node['obj_position']),
                               size)
            rospy.loginfo('New object added: %s', node['name'])
        except:
            rospy.logwarn('Failed to add product: %s',
                          node['name'])

    def add_product(self, shelf, product, idx=''):
        product = dict(self.products[product])
        product['name'] += idx
        pose = self.shelves[shelf]['obj_position']
        pose = np.asarray(pose[:3]) + np.asarray(product['obj_position'][:3])
        product['obj_position'] = np.hstack(
            (pose, product['obj_position'][3:]))

        product['id'] = self.get_object_id(shelf, product)
        product['obj_position'] = self.get_object_pose(product['id'])
        print(product['obj_position'])
        self.add_box(product)
        return product['name'], product['id']

    def obj_position(self, waypoint, offset):
        waypoint = np.asarray(waypoint)
        offset = np.asarray(offset)
        if waypoint.shape[0] != offset.shape[0]:
            rospy.logwarn('Waypoint: [%s] and Offset: [%s] shapes don\'t match',
                          waypoint,
                          offset)
            return waypoint
        return waypoint - offset
    
    def load_objects(self, path):
        shelf_item = list()
        yaml = self.load_yaml(path)
        for line in yaml:
            if 'offset' in line['node'].keys():
                line['node']['obj_position'] = self.obj_position(line['node']['waypoint'],
                                                                 line['node']['offset'])
            else:
                line['node']['obj_position'] = line['node']['waypoint']
            if 'mesh' in line['node'].keys():
                self.add_mesh(line['node'])
            if 'position' in line['node']['name']:
                shelf_item.append(line['node'])
            if 'Shelf' in line['node']['name']:
                self.shelves[
                    line['node']['name']] = line['node']
            if ('product' in line['node']['name']) | ('position' in line['node']['name']):
                self.products[
                    line['node']['name']] = line['node']
#        for i in shelf_item:
#            for j in self.shelves:
#                self.add_product(j, i['name'], idx=j)

    def parse_plan(self, json_plan):
        p_plan = Plan()
        p_plan.id = json_plan['id']
        p_plan.init = json_plan['init']
        p_plan.goal = json_plan['goal']

        for task in json_plan['plan']:
            if (len(task)) >= 3:

                p_task = Task()
                p_task.action = task[0]
                p_task.robot = task[1]
                p_task.target = task[2]
                if (len(task)) == 4:
                    p_task.product = task[3]

                p_plan.task_list.append(p_task)

        return p_plan

    def task_manager(self):
        lastWp = []
        for task in self.plan.task_list:
            if task.action == 'move':
                rospy.loginfo('Move to [%s]', task.target)
                self.__move_task(task.robot, task.target)
                lastWP = self.waypoints[task.target]
            elif task.action == 'pick':
                rospy.loginfo('Moving to [%s] at [%s]',
                              task.product,
                              task.target)
                rospy.loginfo('From [%s] \n to [%s]',
                              lastWP,
                              self.waypoint2pick(lastWP, task.product)
                              )            
                self.__move_pose(
                    self.waypoint2pick(lastWP, task.product)
                )
                product_name, product_id = self.add_product(task.target, task.product)
                rospy.loginfo('Picking [%s] at [%s]',
                              task.product,
                              task.target)
                result = self.__pick_task(product_name,
                                          rpy=[0, 1.0, 0])
                rospy.loginfo(result)
                if result.error_code.val < 0:
                    rospy.logerr("Pick has failed")
                    rospy.logerr("Last sccessful step was: %s",
                                 result.trajectory_descriptions[-1])
                    self.__retreat_arm()
                    continue
                store = self.__available_storage()
                if not store:
                    rospy.loginfo("No available Store")
                else:
                    rospy.loginfo("Storing product on [%s]",
                                  store[0])
                    rospy.loginfo("Storing product on [%s] MAP_FRAME",
                                  self.target_to_frame(store[0]))
                    result = self.__place_task(self.target_to_frame(store[0]),
                                               rpy=[0, 0, 0])
                    if result.error_code.val < 0:
                        rospy.logerr("Place has failed")
                        rospy.logerr("Last successful step was: %s",
                                     result.trajectory_descriptions[-1])
                        #drop object or try again
                        continue
                    store[1] = 1
                    # add here the stored object reposition
                    self.reposition_stored_obj(product_name,
                                               product_id,
                                               '/plate_middle_link')
            elif task.action == 'drop':
                rospy.loginfo('Dropping [%s] at [%s]',
                              task.product,
                              task.target)
                self.scene.remove_attached_object('base_footprint',
                                                  task.product)
                result = self.__pick_task(task.product,
                                          rpy=[0, 0, 0])
                
                if result.error_code.val < 0:
                    rospy.logerr("Pick product at robot tray has failed")
                    rospy.logerr("Last successful step was: %s",
                                 result.trajectory_descriptions[-1])
                    continue
                rospy.loginfo('Dropping at position: [%s]',
                              self.drop_position())
                result = self.__place_task(self.drop_position(),
                                           rpy=[0, 0, 0])
                if result.error_code.val < 0:
                    rospy.logerr("Drop has failed")
                    rospy.logerr("Last successful step was: %s",
                                 result.trajectory_descriptions[-1])
                    continue
                self.scene.remove_attached_object('gripper_link',
                                                  task.product)
                rospy.sleep(1)
                self.scene.remove_world_object(task.product)
                rospy.sleep(1)

            # self.__check_task_status()

    def reposition_stored_obj(self, obj_id, prod_id, store):
        '''
        The arm not always will extacly place the object on the robot's tray,
        thus we need to reposition the object to match the tray height
        Inputs:
        - obj_id: product id
        - store: robot link where is located the reference of storeage plate
        '''
        obj = self.scene.get_attached_objects()
        if obj_id in obj.keys():
            obj = obj[obj_id].object
        else:
            rospy.loginfo('No object attached with this the id, %s', obj_id)
            return -1
        store = self.target_to_frame([0, 0, 0.01], frame_from=store)
        pose = obj.primitive_poses[0].position
        target = [pose.x, pose.y, pose.y]
        frame = obj.header.frame_id
        pose = self.target_to_frame(target, frame_from=frame, orientation=True)
        pose[2] = store[2] + 0.01

        v_pose = self.get_object_pose(prod_id)
        v_pose[2] = store[2] + 0.01

        # shame on you...
        pose[:3] = v_pose[:3]
        print(v_pose, pose)
        v_pose = self.to_pose(v_pose)
        pose = self.to_pose(pose)        

        
        self.scene.attach_box("base_footprint",
                              obj.id,
                              pose,
                              obj.primitives[0].dimensions)
        return 1
        
    def drop_position(self, offset=[0.415, 0, 0.22]):
        return self.target_to_frame(np.asarray(offset))

    def target_to_frame(self, target,
                        frame_to="/map",
                        frame_from="/base_footprint",
                        orientation=False):
        pose = self.to_pose(target, frame=frame_from)
        self.listener.waitForTransform(frame_from,
                                       frame_to,
                                       rospy.Time.now(),
                                       rospy.Duration(4))
        self.listener.getLatestCommonTime(frame_from, frame_to)
        pose = self.listener.transformPose(frame_to, pose)
        if orientation:
            return [pose.pose.position.x,
                    pose.pose.position.y,
                    pose.pose.position.z,
                    pose.pose.orientation.x,
                    pose.pose.orientation.y,
                    pose.pose.orientation.z,
                    pose.pose.orientation.w]
        
        return [pose.pose.position.x,
                pose.pose.position.y,
                pose.pose.position.z]

    def waypoint2pick(self, waypoint, product):
        ''' waypoint information is always infront of productRed
        '''
        waypoint = np.asarray(waypoint)
        productRed_position = np.asarray(
            self.products['productRed']['obj_position'])
        product_position = np.asarray(
            self.products[product]['obj_position'])
        delta = (productRed_position - product_position)[:3]
        waypoint[:3] -= delta[:3]
        # waypoint[0] -= 0.05
        return waypoint

    def __move_pose(self, pose):
        # TODO: The client must be retrieved by the robot name
        client = actionlib.SimpleActionClient('/move_base', MoveBaseAction)
        client.wait_for_server()

        goal = MoveBaseGoal()
        goal.target_pose = self.to_pose(pose)
        client.send_goal(goal)

        wait = client.wait_for_result()
        if not wait:
            rospy.logerr("Action server not available!")
            rospy.signal_shutdown("Action server not available!")
        else:
            state = client.get_state()
        if state == 3:
            rospy.loginfo("Goal succeeded!")    
        
    def __move_task(self, robot, target):
        # TODO: The client must be retrieved by the robot name
        client = actionlib.SimpleActionClient('/move_base', MoveBaseAction)
        client.wait_for_server()

        self.__retreat_arm()

        goal = MoveBaseGoal()
        goal.target_pose = self.to_pose(self.waypoints[target])
        client.send_goal(goal)

        wait = client.wait_for_result()
        if not wait:
            rospy.logerr("Action server not available!")
            rospy.signal_shutdown("Action server not available!")
        else:
            state = client.get_state()
        if state == 3:
            rospy.loginfo("Goal succeeded!")

    def __retreat_arm(self, home=[1.5, -1.5, -3.1, 0, 0]):
        self.robot.pincher_arm.set_joint_value_target(home)
        gplan = self.robot.pincher_arm.plan()
        if len(gplan.joint_trajectory.points) == 0:
            return -1
        else:
            self.robot.pincher_arm.execute(gplan)
        
    def __pick_task(self, target_obj="box", rpy=[]):
        client = actionlib.SimpleActionClient('/pick_custom',
                                              moveit_msgs.msg.PickupAction)
        client.wait_for_server()
        goal = moveit_msgs.msg.PickupActionGoal().goal
        goal.target_name = target_obj
        if len(rpy) > 0:
            goal.possible_grasps = [moveit_msgs.msg.Grasp()]
            q = quaternion_from_euler(rpy[0], rpy[1], rpy[2])
            goal.possible_grasps[0].grasp_pose = geometry_msgs.msg.PoseStamped()
            goal.possible_grasps[0].grasp_pose.pose.orientation.x = q[0]
            goal.possible_grasps[0].grasp_pose.pose.orientation.y = q[1]
            goal.possible_grasps[0].grasp_pose.pose.orientation.z = q[2]
            goal.possible_grasps[0].grasp_pose.pose.orientation.w = q[3]
        client.send_goal(goal)
        client.wait_for_result()
        return client.get_result()

    def __place_task(self, target, rpy=[]):
        client = actionlib.SimpleActionClient('place_custom',
                                              moveit_msgs.msg.PlaceAction)
        client.wait_for_server()
        goal = moveit_msgs.msg.PlaceActionGoal().goal
        goal.place_locations.append(moveit_msgs.msg.PlaceLocation())
        goal.place_locations[0].place_pose = geometry_msgs.msg.PoseStamped()
        goal.place_locations[0].place_pose.pose.position.x = target[0]
        goal.place_locations[0].place_pose.pose.position.y = target[1]
        goal.place_locations[0].place_pose.pose.position.z = target[2]
        if len(rpy) > 0:
            q = quaternion_from_euler(rpy[0], rpy[1], rpy[2])
            goal.place_locations[0].place_pose.pose.orientation.x = q[0]
            goal.place_locations[0].place_pose.pose.orientation.y = q[1]
            goal.place_locations[0].place_pose.pose.orientation.z = q[2]
            goal.place_locations[0].place_pose.pose.orientation.w = q[3]
        print goal
        client.send_goal(goal)
        
        client.wait_for_result()
        return client.get_result()

    def __available_storage(self):
        for store in self.storage:
            if store[1] == 0:
                return store
        return False

    def __check_task_status(self, robot):
        client = actionlib.SimpleActionClient(
            robot + '/move_base', MoveBaseAction)
        client.wait_for_server()

        wait = client.wait_for_result()

        if wait:
            state = client.get_state()
            # succeeded
            if state == GoalStatus.SUCCEEDED:
                return 1
            # not completed
            else:
                return 0
        # without task
        else:
            return -1


if __name__ == "__main__":
    # rospy.sleep(30)
    roscpp_initialize([])
    rospy.init_node('plan_interpreter_py')
    robot = RobotCommander()
    scene = PlanningSceneInterface()
    # rospy.sleep(15)
    plan_int = PlanInterpreter(robot, scene)
    # print(plan_int.plan)
    # print(plan_int.waypoints)
    # print(plan_int.waypoints[""])
    plan_int.task_manager()
