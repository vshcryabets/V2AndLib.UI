LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := audiostreams

LOCAL_CFLAGS := -ffast-math -DFPM_ARM -fexceptions -O2
LOCAL_ARM_MODE := arm
APP_ABI := all

LOCAL_SRC_FILES := FileInputStream.cpp MP3InputStream.cpp AudioStreamException.cpp \
    Wrapper.cpp EncoderWrapper.cpp DecoderWrapper.cpp \
    MP3EncoderStream.cpp

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_STATIC_LIBRARIES := mpg123 lame

include $(BUILD_SHARED_LIBRARY)

