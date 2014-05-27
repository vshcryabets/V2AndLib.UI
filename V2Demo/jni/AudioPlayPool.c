#include <assert.h>
#include <jni.h>
#include <string.h>
#include "math.h"

#include <android/log.h>
#define  LOG_TAG    "libplayer"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

// for native audio
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

// for native asset manager
#include <sys/types.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

// engine interfaces
static SLObjectItf engineObject = NULL;
static SLEngineItf engineEngine;

// output mix interfaces
static SLObjectItf gOutputMixObject = NULL;

// buffer queue player interfaces
#define sPlayerCounts 4
static SLObjectItf gPlayerObjects[sPlayerCounts];
static SLPlayItf gPlayers[sPlayerCounts];
static SLAndroidSimpleBufferQueueItf gPlayerQueues[sPlayerCounts];

// synthesized sawtooth clip
#define SAWTOOTH_FRAMES 16000
static short sawtoothBuffers[sPlayerCounts][SAWTOOTH_FRAMES];

// synthesize a mono sawtooth wave and place it into a buffer (called automatically on load)
__attribute__((constructor)) static void onDlOpen(void) {
	for (size_t i = 0; i < SAWTOOTH_FRAMES; ++i) {
		sawtoothBuffers[0][i] = (short)(30000*sin(((double)i*0.8)));
		sawtoothBuffers[1][i] = (short)(30000*sin(((double)i*0.6)));
		sawtoothBuffers[2][i] = (short)(30000*sin(((double)i*0.5)));
		sawtoothBuffers[3][i] = (short)(30000*sin(((double)i*0.4)));
	}
	for (size_t i = 0; i < 20; ++i) {
		LOGI("%d ", sawtoothBuffers[0][i]);
	}
}

// this callback handler is called every time a buffer finishes playing
void bqPlayerCallback(SLAndroidSimpleBufferQueueItf bq, void *context) {
	assert(bq == bqPlayerBufferQueue);
	assert(NULL == context);
	//	// for streaming playback, replace this test by logic to find and fill the next buffer
	//	if (--nextCount > 0 && NULL != nextBuffer && 0 != nextSize) {
	//		SLresult result;
	//		// enqueue another buffer
	//		result = (*bqPlayerBufferQueue)->Enqueue(bqPlayerBufferQueue, nextBuffer, nextSize);
	//		// the most likely other result is SL_RESULT_BUFFER_INSUFFICIENT,
	//		// which for this code example would indicate a programming error
	//		assert(SL_RESULT_SUCCESS == result);
	//	}
}

// create the engine and output mix objects
void Java_com_v2soft_V2AndLib_demoapp_ui_activities_OpenSLSample_createPlayerEngine(
		JNIEnv* env, jclass clazz) {
	// create engine
	SLresult result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
	assert(SL_RESULT_SUCCESS == result);

	// realize the engine
	result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
	assert(SL_RESULT_SUCCESS == result);

	// get the engine interface, which is needed in order to create other objects
	result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
	assert(SL_RESULT_SUCCESS == result);

	// create output mix, with environmental reverb specified as a non-required interface
	const SLInterfaceID ids[1] = {SL_IID_ENVIRONMENTALREVERB};
	const SLboolean req[1] = {SL_BOOLEAN_FALSE};
	result = (*engineEngine)->CreateOutputMix(engineEngine, &gOutputMixObject, 1, ids, req);
	assert(SL_RESULT_SUCCESS == result);

	// realize the output mix
	result = (*gOutputMixObject)->Realize(gOutputMixObject, SL_BOOLEAN_FALSE);
	assert(SL_RESULT_SUCCESS == result);
}


