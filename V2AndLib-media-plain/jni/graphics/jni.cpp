#include "jni.h"

const char* TAG = "jpegwrapper";

__attribute__((constructor)) static void onDlOpen(void) {
}

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
