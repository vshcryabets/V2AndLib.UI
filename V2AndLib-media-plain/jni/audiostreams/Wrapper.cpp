#include "Wrapper.h"

__attribute__((constructor)) static void onDlOpen(void) {
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if ( vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    jclass clazz = env->FindClass( "com/v2soft/AndLib/medianative/MP3DecoderStream");
    if (clazz) {
        jint ret = env->RegisterNatives(clazz, method_decoder_table, method_decoder_table_size);
        env->DeleteLocalRef(clazz);
        if (  ret != 0 ) {
            return JNI_ERR;
        }
    } else {
        return JNI_ERR;
    }

    clazz = env->FindClass( "com/v2soft/AndLib/medianative/MP3EncoderStream");
    if (clazz) {
        jint ret = env->RegisterNatives(clazz, method_encoder_table, method_encoder_table_size);
        env->DeleteLocalRef(clazz);
        if (  ret != 0 ) {
            return JNI_ERR;
        }
    } else {
        return JNI_ERR;
    }

    return  JNI_VERSION_1_6;
}
