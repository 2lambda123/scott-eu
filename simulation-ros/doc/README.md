# 1. Overview

This *ROS workspace* provides all the libraries for turtlebot2i robot. This robot is intended to be used inside the automated warehouse and it will be mainly resposible for picking products from shelves and placing in the conveyor belts to get delivered. The warehouse scenario also expects the presence of other robots and humans, which corresponds to a collaborative scenario. Here, the turtlebot2i workspace is comprised of three [metapackages](http://wiki.ros.org/Metapackages): 

1. **base_robot:** Contains all functionality related to the turtlebot2i base, this means the Kobuki robot and its acessories (plates, support, sensors). The base_robot is responsible for enabling the robot navigation and the environment perception.
2. **phantomx_pincher_arm:** Formed by the methods that enable the robot arm manipulation. This includes the arm motion planning through Moveit! library.
3. **turtlebot2i:** Integrates the *base_robot* and *phantomx_pincher_arm* functionalities. Includes codes related to the interaction of the robot with the warehouse.

**Note:** Each metapackage contains other ROS packages with their respective documentation.

# 2. Turtlebot2i Robot

The turtlebot is a family of robots generally used for educational and research purposes. The [turtlebot2i](http://www.trossenrobotics.com/interbotix-turtlebot-2i-mobile-ros-platform.aspx) is one of these robots and it is manufactured by Trossen Robotics. This robot is equipped with these following main parts:

<img style="float: right;" src="http://www.trossenrobotics.com/Shared/Images/Product/Interbotix-Turtlebot-2i-Mobile-ROS-Platform/Img3077_isolated.jpg" width="200">

- Kobuki Mobile Base
- Pincher MK3 Robo Arm
- Intel RealSense 3D Camera SR300-Series
- Orbbec Astra Cam
- Accelerometer/Gyro/Compass

Besides that, the turtlebot2i was equipped with the [Scanse LiDAR](http://scanse.io/), which is a low cost sensor that returns range measurements of 360 degrees of the environment.

# 3. Simulated Warehouse

Before running experiments with real robots, it is interesting to validate the code in a simulated environment. In this project, the [V-REP simulator](http://www.coppeliarobotics.com) is used to reproduce the warehouse scenario, the robot behavior and other elements that exist in this environment (e.g. trucks, workers). In this sense, the V-REP is used just for visualization purpose.
The main reason for choosing V-REP is the presence of many ready to use models, possibility to draw new models and demands relatively less computational power (compared to Gazebo).

**Note:** As V-REP does not have a native support to ROS, the [ROS Interface](http://www.coppeliarobotics.com/helpFiles/en/rosInterf.htm) plugin should be installed. 
**Note 2:** Most of the lua scripts of the scenario are stored outside V-REP. This means that the lua scripts contained in the scenario just load these files (they are inside *vrep* folder). This was necessary for version control of the scripts and better reusability.
**Note 3:** The V-REP will innitalize components in the A-Z order, so there will be a few error message in the V-REP console. But the initialization will loop until it succeed.

# 4. ROS

[Robot Operating System (ROS)](http://www.ros.org/) is framework that contains several tools and libraries for robot development. ROS is maintained by the community and many important universities collaborates with it. ROS is also used by the majority of the researchers, thus, ROS methods and codes are almost a standard among them.
The main advatage of ROS is that (in most of the cases) it dispenses the necessity of developing low level algorithms for the robot by reusing the the code avaible in its repository. 

**Note:** If you are not familiar with ROS, it is recommended the following materials: [ROS Concepts](http://wiki.ros.org/ROS/Concepts), [ROS wiki](http://wiki.ros.org/), [ROS techincal overview](http://wiki.ros.org/ROS/Technical%20Overview) and [ROS architecture](www.willowgarage.com/sites/default/files/icraoss09-ROS.pdf).

# 5. Installation

## 5.1. V-REP Installation

For simulated environment, download **V-REP Pro** or **V-REP Pro Edu**. The **V-REP Player** version was not tested here.

In this tutorial, the [V-REP 3.5](https://www.coppeliarobotics.com/previousVersions) was used. Other versions (e.g. CoppeliaSim) might not work.

Installation instructions can be found in [V-REP official documentation](https://www.coppeliarobotics.com/helpFiles/index.html).

After installing V-REP, set the following `VREP_ROOT` environment variables by running the following lines in the terminal (replace the `<path_to_vrep>` by the full path to the V-REP folder), and several lines will be added to `~/.bashrc`:

```
echo "export VREP_ROOT_DIR=/<path_to_vrep>/V-REP_PRO_EDU_V3_5_0_Linux" >> ~/.bashrc
echo "export VREP_ROOT=/<path_to_vrep>/V-REP_PRO_EDU_V3_5_0_Linux" >> ~/.bashrc
source ~/.bashrc
```

You can choose to add these environment variables manually. 

For example, if you have V-REP under Home, add following two lines to **~/.bashrc** using vim or any other editor you like:
```
export VREP_ROOT_DIR=~/V-REP_PRO_EDU_V3_5_0_Linux
export VREP_ROOT=~/V-REP_PRO_EDU_V3_5_0_Linux
```
## 5.2. Setting ROS Environment

We strongly recommend to use **ROS Kinetic** distribution.

Instructions to install ROS Kinect can be found in this link: http://wiki.ros.org/kinetic/Installation

* When running "sudo rosdep init", ignore the following error if it appears: "ERROR: default sources list file already exists:
"
* Please, **follow the "recommended" ROS installation before proceeding to the steps below**.

Step 1. Install Turttlebot2i packages from ROS
  ```
  $ sudo apt install ros-kinetic-turtlebot* libudev-dev ros-kinetic-find-object-2d ros-kinetic-rtabmap-ros ros-kinetic-moveit ros-kinetic-octomap-ros ros-kinetic-manipulation-msgs ros-kinetic-controller-manager python-wxgtk3.0 ros-kinetic-joint-* ros-kinetic-position-controllers ros-kinetic-effort-controllers
  ```

Step 2. Create the Turtlebot2i workspace by clonning the SCOTT repository
  ```
  $ sudo apt install git
  $ git clone https://github.com/EricssonResearch/scott-eu.git
  $ cd scott-eu
  ```

Step 3. Setup the catkin workspace and set ROS environment variables

Install catkin python tools and xsltproc (required by the vrep_ros_interface):

```
$ sudo apt-get install python-catkin-tools xsltproc
```

Step 4. Compile the repository from the `simulation-ros` workspace root

Go to the existing workspace under `/scott-eu/simulation-ros` and run:

  ```
  $ catkin build

  ```

Now you should have 10 packages built suceessfully.

> Also read these instructions but do not create a new workspace: 
> http://wiki.ros.org/catkin/Tutorials/create_a_workspace
>
> NB! Be careful to always select the "kinetic" version of ROS

Step 5. Install V-REP ROS Interface

Add this line to ~/.bashrc:
```
$ cat "export ROS_PACKAGE_PATH=$ROS_PACKAGE_PATH:~/scott-eu/simulation-ros" >> ~/.bashrc
```

And you can check the ROS_PACKAGE_PATH in the terminal:

```
$ echo $ROS_PACKAGE_PATH
```

Don't forget to source it manually if you don't want to restart the terminal. Check whether you can find the ROS package **vrep_ros_interface** and now install it:

```
  $ source ~/.bashrc
  $ roscd vrep_ros_interface
  $ ./install.sh  
```

## 5.3. Running the Simulated Environment

1. Start ROS CORE

    ```
    $ roscore
    ```

2. Open V-REP and load a scene

    ```
    cd $VREP_ROOT
    ./vrep.sh
    ```
    - After loading V-REP, open the warehouse scenario scenario (File -> Open Scene...), which is locate in `turtlebot2i/turtlebot2i_description/v-rep_model/v-rep_model/warehouse_scene/warehouse_scene.ttt`. V-REP scenes have `.ttt` extension.
    - Press play button to start the simulation.
    - **For additional details** please check the [Turtlebot2i V-Rep Model and Warehouse Scenes documentation](https://github.com/EricssonResearch/scott-eu/blob/master/simulation-ros/src/turtlebot2i/turtlebot2i_description/v-rep_model/README.md).
    
    **Note:** Without a running roscore, V-REP cannot communicate with other components. In case of that, check the terminal and make sure that ROS plugin is loaded successfully.
   
3. Running ROS Nodes

    All ROS programs are stored in the corresponding ROS package (e.g. turtlebot2i_navigation, turtlebot2i_safety). The instructions to run the programs can be found in the README.md files located in each package.
    
    Example to run the keyboard teleoperation:
    ```
    $ roslaunch turtlebot2i_navigation turtlebot2i_keyop.launch
    ```
    If everything is configured correctly, the terminal should show "KeyOp: connected". Otherwise, check whether roscore is running, whether V-rep scene is running and whether V-rep loaded RosInterface successfully.

### 5.3.1. Simulated Warehouse

The main simulation scene used in project is the warehouse environment. This scene is found in the [turtlebot2i_description/v-rep_model](https://github.com/EricssonResearch/scott-eu/tree/master/simulation-ros/src/turtlebot2i/turtlebot2i_description/v-rep_model) folder.

- To load the main scene, open the `warehouse_turtlebot2i.ttt` file.
- To add more elements, it is necessary to edit the lua files under [turtlebot2i_description/v-rep_model/warehouse_scene/vrep_scripts](https://github.com/EricssonResearch/scott-eu/tree/master/simulation-ros/src/turtlebot2i/turtlebot2i_description/v-rep_model/warehouse_scene/vrep_scripts) folder.
    - Each lua file corresponds to a particular object in the scene.
    
**Note:** Additional information can be found in [**Turtlebot2i V-rep model** documentation](https://github.com/EricssonResearch/scott-eu/tree/master/simulation-ros/src/turtlebot2i/turtlebot2i_description/v-rep_model).

## 5.4. Using Python VREP Remote API (Optional)

To use the python remote API provided by VREP, some adjustments are necessary:

1. Copy the remoteApi.so (.dll in Windows and .dylib in Mac) to the V-REP python programming folder
    ```
    cp $VREP_ROOT/programming/remoteApiBindings/lib/lib/64Bit/remoteApi.so $VREP_ROOT/programming/remoteApiBindings/python/python/
    ```
    - It may be necessary to change the source folder to copy the remoteApi library depending on the OS (Win/Mac/Linux) and processor (32/64bit). All the library versions are located on `$VREP_ROOT/programming/remoteApiBindings/lib/lib/` folder

2. Add python remote API library to python path

    ```
    echo "export PYTHONPATH=$PYTHONPATH:$VREP_ROOT/programming/remoteApiBindings/python/python" >> ~/.bashrc
    source ~/.bashrc
    ```


