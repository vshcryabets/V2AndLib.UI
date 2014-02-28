#include "lame.h"
#include <android/log.h>
#include "LameWrapper.h"
#include "lame_global_flags.h"
#include <map>

#define mp3buf_size 8192//1.25 * nsamples + 7200;
#define count_to_read_pcm 8192
#define count_to_read_mp3 2048

lame_global_flags *lgf;
unsigned char *mp3buf;
unsigned int g_lastHandler = 1;

JNIEXPORT jstring JNICALL Java_com_v2soft_AndLib_media_MP3Helper_getVersion(JNIEnv * env, jclass c) {
    return env->NewStringUTF(get_lame_version());
}

JNIEXPORT jint JNICALL Java_com_v2soft_AndLib_media_MP3Helper_allocateEncoderNative(JNIEnv * env, jclass c) {
	lgf = lame_init();
	if (lame_init_params(lgf) == -1) {
		__android_log_print(ANDROID_LOG_ERROR, "LameWrapper[Native]", "Lame init params failed");
		return 0;
	}
	mp3buf = (unsigned char *) malloc(mp3buf_size);
	return 1;
}

JNIEXPORT jint JNICALL Java_com_v2soft_AndLib_media_MP3Helper_lameEncodeBufferNative
  (JNIEnv * env, jclass c, jobject buffer_l, jobject buffer_r, jint nsamples)
{
	short int *buf_l = (short int*)env->GetDirectBufferAddress(buffer_l);
	short int *buf_r = (short int*)env->GetDirectBufferAddress(buffer_r);
	(*lgf).num_channels=1;
	(*lgf).mode = STEREO;
	int res = lame_encode_buffer(lgf, buf_l, buf_r, nsamples, mp3buf, mp3buf_size);
//	__android_log_print(ANDROID_LOG_INFO, "LameWrapper[Native]", "POINTER AFTER: %p - %d ", buf_l, nsamples);
//	fwrite(buf_l, sizeof(short int), nsamples, file);			

	if (res != 0)
	{			
	} else __android_log_print(ANDROID_LOG_ERROR, "LameWrapper[Native]", "Encoded buffer size is 0");
	    
	return res;
}

JNIEXPORT jint JNICALL Java_air_babbler_sound_LameWrapper_finishNative(JNIEnv * env, jclass c) {
	free(mp3buf);
	lame_close(lgf);
	return 0;
}

