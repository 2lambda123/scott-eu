# 1. Turtlebot2i V-Rep Model and Warehouse Scenes

This folder contains the V-Rep scene files. All working scenes are under the `v-rep_model/warehouse_scene` folder. Other scenes are still under development.

- The main warehouse scene file is `v-rep_model/warehouse_scene/warehouse_scene.ttt`.
- This scene will be empty when opened.
- Objects are spawned after the simulation has started.

## 1.1 Warehouse Scene Builders

By default, starting the warehouse scene will load the `turtlebot2i/turtlebot2i_description/v-rep_model/warehouse_scene/vrep_scripts/Scene_Builder.lua` script that defines which elements are going to be instantiated.

To modify the scenario, it is possible to modify the `Scene_Builder.lua` script or create a new scene builder file.

### 1.1.1 Setting/Loading a Custom Scene Builder Script

If a scene builder different from `Scene_Builder.lua` needs to be loaded, the following steps should be taken:

1. Open `turtlebot2i/turtlebot2i_description/v-rep_model/warehouse_scene/warehouse_scene.ttt` scenario in V-REP.
2. After loading the scene (will be empty at this stage), open the script parameters of *Scene_Builder* element (found in Scene hierarchy). To do that, double-click *Scipt Parameters* button (represented as a three thin rectangle icon, at left of the a document icon).
3. In the *Script Parameters* diagram, select `Scene_Builder_File_Name`. In *Value* field of *Parameter properties* , specify the desired scene builder script file name.

## 1.2. Warehouse V-REP Models

The folder `v-rep_model/warehouse_scene/vrep_models/` contains the custom V-REP models to build the warehouse scene. The V-REP models are represented by `.ttm` files that contain geometrical and physical properties of the object.

The models in this folder can be organized in these groups:
- Models to build the warehouse scene:
    - 80cmHighPillar100cm.ttm, 80cmHighWall1000cm.ttm, ConcreteBlock.ttm, Floor10x10m.ttm, etc.
- Models of the warehouse machineries:
    - Conveyor belt: ConveyorBelt.ttm
    - Automatic shelf: Shelf.ttm
    - Turtlebot2i mobile robot: turtlebot2i.ttm
- Models of the workers (Bill):
    - Working_Bill.ttm

## 1.3. Scripts of the Warehouse V-REP Models

The folder `v-rep_model/warehouse_scene/vrep_scripts/` contains scripts that specify the way models are spawned in the scene. For instance, it is possible to define the number of robots in the scene, sensors of the robot, position of the warehouse machineries and so on. The V-REP models are represented by `.lua` files.

- A list of supported API functions can be found in this [link](https://www.coppeliarobotics.com/helpFiles/en/apiFunctionListCategory.htm).
- Official information about V-REP lua scripts can be found in this [link](https://www.coppeliarobotics.com/helpFiles/en/scripts.htm).

Similarly as in V-REP models, scripts in this folder can be organized in groups:
- Models to build the warehouse scene:
    - Scene_Builder.lua: **This is the main script** which is run when the simulation is started. This script is attached to `warehouse_scene.ttt` and **can't be removed**.
- Models of the warehouse machineries:
    - Conveyor belt: ConveyorBeltBody.lua
    - Automatic shelf: ShelfBody.lua
    - Turtlebot2i mobile robot: turtlebot2i.lua
        -  There are also other scripts attached to turtlebot2i: GPS.lua, IMU.lua, lidar.lua, camera.lua, camera_SR300.lua, battery.lua

### 1.3.1. Setting turtlebot2i Cameras

By default, all turtlebot2i sensors are enabled, however the cameras can be disabled in sake of processing time.

1. Open `v-rep_model/warehouse_scene/vrep_scripts/camera.lua`.
2. Find `sim.childscriptcall_initialization` block.
3. Change either `rgb_enabled` or `depth_enabled` boolean values.
    - In general, depth sensor demands a lot of processing.

### 1.3.2. Models Spawned in the Simulation

By default, predefined objects and scene objects are instantiated when the simulation is started. However, it is also possible to remove or add models in the scene.

1. Open `v-rep_model/warehouse_scene/vrep_scripts/Scene_Builder.lua`.
2. Find `sysCall_init()` function block.
3. Use the `addModel()` function to add a model in a specific place.
