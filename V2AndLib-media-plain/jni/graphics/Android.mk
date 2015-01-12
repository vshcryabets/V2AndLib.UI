LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := graphics

LOCAL_SRC_FILES := JpegWrapper.cpp jni.cpp CJPEGEncoder.cpp JPEGException.cpp
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_STATIC_LIBRARIES := libjpeg

include $(BUILD_SHARED_LIBRARY)

