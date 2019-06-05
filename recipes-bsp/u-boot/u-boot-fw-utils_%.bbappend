FILESEXTRAPATHS_prepend := "${THISDIR}/patches:${THISDIR}/files:"

SRC_URI += "file://uboot_mender_zynq.cfg \
	    file://0001_Change_Default_Config.patch \
	    file://0002_Configure_Env_And_Bootcount.patch \
            "

require u-boot-mender-zynq.inc

PROVIDES_${PN} = "u-boot-fw-utils"
RPROVIDES_${PN} = "u-boot-fw-utils"
