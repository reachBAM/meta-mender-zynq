FILESEXTRAPATHS_prepend := "${THISDIR}/patches:${THISDIR}/files:"

SRC_URI += "file://uboot_mender_zynq.cfg \
	    file://0001_Change_Default_Config.patch \
            "

require u-boot-mender-zynq.inc

PROVIDES += "u-boot-fw-utils"
RPROVIDES_${PN} += "u-boot-fw-utils"
