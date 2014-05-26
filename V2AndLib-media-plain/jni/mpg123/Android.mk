LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := mpg123

LOCAL_CFLAGS := -ffast-math -DFPM_ARM -fexceptions -O2 -DOPT_GENERIC -DREAL_IS_FIXED
LOCAL_SRC_FILES := compat.c dct64.c tabinit.c \
		    dither.c equalizer.c feature.c \
		    format.c parse.c layer3.c frame.c index.c \
		    libmpg123.c readers.c optimize.c stringbuf.c synth.c id3.c

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)
		    
include $(BUILD_STATIC_LIBRARY)