// create buffer queue audio player
void Java_com_v2soft_V2AndLib_demoapp_ui_activities_OpenSLSample_createPlayers(JNIEnv* env,
		jclass clazz)
{
	SLresult result;

	// configure audio source
	SLDataLocator_AndroidSimpleBufferQueue loc_bufq =
	{SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};

	SLDataFormat_PCM format_pcm;
    format_pcm.formatType = SL_DATAFORMAT_PCM;
    format_pcm.numChannels = 1;
    format_pcm.samplesPerSec = SL_SAMPLINGRATE_48; //mHz
    format_pcm.bitsPerSample = SL_PCMSAMPLEFORMAT_FIXED_16;
    format_pcm.containerSize = SL_PCMSAMPLEFORMAT_FIXED_16;
    format_pcm.channelMask = SL_SPEAKER_FRONT_CENTER;
    format_pcm.endianness = SL_BYTEORDER_LITTLEENDIAN;

	SLDataSource audioSrc = {&loc_bufq, &format_pcm};

	// configure audio sink
	SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, gOutputMixObject};
	SLDataSink audioSnk = {&loc_outmix, NULL};
	const SLInterfaceID ids[1] = {SL_IID_BUFFERQUEUE};
	const SLboolean req[1] = {SL_BOOLEAN_TRUE};

	for ( size_t i = 0 ; i < sPlayerCounts; i++ ) {
		// create audio player
		result = (*engineEngine)->CreateAudioPlayer(engineEngine, &gPlayerObjects[i],
				&audioSrc, &audioSnk,
				1, ids, req);
		assert(SL_RESULT_SUCCESS == result);

		// realize the player
		result = (*gPlayerObjects[i])->Realize(gPlayerObjects[i], SL_BOOLEAN_FALSE);
		assert(SL_RESULT_SUCCESS == result);

		// get the play interface
		result = (*gPlayerObjects[i])->GetInterface(gPlayerObjects[i], SL_IID_PLAY,
				&gPlayers[i]);
		assert(SL_RESULT_SUCCESS == result);

		// get the buffer queue interface
		result = (*gPlayerObjects[i])->GetInterface(gPlayerObjects[i], SL_IID_BUFFERQUEUE,
				&gPlayerQueues[i]);
		assert(SL_RESULT_SUCCESS == result);

		// register callback on the buffer queue
		result = (*gPlayerQueues[i])->RegisterCallback(gPlayerQueues[i],
				bqPlayerCallback, NULL);
		assert(SL_RESULT_SUCCESS == result);
	}
}

void Java_com_v2soft_V2AndLib_demoapp_ui_activities_OpenSLSample_stopPlayer(JNIEnv* env,
		jclass clazz, jint idx) {
	if ( idx < 0 || idx >= sPlayerCounts ) {
		return;
	}
	// set the player's state to playing
	SLresult result = (*gPlayers[idx])->SetPlayState(gPlayers[idx], SL_PLAYSTATE_STOPPED);
	assert(SL_RESULT_SUCCESS == result);
}

// shut down the native audio system
void Java_com_v2soft_V2AndLib_demoapp_ui_activities_OpenSLSample_shutdownPlayers(
		JNIEnv* env, jclass clazz) {
	LOGI("Shutdown player");
	for ( size_t i = 0 ; i < sPlayerCounts; i++ ) {
		if ( gPlayerObjects != NULL ) {
			(*gPlayerObjects[i])->Destroy(gPlayerObjects[i]);
			gPlayerObjects[i] = NULL;
		}
	}

	// destroy output mix object, and invalidate all associated interfaces
	if (gOutputMixObject != NULL) {
		(*gOutputMixObject)->Destroy(gOutputMixObject);
		gOutputMixObject = NULL;
	}

	// destroy engine object, and invalidate all associated interfaces
	if (engineObject != NULL) {
		(*engineObject)->Destroy(engineObject);
		engineObject = NULL;
		engineEngine = NULL;
	}
}

jboolean Java_com_v2soft_V2AndLib_demoapp_ui_activities_OpenSLSample_startPlayer(
		JNIEnv* env, jclass clazz, int idx) {
	if ( idx < 0 || idx >= sPlayerCounts ) {
		return 0;
	}
	SLresult result = (*gPlayers[idx])->SetPlayState(gPlayers[idx], SL_PLAYSTATE_PLAYING);
	assert(SL_RESULT_SUCCESS == result);
	// here we only enqueue one buffer because it is a long clip,
	// but for streaming playback we would typically enqueue at least 2 buffers to start
	result = (*gPlayerQueues[idx])->Enqueue(gPlayerQueues[idx],
			sawtoothBuffers[idx], SAWTOOTH_FRAMES);
	return (SL_RESULT_SUCCESS == result);
}
