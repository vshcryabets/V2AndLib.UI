#include <jni.h>
#include "MP3InputStream.h"
#include "PCMInputStream.h"
#include "AudioStreamException.h"
#include <map>


__attribute__((constructor)) static void onDlOpen(void) {
}

const int S_OK = 1;
const int S_ERR = 0;
const int ERR_NO_SUCH_HANDLER = -2;

using namespace AudioHelpers;

static size_t g_lastHandler = 1;
static std::map<int, PCMInputStream*> g_Handlers;

jint nativeReleaseMP3(jint handler) {
    std::map<int, PCMInputStream*>::iterator iterator = g_Handlers.find(handler);
    if ( iterator == g_Handlers.end() ) {
        return ERR_NO_SUCH_HANDLER;
    }

    if ( iterator->second != NULL ) {
        delete iterator->second;
        g_Handlers.erase(handler);
    }
    return S_OK;
}

jint nativeOpenMP3(JNIEnv *env, jobject clazz, jstring path) {
    try {
        const char *nativeString = env->GetStringUTFChars(path, 0);
        PCMInputStream* fileStream = new MP3InputStream(nativeString);
        env->ReleaseStringUTFChars(path, nativeString);

        int handler = g_lastHandler++;
        g_Handlers.insert(std::pair<int, PCMInputStream*>(handler,fileStream));
        return handler;
    } catch (AudioHelpers::AudioStreamException* err) {
        printf("ERR: %s\n", err->what());
    }
    return S_ERR;
}

jint nativeReadMP3(JNIEnv *env, jobject clazz, jbyteArray buffer, jint offset, jint count, jint handler) {
    std::map<int, PCMInputStream*>::iterator iterator = g_Handlers.find(handler);
    if ( iterator == g_Handlers.end() ) {
            return ERR_NO_SUCH_HANDLER;
    }
    try {
        jboolean isCopy = false;
        jbyte* array = env->GetByteArrayElements(buffer, &isCopy);
        PCMInputStream* fileStream = iterator->second;
        jint read = fileStream->read(array+offset, count);
        env->ReleaseByteArrayElements(buffer, array, 0);
        return read;
    } catch (AudioHelpers::AudioStreamException* err) {
        printf("ERR: %s\n", err->what());
    }
    return -1;
}


const JNINativeMethod method_table[] = {
  { "nativeOpen", "(Ljava/lang/String;)I", (void *) nativeOpenMP3 },
  { "nativeRelease", "(I)I", (void *) nativeReleaseMP3 },
  { "nativeRead", "([BIII)I", (void *) nativeReadMP3 }
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
