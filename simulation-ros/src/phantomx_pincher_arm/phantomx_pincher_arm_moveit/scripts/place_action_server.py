#!/usr/bin/env python
import sys
import numpy as np
import rospy
import actionlib
import moveit_msgs.msg
from  geometry_msgs.msg import PoseStamped
from moveit_commander import RobotCommander, roscpp_initialize
from moveit_commander import PlanningSceneInterface
from phantomx_pincher import Phantomx_Pincher

class Place(object):
    _feedback = moveit_msgs.msg.PlaceActionFeedback().feedback
    _result = moveit_msgs.msg.PlaceActionResult().result

    def __init__(self, name):
        self._action_name = name
        self._as = actionlib.SimpleActionServer(self._action_name,
                                                moveit_msgs.msg.PlaceAction,
                                                execute_cb=self.execute_cb,
                                                auto_start=False)
        self._as.start()
        rospy.loginfo('Action Service Loaded')
        self.robot = Phantomx_Pincher()
        rospy.loginfo('Moveit Robot Commander Loaded')
        self.scene = PlanningSceneInterface()
        rospy.loginfo('Moveit Planning Scene Loaded')
        rospy.loginfo('Place action is ok. Awaiting for connections')

    def execute_cb(self, goal):
        r = rospy.Rate(1)
        sucess = True

        if len(self.scene.get_attached_objects()) < 1:
            rospy.loginfo("No object attached")
            self._result.error_code.val = -1
            self._as.set_aborted(self._result)
            sucess = False
            return None

        self.robot.set_start_state_to_current_state()
        self._feedback.state = "Planing to place pose"
        self._as.publish_feedback(self._feedback)
        target = [goal.place_locations[0].place_pose.pose.position.x,
                  goal.place_locations[0].place_pose.pose.position.y,
                  goal.place_locations[0].place_pose.pose.position.z]
        quat = [goal.place_locations[0].place_pose.pose.orientation.x,
               goal.place_locations[0].place_pose.pose.orientation.y,
               goal.place_locations[0].place_pose.pose.orientation.z,
               goal.place_locations[0].place_pose.pose.orientation.w]
        if np.sum(quat) == 0:
            # not valid quaternion
            quat = []
        rospy.loginfo('Place quaternion [%s]', quat)
        plan = self.robot.ef_pose(list(target), orientation=quat)
        if plan is None:
            rospy.loginfo("Plan to place failed")
            self._result.error_code.val = -1
            self._as.set_aborted(self._result)
            sucess = False
            return None

        self._feedback.state = "Going to place the object"
        self._as.publish_feedback(self._feedback)
        self._result.trajectory_descriptions.append("Going to place the object")
        self._result.trajectory_stages.append(plan)
        ex_status = self.robot.arm_execute(plan)
        if not ex_status:
            rospy.loginfo("Execution to place failed: [%s]", ex_status)
            self._result.error_code.val = -4
            self._as.set_aborted(self._result)
            sucess = False
            return None
        rospy.sleep(1)
        self._feedback.state = "Planning to open the gripper"
        self._as.publish_feedback(self._feedback)
        plan = self.robot.openGripper()
        if plan is None:
            rospy.loginfo("Open Gripper plan failed")
            self._result.error_code.val = -1
            self._as.set_aborted(self._result)
            sucess = False
            return None
        self._result.trajectory_descriptions.append('OpenGripper')
        self._result.trajectory_stages.append(plan)
        self._feedback.state = "Openning gripper"
        print self._feedback
        ex_status = self.robot.gripper_execute(plan)
        if not ex_status:
            rospy.loginfo("Execution to open gripper failed: [%s]", ex_status)
            self._result.error_code.val = -4
            self._as.set_aborted(self._result)
            sucess = False
            return None

        rospy.sleep(1)
        self._as.publish_feedback(self._feedback)
   
        if sucess:
            self._result.error_code.val = 1
            self._as.set_succeeded(self._result)

if __name__ == '__main__':
#    roscpp_initialize(sys.argv)
    rospy.init_node('place_custom')
    server = Place(rospy.get_name())
    rospy.spin()
