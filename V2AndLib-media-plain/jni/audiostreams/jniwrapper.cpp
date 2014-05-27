#include <jni.h>
//#include <android/log.h>
#include "MP3InputStream.h"
#include "PCMInputStream.h"

__attribute__((constructor)) static void onDlOpen(void) {
}

const int S_OK = 0;
const int S_UNINITIALIZED = 100;

static AudioHelpers::PCMInputStream *gFileStream = NULL;

jint nativeRelease() {
    if ( gFileStream != NULL ) {
        delete gFileStream;
        gFileStream = NULL;
    }
}

jint nativeLoad(JNIEnv *env, jobject clazz) { //, jstring path
    nativeRelease();
//    const char *nativeString = env->GetStringUTFChars(path, 0);
//    gFileStream = new AudioHelpers::MP3InputStream(nativeString);
//    env->ReleaseStringUTFChars(path, nativeString);
    return S_OK;
}

static JNINativeMethod method_table[] = {
  { "nativeLoad", "()I;", (void *) nativeLoad } //,
//  { "nativeRelease", "()I", (void *) nativeRelease }
};

static int method_table_size = sizeof(method_table) / sizeof(method_table[0]);

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
  JNIEnv* env;
  if ( vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
    return JNI_ERR;
  } else {
    jclass clazz = env->FindClass( "com/v2soft/AndLib/medianative/MP3DecoderStream");
    if (clazz) {
      jint ret = env->RegisterNatives(clazz, method_table, method_table_size);
      env->DeleteLocalRef(clazz);
      return ret == 0 ? JNI_VERSION_1_6 : JNI_ERR;
    } else {
      return JNI_ERR;
    }
  }
}
