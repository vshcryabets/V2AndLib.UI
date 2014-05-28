#include "JpegWrapper.h"
#include <stdio.h>
//#include <android/log.h>
#include <setjmp.h>
#include "jpeglib.h"

const char* TAG = "jpegwrapper";

struct my_error_mgr {
    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};
typedef struct my_error_mgr * my_error_ptr;

METHODDEF(void) my_error_exit(j_common_ptr cinfo) {
    my_error_ptr myerr = (my_error_ptr) cinfo->err;
    (*cinfo->err->output_message)(cinfo);
//    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "A1.E2");
    longjmp(myerr->setjmp_buffer, 1);
}

JNIEXPORT jstring JNICALL Java_com_v2soft_AndLib_media_JPEGHelper_getVersion(JNIEnv * env, jclass c) {
    return env->NewStringUTF("9.0");
}


JNIEXPORT jobject JNICALL Java_com_v2soft_AndLib_media_JPEGHelper_getJPEGInfo(JNIEnv* env,
    jclass c,
    jstring jniFileName) {

    const char* fileName = env->GetStringUTFChars(jniFileName, 0);

    FILE* file = fopen(fileName, "rb");
    env->ReleaseStringUTFChars(jniFileName, fileName);

    if ( file == NULL ) {
        return NULL;
    }

    struct jpeg_decompress_struct cinfo;
    struct my_error_mgr jerr;

    cinfo.err = jpeg_std_error(&jerr.pub);
    jerr.pub.error_exit = my_error_exit;
    if (setjmp(jerr.setjmp_buffer)) {
//            __android_log_print(ANDROID_LOG_VERBOSE, TAG, "A1.E");
            jpeg_destroy_decompress(&cinfo);
            return NULL;
        }

    jpeg_create_decompress(&cinfo);
    jpeg_stdio_src(&cinfo, file);

    //reading JPEG header
    jpeg_read_header(&cinfo, TRUE);

//    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "A1.6 %d x %d", cinfo.image_width, cinfo.image_height);
    // get class
    jclass optionsClass = env->FindClass("android/graphics/BitmapFactory$Options");
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
//        env->ExceptionClear();
//        __android_log_print(ANDROID_LOG_VERBOSE, TAG, "Got exception 1");
       return NULL;
    }
//    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "A1.7 %p", optionsClass);
    // get contructor
    jmethodID midConstructor = env->GetMethodID(optionsClass, "<init>", "()V");
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
//        env->ExceptionClear();
//        __android_log_print(ANDROID_LOG_VERBOSE, TAG, "Got exception 2");
       return NULL;
    }
//    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "A1.8 %p", midConstructor);
    jobject result = env->NewObject(optionsClass, midConstructor);
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
//        env->ExceptionClear();
//        __android_log_print(ANDROID_LOG_VERBOSE, TAG, "Got exception 3");
       return NULL;
    }
    jfieldID fieldWidth = env->GetFieldID(optionsClass, "outWidth", "I");
    jfieldID fieldHeight = env->GetFieldID(optionsClass, "outHeight", "I");
    env->SetIntField(result, fieldWidth, cinfo.image_width);
    env->SetIntField(result, fieldHeight, cinfo.image_height);

//    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "A1.9 %p aaa", result);
    //free resources
    jpeg_destroy_decompress(&cinfo);
//    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "A1.10 %p", result);
    return result;
}

