#!/usr/bin/env python
#modified from: https://github.com/ROBOTIS-GIT/turtlebot3_machine_learning/blob/master/turtlebot3_dqn/nodes/turtlebot3_dqn_stage_4

import rospy
import os
import json
import numpy as np
import random
import time
import sys
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))
from collections import deque
from std_msgs.msg import Float32MultiArray
from environment_mlp import Env
from keras.models import Sequential, load_model
from keras.optimizers import RMSprop
from keras.layers import Dense, Dropout, Activation
from turtlebot2i_safety.msg import SafetyRisk

EPISODES = 3000


class ReinforceAgent():
    def __init__(self, state_size, action_size):
        self.pub_result = rospy.Publisher('result', Float32MultiArray, queue_size=5)
        self.dirPath = os.path.dirname(os.path.realpath(__file__))
        self.dirPath = self.dirPath.replace('scott-eu/simulation-ros/src/turtlebot2i/turtlebot2i_safety/src', 'scott-eu/simulation-ros/src/turtlebot2i/turtlebot2i_safety/src/models/mlp_')
        self.result = Float32MultiArray()

        #self.load_model = True # Inference
        self.load_model = False # Training
        #self.load_episode = 885 # Inference
        self.load_episode = 0 # Training
        self.state_size = state_size
        self.action_size = action_size
        self.episode_step = 6000
        self.target_update = 2000
        self.discount_factor = 0.99
        self.learning_rate = 0.00025
        self.epsilon = 1.0
        self.epsilon_decay = 0.99
        self.epsilon_min = 0.05
        self.batch_size = 64
        self.train_start = 64
        self.memory = deque(maxlen=1000000)

        self.model = self.buildModel()
        self.target_model = self.buildModel()

        self.updateTargetModel()

        self.scores   = []
        self.episodes = []

        if self.load_model:
            self.model.set_weights(load_model(self.dirPath + str(self.load_episode) + ".h5").get_weights())

            with open(self.dirPath + str(self.load_episode) + '.json') as outfile:
                param = json.load(outfile)
                self.epsilon = param.get('epsilon')

            loaded_result = np.load(self.dirPath + str(self.load_episode) + '.npz')
            self.scores   = list(loaded_result['scores'])
            self.episodes = list(loaded_result['episodes'])


    def buildModel(self):
        model = Sequential()
        dropout = 0.2

        model.add(Dense(64, input_shape=(self.state_size,), activation='relu', kernel_initializer='lecun_uniform'))

        model.add(Dense(32, activation='relu', kernel_initializer='lecun_uniform'))
        model.add(Dropout(dropout))

        model.add(Dense(self.action_size, kernel_initializer='lecun_uniform'))
        model.add(Activation('linear'))
        model.compile(loss='mse', optimizer=RMSprop(lr=self.learning_rate, rho=0.9, epsilon=1e-06))
        model.summary()

        return model
    
    def getQvalue(self, reward, next_target, done):
        if done:
            return reward
        else:
            return reward + self.discount_factor * np.amax(next_target)

    def updateTargetModel(self):
        self.target_model.set_weights(self.model.get_weights())

    def getAction(self, state):
        if np.random.rand() <= self.epsilon:
            self.q_value = np.zeros(self.action_size)
            return random.randrange(self.action_size)
        else:
            q_value = self.model.predict(state.reshape(1, len(state)))
            self.q_value = q_value
            return np.argmax(q_value[0])

    def appendMemory(self, state, action, reward, next_state, done):
        self.memory.append((state, action, reward, next_state, done))

    def trainModel(self, target=False):
        mini_batch = random.sample(self.memory, self.batch_size)
        X_batch = np.empty((0, self.state_size), dtype=np.float64)
        Y_batch = np.empty((0, self.action_size), dtype=np.float64)

        for i in range(self.batch_size):
            states = mini_batch[i][0]
            actions = mini_batch[i][1]
            rewards = mini_batch[i][2]
            next_states = mini_batch[i][3]
            dones = mini_batch[i][4]

            q_value = self.model.predict(states.reshape(1, len(states)))
            self.q_value = q_value

            if target:
                next_target = self.target_model.predict(next_states.reshape(1, len(next_states)))

            else:
                next_target = self.model.predict(next_states.reshape(1, len(next_states)))

            next_q_value = self.getQvalue(rewards, next_target, dones)

            X_batch = np.append(X_batch, np.array([states.copy()]), axis=0)
            Y_sample = q_value.copy()

            Y_sample[0][actions] = next_q_value
            Y_batch = np.append(Y_batch, np.array([Y_sample[0]]), axis=0)

            if dones:
                X_batch = np.append(X_batch, np.array([next_states.copy()]), axis=0)
                Y_batch = np.append(Y_batch, np.array([[rewards] * self.action_size]), axis=0)

        self.model.fit(X_batch, Y_batch, batch_size=self.batch_size, epochs=1, verbose=0)



