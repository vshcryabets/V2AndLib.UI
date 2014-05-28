#include <jni.h>

#ifndef _Included_JpegWrapper
#define _Included_JpegWrapper

const int ERR_NO_SUCH_HANDLER = -1012;
const int ERR_SMALL_OUTPUT_BUFFER = -1013;
const int ERR_OPPERATION_SUCCESS = -1014;
const int ERR_WRONG_INPUT_BUFFER = -1015;
const int ERR_NULL_BUFFER = -1016;
const int ERR_LAME_ERROR = -1017;

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jstring JNICALL Java_com_v2soft_AndLib_media_JPEGHelper_getVersion(JNIEnv *, jclass);
JNIEXPORT jobject JNICALL Java_com_v2soft_AndLib_media_JPEGHelper_getJPEGInfo(JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif // _Included_JpegWrapperß
