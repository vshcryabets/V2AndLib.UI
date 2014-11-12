LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := lame

LOCAL_SRC_FILES := \
    VbrTag.c bitstream.c fft.c lame.c presets.c quantize_pvt.c tables.c \
    vbrquantize.c gain_analysis.c psymodel.c reservoir.c takehiro.c \
    version.c encoder.c id3tag.c newmdct.c quantize.c set_get.c util.c

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)

include $(BUILD_SHARED_LIBRARY)

