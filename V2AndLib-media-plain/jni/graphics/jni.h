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

#endif // _Included_JpegWrapper

