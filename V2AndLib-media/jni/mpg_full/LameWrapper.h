#include <jni.h>

#ifndef _Included_LameWrapper
#define _Included_LameWrapper
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_v2soft_AndLib_media_MP3Helper
 * Method:    getVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_v2soft_AndLib_media_MP3Helper_getVersion(JNIEnv *, jclass);

/*
 * Class:     com_v2soft_AndLib_media_MP3Helper
 * Method:    initNative
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_v2soft_AndLib_media_MP3Helper_initEncoder(JNIEnv *, jclass);

/*
 * Class:     com_v2soft_AndLib_media_MP3Helper
 * Method:    finishNative
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_air_babbler_sound_LameWrapper_finishNative(JNIEnv *, jclass);



#ifdef __cplusplus
}
#endif
#endif
