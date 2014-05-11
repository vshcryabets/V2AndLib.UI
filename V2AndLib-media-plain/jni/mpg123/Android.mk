LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := mp3wrapper

LOCAL_CFLAGS := -ffast-math -DFPM_ARM -fexceptions -O2 -DOPT_GENERIC -DREAL_IS_FIXED
LOCAL_ARM_MODE := arm
APP_ABI := all

LOCAL_SRC_FILES := compat.c dct64.c tabinit.c \
		    dither.c equalizer.c feature.c \
		    format.c parse.c layer3.c frame.c index.c \
		    libmpg123.c readers.c optimize.c stringbuf.c synth.c id3.c
		    
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)

