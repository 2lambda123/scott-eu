#!/usr/bin/env python
import tf
import rospy
import actionlib
import moveit_msgs.msg
from geometry_msgs.msg import PoseStamped
from moveit_commander import RobotCommander, roscpp_initialize
from moveit_commander import PlanningSceneInterface
from phantomx_pincher import Phantomx_Pincher


class Pick(object):
    _feedback = moveit_msgs.msg.PickupActionFeedback().feedback
    _result = moveit_msgs.msg.PickupActionResult().result

    def __init__(self, name):
        self._action_name = name
        self._as = actionlib.SimpleActionServer(self._action_name,
                                                moveit_msgs.msg.PickupAction,
                                                execute_cb=self.execute_cb,
                                                auto_start=False)
        self._as.start()
        rospy.loginfo('Action Service Loaded')
        self.robot = Phantomx_Pincher()
        rospy.loginfo('Moveit Robot Commander Loaded')
        self.scene = PlanningSceneInterface()
        rospy.loginfo('Moveit Planning Scene Loaded')
        rospy.loginfo('Pick action is ok. Awaiting for connections')

    def get_target(self, target_name):
        obj = self.scene.get_objects([target_name])
        obj = obj[target_name]
        pose = obj.primitive_poses[0].position
        target = [pose.x, pose.y, pose.z]
        size = obj.primitives[0].dimensions
        rospy.loginfo(target)
        return size, target

    def execute_cb(self, goal):
        r = rospy.Rate(1)
        sucess = True

        #self._result.trajectory_start = self.robot.robot.get_current_state()
        self.robot.set_start_state_to_current_state()
        self._feedback.state = "Open Gripper"
        self._as.publish_feedback(self._feedback)

        self.robot.robot.get_current_state()
        rospy.loginfo('Open Gripper Plan')
        plan = self.robot.openGripper()
        if plan is None:
            rospy.loginfo("Open Gripper plan failed")
            self._result.error_code.val = -1
            self._as.set_aborted(self._result)
            sucess = False
            return None
        self._result.trajectory_descriptions.append('OpenGripper')
        self._result.trajectory_stages.append(plan)
        self._feedback.state = "Opening gripper"
        rospy.loginfo('Opening gripper')
        ex_status = self.robot.gripper_execute(plan)
        if not ex_status:
            rospy.loginfo("Execution to open gripper failed: [%s]", ex_status)
            self._result.error_code.val = -4
            self._as.set_aborted(self._result)
            sucess = False
            return None
        self._as.publish_feedback(self._feedback)
        self.scene.remove_attached_object('gripper_link')

        self._feedback.state = "Planning to reach object"
        rospy.loginfo('Planning to reach obj')
        self._as.publish_feedback(self._feedback)
        dimension, target = self.get_target(goal.target_name)
        quat = []
        if len(goal.possible_grasps) > 0:
            quat = [goal.possible_grasps[0].grasp_pose.pose.orientation.x,
                   goal.possible_grasps[0].grasp_pose.pose.orientation.y,
                   goal.possible_grasps[0].grasp_pose.pose.orientation.z,
                   goal.possible_grasps[0].grasp_pose.pose.orientation.w]
        rospy.loginfo('Pick Quaternion [%s]', quat)
        plan = self.robot.ef_pose(target, dimension, orientation=quat)
        if plan is None:
            rospy.loginfo("Plan to grasp failed")
            self._result.error_code.val = -1
            self._as.set_aborted(self._result)
            sucess = False
            return None

        self._feedback.state = "Going to the object"
        rospy.loginfo('Going to Obj')
        self._as.publish_feedback(self._feedback)
        self._result.trajectory_descriptions.append("Going to grasp the object")
        self._result.trajectory_stages.append(plan)
        ex_status = self.robot.arm_execute(plan)
        if not ex_status:
            rospy.loginfo("Execution to grasp failed: [%s]", ex_status)
            self._result.error_code.val = -4
            self._as.set_aborted(self._result)
            sucess = False
            return None

#        self._feedback.state = "Removing obtect to be grasp from the planning scene"
#        self._as.publish_feedback(self._feedback)
        obj  = self.scene.get_objects([goal.target_name])
        obj = obj[goal.target_name]
#        self.scene.remove_world_object(goal.target_name)#

        self._feedback.state = "Attaching object"
        self._as.publish_feedback(self._feedback)
        pose = PoseStamped()
        pose.pose = obj.primitive_poses[0]
        pose.header = obj.header
        self.scene.attach_box("gripper_link",
                              obj.id,
                              pose,
                              obj.primitives[0].dimensions,
                              ['gripper_link',
                               'gripper_active_link',
                               'gripper_active2_link'])
        rospy.sleep(1)

        
        rospy.sleep(1)
        self._feedback.state = "Planning to close the gripper"
        self._as.publish_feedback(self._feedback)
        plan = self.robot.closeGripper()
        if plan is None:
            rospy.loginfo("Close Gripper plan failed")
            self._result.error_code.val = -1
            self._as.set_aborted(self._result)
            sucess = False
            return None
        self._result.trajectory_descriptions.append('CloseGripper')
        self._result.trajectory_stages.append(plan)
        self._feedback.state = "Closing gripper"
        ex_status = self.robot.gripper_execute(plan)
        if not ex_status:
            rospy.loginfo("Execution to grasp failed: [%s]", ex_status)
            self._result.error_code.val = -4
            self._as.set_aborted(self._result)
            sucess = False
            return None
        self._as.publish_feedback(self._feedback)

        if sucess:
            self._result.error_code.val = 1
            self._as.set_succeeded(self._result)


if __name__ == '__main__':
#    roscpp_initialize(sys.argv)
    rospy.init_node('pick_custom')
    server = Pick(rospy.get_name())
    rospy.spin()
