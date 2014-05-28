#include <jni.h>
//#include <android/log.h>
#include "MP3InputStream.h"
#include "PCMInputStream.h"
#include "PCMInputStreamException.h"

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

static JNINativeMethod method_table[] = {
  { "nativeLoad", "(Ljava/lang/String;)I", (void *) nativeLoad },
  { "nativeRelease", "()I", (void *) nativeRelease }
};

static int method_table_size = sizeof(method_table) / sizeof(method_table[0]);

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
  JNIEnv* env;
  if ( vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
    return JNI_ERR;
  } else {
    jclass clazz = env->FindClass( "com/v2soft/AndLib/medianative/MP3DecoderStream");
    if (clazz) {

        // Now get the method id for function 'newInstance()'
//        env->GetMethodID(clazz,
//                                                    "nativeLoad",
//                                                    "()V;");
//      printf("clazz not NULL\n");

//      jobject strObj = env->AllocObject(clazz);
//      jmethodID midGetClass = env->GetMethodID(clazz, "getClass", "()Ljava/lang/Class;");
//      jobject clsObj = env->CallObjectMethod(strObj, midGetClass);
//      jclass jCls = env->GetObjectClass(clsObj);
//      jmethodID midGetFields = env->GetMethodID(jCls, "getMethods", "()[Ljava/lang/reflect/Method;");
//      jobjectArray jobjArray = (jobjectArray)env->CallObjectMethod(clsObj, midGetFields);
//      jsize len = env->GetArrayLength(jobjArray);
//      jsize i;
//
//      for (i = 0 ; i < len ; i++) {
//          jobject _strMethod = env->GetObjectArrayElement(jobjArray , i) ;
//          jclass _methodClazz = env->GetObjectClass(_strMethod) ;
//          jmethodID mid = env->GetMethodID(_methodClazz , "getName" , "()Ljava/lang/String;") ;
//          jstring _name = (jstring)env->CallObjectMethod(_strMethod , mid ) ;
//          const char *str = env->GetStringUTFChars(_name, 0);
//
//          printf("\n%s", str);
//          env->ReleaseStringUTFChars(_name, str);
//      }

      jint ret = env->RegisterNatives(clazz, method_table, method_table_size);
//      printf("\nRet=%d\n", ret);
      env->DeleteLocalRef(clazz);
      return ret == 0 ? JNI_VERSION_1_6 : JNI_ERR;
    } else {
      return JNI_ERR;
    }
  }
}