if __name__ == '__main__':
    try:
        rospy.init_node('risk_mitigation_mlp_py')
        pub_result = rospy.Publisher('result', Float32MultiArray, queue_size=5)
        pub_get_action = rospy.Publisher('get_action', Float32MultiArray, queue_size=5)
        result = Float32MultiArray()
        get_action = Float32MultiArray()

        
        env = Env()

        action_size = env.action_size
        state_size = 17

        agent = ReinforceAgent(state_size, action_size)
        global_step = 0
        start_time = time.time()

        print('start training')
        #training_mode = False  # Inference
        training_mode = True    # Training
        if training_mode:
            for e in range(agent.load_episode + 1, EPISODES):
                done = False
                state = env.reset()
                score = 0
                for t in range(agent.episode_step):
                    action = agent.getAction(state)

                    next_state, reward, done = env.step(action)

                    agent.appendMemory(state, action, reward, next_state, done)

                    if len(agent.memory) >= agent.train_start:
                        if global_step <= agent.target_update:
                            agent.trainModel()
                        else:
                            agent.trainModel(True)

                    score += reward
                    state = next_state
                    get_action.data = [env.action_list[action][0], env.action_list[action][1], score, reward]
                    pub_get_action.publish(get_action)

                    if e % 5 == 0:
                        agent.model.save(agent.dirPath + str(e) + '.h5')
                        with open(agent.dirPath + str(e) + '.json', 'w') as outfile:
                            json.dump(param_dictionary, outfile)
                        np.savez(agent.dirPath + str(e) + '.npz', scores=agent.scores, episodes=agent.episodes)

                    if t >= 2000:
                        rospy.loginfo("Time out!!")
                        done = True

                    if done:
                        result.data = [score, np.max(agent.q_value)]
                        pub_result.publish(result)
                        agent.updateTargetModel()
                        agent.scores.append(score)
                        agent.episodes.append(e)
                        m, s = divmod(int(time.time() - start_time), 60)
                        h, m = divmod(m, 60)

                        rospy.loginfo('Ep: %d score: %.2f memory: %d epsilon: %.2f time: %d:%02d:%02d',
                                      e, score, len(agent.memory), agent.epsilon, h, m, s)
                        param_keys = ['epsilon']
                        param_values = [agent.epsilon]
                        param_dictionary = dict(zip(param_keys, param_values))
                        break

                    global_step += 1
                    if global_step % agent.target_update == 0:
                        rospy.loginfo("UPDATE TARGET NETWORK")

                if agent.epsilon > agent.epsilon_min:
                    agent.epsilon *= agent.epsilon_decay
        else:
            agent.epsilon = 0.0
            prev_action = 0
            time_duration_list = []

            while True:
                data = None
                data = rospy.wait_for_message('/turtlebot2i/safety/obstacles_risk', SafetyRisk, timeout=500)
                #time_previous = time.time()
                if data == None:
                    print("no data")
                    env.publishScaleSpeed(1.0, 1.0)
                elif len(data.risk_value) == 0:
                    env.publishScaleSpeed(1.0, 1.0)
                elif max(data.risk_value) == 0:
                    env.publishScaleSpeed(1.0, 1.0)    
                else:
                    state, done = env.getState(data)
                    action = agent.getAction(np.asarray(state))
                    #time_end = time.time()
                    env.publishScaleSpeed(env.action_list[action][0], env.action_list[action][1])

                    # time_duration_list.append(time_end-time_previous)
                    # if len(time_duration_list) == 100:
                    #     dirPath = os.path.dirname(os.path.realpath(__file__))
                    #     savePath = self.dirPath.replace('scott-eu/simulation-ros/src/turtlebot2i/turtlebot2i_safety/src', 'time_duration/time_duration_rm_mlp.npz')
                    #     np.savez(savePath, time_duration_list=time_duration_list)

                    if prev_action != action:
                        print("action:",action)
                        prev_action = action

    except rospy.ROSInterruptException:
        env.vrep_control.shutdown()
        #vrep.simxFinish(clientID)
        rospy.loginfo("Training finished.")
        
