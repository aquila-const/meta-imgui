# meta-imgui
Yocto layer for imgui project.


## Layer

This layer adds the ability to compile and use your ImGui application on boot utilizing CMake. This uses BitBake to compile to an image and as of now it has been tested on the Jetson Nano 2GB Devkit. More tests are definitely welcome as I will be adding more machines later. Jetson Nano is Arm64 so it *should* run on similar machines.

