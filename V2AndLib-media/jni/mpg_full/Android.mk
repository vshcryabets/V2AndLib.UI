LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := lamewrapper_full

LOCAL_CFLAGS := -ffast-math -DFPM_ARM -fexceptions -O2
LOCAL_ARM_MODE := arm
APP_ABI := all

LOCAL_SRC_FILES := LameWrapper.cpp common.c dct64_i386.c decode_i386.c \
                interface.c layer1.c layer2.c layer3.c tabinit.c bitstream.c fft.c id3tag.c \
                presets.c quantize.c reservoir.c tables.c util.c VbrTag.c encoder.c \
                gain_analysis.c lame.c newmdct.c psymodel.c quantize_pvt.c set_get.c takehiro.c \
                vbrquantize.c version.c mpglib_interface.c
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)

