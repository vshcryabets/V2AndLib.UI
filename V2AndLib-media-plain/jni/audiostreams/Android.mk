LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := audiostreams

LOCAL_SRC_FILES := FileInputStream.cpp MP3DecoderStream.cpp AudioStreamException.cpp \
    Wrapper.cpp EncoderWrapper.cpp DecoderWrapper.cpp \
    MP3EncoderStream.cpp

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_STATIC_LIBRARIES := mpg123 lame

include $(BUILD_SHARED_LIBRARY)

