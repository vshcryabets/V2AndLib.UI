LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libjpegwrapper
LOCAL_MODULE_FILENAME := libjpegwrapper
LOCAL_CFLAGS := -ffast-math -DFPM_ARM -fexceptions -O2

LOCAL_SRC_FILES := \
    jcapimin.c jcapistd.c jccoefct.c jccolor.c jcdctmgr.c jchuff.c \
    jcinit.c jcmainct.c jcmarker.c jcmaster.c jcomapi.c jcparam.c \
    jcprepct.c jcsample.c jctrans.c jdapimin.c jdapistd.c \
    jdatadst.c jdatasrc.c jdcoefct.c jdcolor.c jddctmgr.c jdhuff.c \
    jdinput.c jdmainct.c jdmarker.c jdmaster.c jdmerge.c \
    jdpostct.c jdsample.c jdtrans.c jerror.c jfdctflt.c jfdctfst.c \
    jfdctint.c jidctflt.c jidctfst.c jidctint.c jquant1.c \
    jquant2.c jutils.c jmemmgr.c jcarith.c jdarith.c jaricom.c

# Use the no backing store memory manager provided by
# libjpeg. See install.txt
LOCAL_SRC_FILES +=  jmemnobs.c

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
include $(BUILD_STATIC_LIBRARY)

