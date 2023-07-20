# meta-imgui
Yocto layer for imgui project.

## Before you build

If you went ahead and built anything and tried flashing your machine. You might be having a little bit of trouble getting things to start. That is okay. I used core-image-sato because it has everything we need and core-image-minimal is quite large and maybe I might find some way to have a lighter setup. For now you need to navigate to:
```
<your-project-root>/poky/meta/recipes-sato/matchbox-sato/matchbox-session-sato
```

And edit the session file to something like this:

```
#!/bin/sh

. /etc/formfactor/config

if [ "$HAVE_TOUCHSCREEN" = "1" ]; then
    SHOWCURSOR="no"
else
    SHOWCURSOR="yes"
fi

if [ "$HAVE_KEYBOARD" = "1" ]; then
    KEYBOARD_APPLET=""
else
    KEYBOARD_APPLET="keyboard"
fi

# matchbox-desktop &

# Lines containing feature-[foo] are removed at build time if the machine
# doesn't have the feature "foo".

START_APPLETS=showdesktop,windowselector
END_APPLETS=$KEYBOARD_APPLET,systray,startup-notify,notify
END_APPLETS=battery,$END_APPLETS # feature-acpi
END_APPLETS=battery,$END_APPLETS # feature-apm
END_APPLETS=clock,$END_APPLETS
END_APPLETS=openmoko-panel-gsm,$END_APPLETS # feature-phone

# matchbox-panel --start-applets $START_APPLETS --end-applets $END_APPLETS &
/usr/bin/noctoui &
exec matchbox-window-manager -theme Sato -use_cursor $SHOWCURSOR $@

```

If you notice at the top it runs matchbox, which is okay if you want to run matchbox but then your app will not run on boot. So instead, we can remove or comment it out and add the path to our application at the bottom.

Since it is ImGui, we should have a mouse and I will test touch screen later as I do have a touch screen module with me.

## Layer

This layer adds the ability to compile and use your ImGui application on boot utilizing CMake. This uses BitBake to compile to an image and as of now it has been tested on the Jetson Nano 2GB Devkit. More tests are definitely welcome as I will be adding more machines later. Jetson Nano is Arm64 so it *should* run on similar machines.

## Configuring

I will add an automated script to generate the files based on a CLI or a template. For now you should rename the files in noctogui, including noctogui, to your project name. You can even change meta-imgui to *meta-yourprojectui*. Anything that can make your life easier.

## Contributing
Pull requests are welcome but will be reviewed. I will also be setting up some sort of testing flow as well and later an emulator for jetson nano to reduce not needed flashing.