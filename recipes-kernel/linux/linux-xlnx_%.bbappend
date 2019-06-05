#u-boot needs a file with a recognized extension, so create it here
# TODO: This package was empty so it was used, need to add it more appropriately
FILES_kernel-image = "/${KERNEL_IMAGEDEST}/image.ub"
do_install_append() {
	cp ${D}/${KERNEL_IMAGEDEST}/fitImage-${KERNEL_VERSION} ${D}/${KERNEL_IMAGEDEST}/image.ub
}
