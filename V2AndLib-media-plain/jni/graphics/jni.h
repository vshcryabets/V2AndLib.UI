#ifndef _Included_JpegWrapper
#define _Included_JpegWrapper
#include <jni.h>

const int ERR_OK = 0;
const int ERR_NO_SUCH_HANDLER = -1012;
const int ERR_SMALL_OUTPUT_BUFFER = -1013;
const int ERR_OPPERATION_SUCCESS = -1014;
const int ERR_WRONG_INPUT_BUFFER = -1015;
const int ERR_NULL_BUFFER = -1016;
const int ERR_CANT_GET_RESULT_CLASS = -1017;
const int ERR_NO_FILE = -1018;
const int ERR_JPEG_DECODER = -1019;
const int ERR_CANT_CREATE_FILE = -1020;
const int ERR_INCORRECT_GEOMETRY_PARAMETER = -1021;

JNIEXPORT jstring JNICALL nativeGetVersion(JNIEnv * env, jclass c);
JNIEXPORT jint JNICALL nativeGetJPEGInfo(JNIEnv* env, jclass c, jstring jniFileName, jobject result);
JNIEXPORT jint JNICALL nativeCropJPEG(JNIEnv* env, jclass c, jstring input, jintArray cropArea, jstring output);

const JNINativeMethod method_jpeghelper_table[] = {
  { "nativeGetVersion", "()Ljava/lang/String;", (void*) nativeGetVersion},
  { "nativeGetJPEGInfo", "(Ljava/lang/String;Lcom/v2soft/AndLib/medianative/JPEGOptions;)I", (void*) nativeGetJPEGInfo},
  { "nativeCropJPEG", "(Ljava/lang/String;[ILjava/lang/String;)I", (void*) nativeCropJPEG},
};

static int method_jpeghelper_table_size = sizeof(method_jpeghelper_table) / sizeof(method_jpeghelper_table[0]);

#endif // _Included_JpegWrapper
