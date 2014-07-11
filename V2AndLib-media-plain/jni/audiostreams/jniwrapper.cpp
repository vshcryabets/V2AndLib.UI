#include <jni.h>
#include "MP3InputStream.h"
#include "PCMInputStream.h"
#include "PCMInputStreamException.h"

__attribute__((constructor)) static void onDlOpen(void) {
}

const int S_OK = 0;

static AudioHelpers::PCMInputStream *gFileStream = NULL;

jint nativeRelease() {
    if ( gFileStream != NULL ) {
        delete gFileStream;
        gFileStream = NULL;
    }
    return S_OK;
}

jint nativeLoad(JNIEnv *env, jobject clazz, jstring path) {
    try {
        nativeRelease();
        const char *nativeString = env->GetStringUTFChars(path, 0);
        gFileStream = new AudioHelpers::MP3InputStream(nativeString);
        env->ReleaseStringUTFChars(path, nativeString);
    } catch (AudioHelpers::PCMInputStreamException* err) {
        printf("ERR: %s\n", err->what());
    }
    return S_OK;
}

jint nativeRead(JNIEnv *env, jobject clazz, jbyteArray buffer, jint offset, jint count) {
    try {
        jboolean isCopy = false;
        jbyte* array = env->GetByteArrayElements(buffer, &isCopy);
        jint read = gFileStream->read(array+offset, count);
        env->ReleaseByteArrayElements(buffer, array, 0);
        return read;
    } catch (AudioHelpers::PCMInputStreamException* err) {
        printf("ERR: %s\n", err->what());
    }
    return -1;
}


static JNINativeMethod method_table[] = {
  { "nativeLoad", "(Ljava/lang/String;)I", (void *) nativeLoad },
  { "nativeRelease", "()I", (void *) nativeRelease },
  { "nativeRead", "([BII)I", (void *) nativeRead }
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
