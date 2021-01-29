#!/usr/bin/env python

import rospy
import time
from pympler.asizeof import asizeof
from turtlebot2i_scene_graph.srv import GenerateSceneGraph as GenerateSceneGraphOut
from turtlebot2i_edge.srv import GenerateSceneGraph as GenerateSceneGraphIn
from turtlebot2i_edge.srv import GenerateSceneGraphResponse, Stamp


class ModelPerformance:
    def __init__(self, m_ap, execution_latency):
        self.m_ap = m_ap
        self.execution_latency = execution_latency


def proxy(generate_scene_graph, model_performance, request, stamp=None):
    # in the simulation with ns-3, the proxy is not really on the edge
    # here we transfer the bytes in ns-3 to have the correct delay
    if stamp is not None:
        n_bytes = asizeof(request)
        bytes_ = b'\x00' * n_bytes
        response = stamp(bytes=bytes_, max_duration=rospy.Time.from_sec(5))
        if response.stamped != n_bytes:   # timeout
            return None
    communication_latency = rospy.Time.now() - request.header.stamp

    # generate scene graph
    time_start = rospy.Time.now()
    response = generate_scene_graph()
    time_elapsed = rospy.Time.now() - time_start
    time_sleep = model_performance.execution_latency - time_elapsed
    time_sleep = time_sleep.to_sec()
    if time_sleep > 0:
        time.sleep(time_sleep)

    return GenerateSceneGraphResponse(
        communication_latency=communication_latency,
        execution_latency=model_performance.execution_latency,
        model_m_ap=model_performance.m_ap,
        scene_graph=response.scene_graph
    )


def main():
    rospy.init_node('scene_graph_proxy')

    rospy.loginfo('Getting parameters...')
    m_ap = rospy.get_param('~model/m_ap')
    on_edge = rospy.get_param('~on_edge')
    execution_latency = rospy.Duration(
        rospy.get_param('~model/execution_latency/secs'),
        rospy.get_param('~model/execution_latency/nsecs')
    )
    model_performance = ModelPerformance(m_ap, execution_latency)
    rospy.loginfo('Model mAP: %f' % m_ap)
    rospy.loginfo('On edge: %s' % str(on_edge))
    rospy.loginfo('Model execution latency: %f s' % execution_latency.to_sec())

    rospy.loginfo('Waiting for ROS service to generate scene graph...')
    generate_scene_graph = rospy.ServiceProxy('generate_scene_graph', GenerateSceneGraphOut)
    generate_scene_graph.wait_for_service()

    if on_edge:
        rospy.loginfo('Waiting for ROS service to stamp...')
        stamp = rospy.ServiceProxy('stamp', Stamp)
        stamp.wait_for_service()
    else:
        stamp = None

    rospy.Service(
        name='generate_scene_graph_proxy',
        service_class=GenerateSceneGraphIn,
        handler=lambda request: proxy(generate_scene_graph, model_performance, request, stamp=stamp),
        buff_size=2**20     # 1 MB, enough for camera images
    )
    rospy.loginfo('ROS service to generate scene graph with proxy ready')

    rospy.spin()


if __name__ == '__main__':
    main()
