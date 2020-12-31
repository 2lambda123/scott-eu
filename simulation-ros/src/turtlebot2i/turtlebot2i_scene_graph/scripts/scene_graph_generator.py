#!/usr/bin/env python

import sys
import argparse
import rospy
from turtlebot2i_scene_graph import VrepObjectExtractor, SceneGraphGenerator, make_message
from turtlebot2i_scene_graph.msg import SceneGraph
from turtlebot2i_scene_graph.srv import GenerateSceneGraph, GenerateSceneGraphResponse

# TODO: check that there are all the objects, maybe do some config files? how to avoid hardcoding?

WALLS = [
    '80cmHighWall1000cm',
    '80cmHighWall1000cm0',
    '80cmHighWall1500cm',
    '80cmHighWall1500cm0',
    '80cmHighWall1500cm1',
    '80cmHighWall2000cm',
    '80cmHighWall2000cm0',
    '80cmHighWall500cm',
    '80cmHighWall500cm0',
    '80cmHighWall500cm1',
    '80cmHighWall500cm2',
    '80cmHighWall500cm3',
    '80cmHighWall500cm4',
    '80cmHighWall500cm5',
    '80cmHighWall200cm',
    '80cmHighWall200cm0',
    '80cmHighWall200cm1',
    '80cmHighWall200cm2',
    '80cmHighWall200cm3',
    '80cmHighWall750cm',
    '80cmHighWall750cm0',
    '80cmHighWall100cm',
    '80cmHighWall100cm0',
    '80cmHighWall100cm1',
    '80cmHighWall100cm2'
]

ROBOTS = [
    'turtlebot2i',
    'turtlebot2i#0'
]

STATIC_OBJECTS = [
    'stairs',
    'slidingDoor',
    'dockstation_body',
    'ConcreteBox',
    'ConcreteBox#0',
    'ConcreteBox#1',
    'ConcreteBox#2',
    'ConcreteBox#3',
    'ConcreteBox#4',
    'ConcreteBox#5',
    'ConcreteBox#6',
    'ConcreteBox#7',
    'ConcreteBox#8',
    'ConcreteBox#9',
    '80cmHighPillar100cm',
    '80cmHighPillar100cm0',
    'ShelfBody',
    'ShelfBody#0',
    'ShelfBody#1',
    'ShelfBodySimple',
    'ShelfBodySimple#0',
    'ShelfBodySimple#1',
    'ConveyorBeltBody',
    'ConveyorBeltBody#0',
    'ConveyorBeltBody#1',
    'ConveyorBeltBody#2',
    'ConveyorBeltBody#3',
    'ConveyorBeltBody#4',
    'ConveyorBeltBody#5',
    'ConveyorBeltBody#6',
    'ConveyorBeltBody#7',
    'ConveyorBeltBody#8',
    'ConveyorBeltBody#9'
]

DYNAMIC_OBJECTS = [
    'Walking_Bill#0',
    'Walking_Bill#1',
    'Walking_Bill#2',
    'Walking_Bill#3',
    'Walking_Bill#4',
    'Bill',
    'Bill#5',
    'Bill#0'
]


def generate_scene_graph(generator):
    scene_graph = generator.generate_scene_graph()
    scene_graph = make_message(scene_graph)
    return GenerateSceneGraphResponse(scene_graph)


def publish_scene_graph(generator, publisher):
    scene_graph = generator.generate_scene_graph()
    scene_graph = make_message(scene_graph)
    publisher.publish(scene_graph)


def main():
    rospy.init_node('scene_graph_generator', anonymous=True)

    rospy.loginfo('Parsing command line arguments...')
    argv = rospy.myargv(sys.argv)
    parser = argparse.ArgumentParser()
    parser.add_argument('mode', type=str, choices=['service', 'topic'])
    args = parser.parse_args(argv[1:])
    rospy.loginfo('Mode: %s' % args.mode)

    rospy.loginfo('Getting parameters from parameter server...')
    host = rospy.get_param('~vrep/host')
    port = rospy.get_param('~vrep/port')
    robot = rospy.get_param('~robot/name')
    rospy.loginfo('V-REP remote API server: %s:%d' % (host, port))
    rospy.loginfo('Robot: %s' % robot)

    rospy.loginfo('Connecting to V-REP, can take a while...')
    extractor = VrepObjectExtractor(host, port, WALLS, ROBOTS, STATIC_OBJECTS, DYNAMIC_OBJECTS)
    rospy.loginfo('Initialization completed')

    generator = SceneGraphGenerator(robot, extractor)

    if args.mode == 'service':
        rospy.loginfo('Starting ROS service...')
        rospy.Service(
            name='generate_scene_graph',
            service_class=GenerateSceneGraph,
            handler=lambda: generate_scene_graph(generator),
            buff_size=2 ** 20   # 1 MB, enough for camera images
        )
        rospy.spin()

    elif args.mode == 'topic':
        rospy.loginfo('Starting publishing...')
        publisher = rospy.Publisher('scene_graph', SceneGraph, queue_size=1)
        rate = rospy.Rate(30)   # 30 FPS
        while not rospy.is_shutdown():
            publish_scene_graph(generator, publisher)
            rate.sleep()

    else:
        raise ValueError


if __name__ == '__main__':
    main()
