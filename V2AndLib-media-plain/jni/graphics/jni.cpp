#include "jni.h"

const char* TAG = "jpegwrapper";

const JNINativeMethod method_jpeghelper_table[] = {
  { "nativeGetVersion", "()Ljava/lang/String;", (void*) nativeGetVersion},
  { "nativeGetJPEGInfo", "(Ljava/lang/String;Lcom/v2soft/AndLib/medianative/JPEGOptions;)I", (void*) nativeGetJPEGInfo},
  { "nativeCropJPEG", "(Ljava/lang/String;[ILjava/lang/String;I)I", (void*) nativeCropJPEG},
  { "nativeLoadJPEG", "(Ljava/lang/String;[I)[B", (void*) nativeLoadJPEG},
  { "nativeSaveJPEG", "(Ljava/lang/String;[BIII)I", (void*) nativeSaveJPEG},
};

static int method_jpeghelper_table_size = sizeof(method_jpeghelper_table) / sizeof(method_jpeghelper_table[0]);



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
