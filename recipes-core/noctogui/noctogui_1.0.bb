SUMMARY = "Installer for ImGui projects."
HOMEPAGE = "https://github.com/aquila-const/NoctoGui"
DESCRIPTION = "This is a UI for Nocto repo. Built on ImGui."
SECTION = "base"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=9768340f0372ee916245d20c0c3d67de"
DEPENDS = "gdk-pixbuf-native"

#we need x11 in order to run our GUI
DISTRO_FEATURES_append = " x11"

#There has been some issue with directly writing the ImGui application 
#to the jetson-nano-2gb image as it saves in default aarchpoky linux directory
#and is not written to the rootfs.
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"
MULTIMACH_TARGET_SYS = "jetson_nano_2gb_devkit-poky-linux"
WORKDIR = "${TMPDIR}/work/${MULTIMACH_TARGET_SYS}/${PN}/${EXTENDPE}${PV}-${PR}"

#this is your release commit SHA
SRCREV = "1a008207bef7c68cdef543d551f1617c809a89e6"

#this is where you add your repo, make sure the branch name matches as well
SRC_URI = "git://github.com/aquila-const/NoctoGui.git;protocol=https;branch=main"
S = "${WORKDIR}/git"

#these are needed to compile and run our app on boot
inherit cmake pkgconfig update-alternatives systemd
#required for windowing
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE_${PN} = "noctogui-systemd.service"

#The normal approach is to use += 
SRC_URI_append += " file://noctogui-systemd.service \
                    file://noctogui-init"


FILES_${PN} += "${system_unitdir}/system/noctogui.service"


PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[x11] = "-DWITH_X11=ON,,virtual/libx11 gtk+3"

#required by imgui and glfw to compile
DEPENDS += "libxtst libxext libxxf86vm libxi libxrandr libxrender libxcursor libxinerama libdmx libxau libxcomposite"

#fix submodule issue
do_configure_prepend() {
  cd ${WORKDIR}/git
  git submodule update --init --recursive
}
do_install_append() {
  #let's create the directories
  install -d ${D}/${sbindir}
  #app needs to be executable
  install -m 0755 ${WORKDIR}/noctogui-init ${D}/${sbindir}/noctogui-init.sh
  #let's add to the system dir to run our app on boot
  install -d ${D}/${systemd_system_unitdir}/system
  install -m 0644 ${WORKDIR}/noctogui-systemd.service ${D}/${systemd_system_unitdir}/system
  #install our app here
  install -m 0755 ${WORKDIR}/build/app/noctoui ${D}/usr/bin/noctoui

}

NATIVE_SYSTEMD_SUPPORT = "1"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} += "noctogui-systemd.service"

INITSCRIPT_NAME = "noctoui.sh"
#this is taken from psplash
INITSCRIPT_PARAMS = "start 0 S . stop 20 0 1 6 ."
