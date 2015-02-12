#ifndef _Included_JpegWrapper
#define _Included_JpegWrapper
#include <jni.h>

JNIEXPORT jstring JNICALL nativeGetVersion(JNIEnv * env, jclass c);
JNIEXPORT jint JNICALL nativeGetJPEGInfo(JNIEnv* env, jclass c, jstring jniFileName, jobject result);
JNIEXPORT jint JNICALL nativeCropJPEG(JNIEnv* env, jclass c, jstring input, jintArray cropArea,
    jstring output, jint quality);
JNIEXPORT jbyteArray JNICALL nativeLoadJPEG(JNIEnv* env, jclass c, jstring input, jintArray cropArea);
JNIEXPORT jint JNICALL nativeSaveJPEG(JNIEnv* env, jclass c, jstring output, jbyteArray data,
    jint width, jint height, jint quality);

const JNINativeMethod method_jpeghelper_table[] = {
  { "nativeGetVersion", "()Ljava/lang/String;", (void*) nativeGetVersion},
  { "nativeGetJPEGInfo", "(Ljava/lang/String;Lcom/v2soft/AndLib/medianative/JPEGOptions;)I", (void*) nativeGetJPEGInfo},
  { "nativeCropJPEG", "(Ljava/lang/String;[ILjava/lang/String;I)I", (void*) nativeCropJPEG},
  { "nativeLoadJPEG", "(Ljava/lang/String;[I)[B", (void*) nativeLoadJPEG},
  { "nativeSaveJPEG", "(Ljava/lang/String;[BIII)I", (void*) nativeSaveJPEG},
};

static int method_jpeghelper_table_size = sizeof(method_jpeghelper_table) / sizeof(method_jpeghelper_table[0]);

#endif // _Included_JpegWrapper

