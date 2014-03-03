#include <jni.h>

#ifndef _Included_LameWrapper
#define _Included_LameWrapper

const int ERR_NO_SUCH_HANDLER = -1012;
const int ERR_SMALL_OUTPUT_BUFFER = -1013;
const int ERR_OPPERATION_SUCCESS = -1014;
const int ERR_WRONG_INPUT_BUFFER = -1015;
const int ERR_NULL_BUFFER = -1016;
const int ERR_LAME_ERROR = -1017;

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jstring JNICALL Java_com_v2soft_AndLib_media_MP3Helper_getVersion(JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_v2soft_AndLib_media_MP3Helper_allocateEncoderNative(JNIEnv *env, jclass c,
        jint numberOfChannels, jint inputSampleRate, jint outputSampleRate, jint mode);

JNIEXPORT jint JNICALL Java_com_v2soft_AndLib_media_MP3Helper_lameEncodeBufferNative(JNIEnv * env, jclass c,
        jbyteArray jInputBuffer, jbyteArray bufferOutput, jint handler);
JNIEXPORT jint JNICALL Java_com_v2soft_AndLib_media_MP3Helper_finishNative(JNIEnv * env, jclass c,
        jint handler);

#ifdef __cplusplus
}
#endif
#endif
