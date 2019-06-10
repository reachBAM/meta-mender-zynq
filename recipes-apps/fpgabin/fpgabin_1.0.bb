SUMMARY = "Generates fpga.bin using bootgen tool"
DESCRIPTION = "Manages task dependencies and creation of boot.bin. Use the \
BIF_PARTITION_xyz global variables and flags to determine what makes it into \
the image."

LICENSE = "BSD"

include fpga-zynq.inc
inherit deploy

PROVIDES = "virtual/fpga-bin"
FILES_${PN} = "/boot/fpga.bin"

do_configure[depends] += "${@get_bootbin_depends(d)}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

FPGA_BIF_FILE_PATH ?= "${B}/fpgagen.bif"

BOOTGEN_EXTRA_ARGS ?= ""

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"

def get_bootbin_depends(d):
    bootbindeps = ""
    bifpartition = (d.getVar("FPGA_BIF_PARTITION_ATTR", True) or "").split()
    attrdepends = d.getVarFlags("FPGA_BIF_PARTITION_DEPENDS") or {}
    for cfg in bifpartition:
        if cfg in attrdepends:
            bootbindeps = bootbindeps + " " + attrdepends[cfg]

    return bootbindeps

def create_bif(config, attrflags, attrimage, common_attr, biffd, d):
    import re, os
    for cfg in config:
        if cfg not in attrflags and common_attr:
            error_msg = "%s: invalid ATTRIBUTE" % (cfg)
            bb.error("BIF attribute Error: %s " % (error_msg))
        else:
            if common_attr:
                cfgval = attrflags[cfg].split(',')
                cfgstr = "\t [%s] %s\n" % (cfg,', '.join(cfgval))
            else:
                if cfg not in attrimage:
                    error_msg = "%s: invalid or missing elf or image" % (cfg)
                    bb.error("BIF atrribute Error: %s " % (error_msg))
                imagestr = d.expand(attrimage[cfg])
                if os.stat(imagestr).st_size == 0:
                    bb.warn("Empty file %s, excluding from bif file" %(imagestr))
                    continue
                if cfg in attrflags:
                    cfgval = attrflags[cfg].split(',')
                    cfgstr = "\t [%s] %s\n" % (', '.join(cfgval), imagestr)
                else:
                    cfgstr = "\t %s\n" % (imagestr)
            biffd.write(cfgstr)

    return

python do_configure() {

    fp = d.getVar("FPGA_BIF_FILE_PATH", True)
    biffd = open(fp, 'w')
    biffd.write("all:\n")
    biffd.write("{\n")

    bifattr = (d.getVar("FPGA_BIF_COMMON_ATTR", True) or "").split()
    if bifattr:
        attrflags = d.getVarFlags("FPGA_BIF_COMMON_ATTR") or {}
        create_bif(bifattr, attrflags,'', 1, biffd, d)

    bifpartition = (d.getVar("FPGA_BIF_PARTITION_ATTR", True) or "").split()
    if bifpartition:
        attrflags = d.getVarFlags("FPGA_BIF_PARTITION_ATTR") or {}
        attrimage = d.getVarFlags("FPGA_BIF_PARTITION_IMAGE") or {}
        create_bif(bifpartition, attrflags, attrimage, 0, biffd, d)

    biffd.write("}")
    biffd.close()
}

do_configure[vardeps] += "FPGA_BIF_PARTITION_ATTR FPGA_BIF_PARTITION_IMAGE FPGA_BIF_COMMON_ATTR"

do_compile() {
    cd ${WORKDIR}
    bootgen -image ${FPGA_BIF_FILE_PATH} -arch ${SOC_FAMILY} -process_bitstream bin -w on
    if [ ! -e ${DEPLOY_DIR_IMAGE}/download-${MACHINE}.bit.bin ]; then
        bbfatal "bootgen failed. See log"
    fi
}

do_install() {
	install -d ${D}/boot
	install -m 0644 ${DEPLOY_DIR_IMAGE}/download-${MACHINE}.bit.bin ${D}/boot/fpga.bin
}
