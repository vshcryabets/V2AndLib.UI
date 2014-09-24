LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := graphics

LOCAL_CFLAGS := -ffast-math -DFPM_ARM -fexceptions -O2

LOCAL_SRC_FILES := JpegWrapper.cpp
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_STATIC_LIBRARIES := libjpegwrapper

include $(BUILD_SHARED_LIBRARY)

