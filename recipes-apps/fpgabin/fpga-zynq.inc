#specify BIF partition attributes required for BOOT.bin
FPGA_BIF_PARTITION_ATTR ?= "bitstream"

# enable bitstream-Note this is not enabled by default (missing in FPGA_BIF_PARTITION_ATTR)
FPGA_BIF_PARTITION_IMAGE[bitstream] ?= "${DEPLOY_DIR_IMAGE}/download-${MACHINE}.bit"
FPGA_BIF_PARTITION_DEPENDS[bitstream] ?= "virtual/bitstream:do_deploy"
