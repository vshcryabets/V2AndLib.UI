#include "JpegWrapper.h"
#include <stdio.h>
//#include <android/log.h>
#include <setjmp.h>
#include "jpeglib.h"

const char* TAG = "jpegwrapper";

__attribute__((constructor)) static void onDlOpen(void) {
}

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

JNIEXPORT jstring JNICALL nativeGetVersion(JNIEnv * env, jclass c) {
    return env->NewStringUTF("9.0");
}


JNIEXPORT jint JNICALL nativeGetJPEGInfo(JNIEnv* env,
    jclass c,
    jstring jniFileName, jobject result) {

    const char* fileName = env->GetStringUTFChars(jniFileName, 0);

    FILE* file = fopen(fileName, "rb");
    env->ReleaseStringUTFChars(jniFileName, fileName);

    if ( file == NULL ) {
        return ERR_NO_FILE;
    }

    struct jpeg_decompress_struct cinfo;
    struct my_error_mgr jerr;

    cinfo.err = jpeg_std_error(&jerr.pub);
    jerr.pub.error_exit = my_error_exit;
    if (setjmp(jerr.setjmp_buffer)) {
            jpeg_destroy_decompress(&cinfo);
            return ERR_JPEG_DECODER;
        }

    jpeg_create_decompress(&cinfo);
    jpeg_stdio_src(&cinfo, file);

    //reading JPEG header
    jpeg_read_header(&cinfo, TRUE);

    // get class
    jclass optionsClass = env->FindClass("com/v2soft/AndLib/medianative/JPEGOptions");
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
       return ERR_CANT_GET_RESULT_CLASS;
    }

    jfieldID fieldWidth = env->GetFieldID(optionsClass, "mWidth", "I");
    jfieldID fieldHeight = env->GetFieldID(optionsClass, "mHeight", "I");
    env->SetIntField(result, fieldWidth, cinfo.image_width);
    env->SetIntField(result, fieldHeight, cinfo.image_height);

    //free resources
    jpeg_destroy_decompress(&cinfo);
    return ERR_OK;
}

const JNINativeMethod method_jpeghelper_table[] = {
  { "nativeGetVersion", "()Ljava/lang/String;", (void*) nativeGetVersion},
  { "nativeGetJPEGInfo", "(Ljava/lang/String;Lcom/v2soft/AndLib/medianative/JPEGOptions;)I", (void*) nativeGetJPEGInfo},
};
static int method_jpeghelper_table_size = sizeof(method_jpeghelper_table) / sizeof(method_jpeghelper_table[0]);


jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if ( vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    jclass clazz = env->FindClass( "com/v2soft/AndLib/medianative/JPEGHelper");
    if (clazz) {
        jint ret = env->RegisterNatives(clazz, method_jpeghelper_table, method_jpeghelper_table_size);
        env->DeleteLocalRef(clazz);
        if (  ret != 0 ) {
            return JNI_ERR;
        }
    } else {
        return JNI_ERR;
    }

    return  JNI_VERSION_1_6;
}
