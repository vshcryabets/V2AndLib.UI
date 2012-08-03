/*
 * Copyright (C) 2012 V.Shcryabets (vshcryabets@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include <assert.h>
#include <jni.h>
#include <string.h>
#include "android/log.h"
// for native audio
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

#define  LOG_TAG    "libna"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

// engine interfaces
static SLObjectItf engineObject = NULL;
static SLEngineItf engineEngine;

// recorder interfaces
static SLObjectItf recorderObject = NULL;
static SLRecordItf recorderRecord;
static SLAndroidSimpleBufferQueueItf recorderBufferQueue;

// 5 seconds of recorded audio at 16 kHz mono, 16-bit signed little endian
#define RECORDER_FRAMES (16000 * 5)
static short recorderBuffer[RECORDER_FRAMES];
static short recorderBuffer2[RECORDER_FRAMES];
static short recorderBuffer3[RECORDER_FRAMES];
static unsigned recorderSize = 0;
static SLmilliHertz recorderSR;

int mBufferCount;

// synthesize a mono sawtooth wave and place it into a buffer (called automatically on load)
__attribute__((constructor)) static void onDlOpen(void) {
}

// this callback handler is called every time a buffer finishes recording
void bqRecorderCallback(SLAndroidSimpleBufferQueueItf bq, void *context) {
	assert(bq == bqRecorderBufferQueue);
	assert(NULL == context);

	// for streaming recording, here we would call Enqueue to give recorder the next buffer to fill
	// but instead, this is a one-time buffer so we stop recording
	SLresult result;
	if ( mBufferCount < 1 ) {
		result = (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recorderBuffer2,
				RECORDER_FRAMES * sizeof(short));
		LOGI("3 recorded %d", recorderSize);
	} else {
		result = (*recorderRecord)->SetRecordState(recorderRecord, SL_RECORDSTATE_STOPPED);
		if (SL_RESULT_SUCCESS == result) {
			recorderSize = RECORDER_FRAMES * sizeof(short);
			recorderSR = SL_SAMPLINGRATE_16;
			LOGI("2 recorded %d", recorderSize);
		}
	}
	LOGI("recorded %d", recorderSize);
	mBufferCount++;
}

// create the engine and output mix objects
void Java_com_v2soft_V2AndLib_demoapp_ui_activities_OpenSLSample_createEngine(JNIEnv* env, jclass clazz) {
	// create engine
	SLresult result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
	assert(SL_RESULT_SUCCESS == result);

	// realize the engine
	result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
	assert(SL_RESULT_SUCCESS == result);

	// get the engine interface, which is needed in order to create other objects
	result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
	assert(SL_RESULT_SUCCESS == result);
}

// create audio recorder
jboolean Java_com_v2soft_V2AndLib_demoapp_ui_activities_OpenSLSample_createAudioRecorder(JNIEnv* env, jclass clazz)
{
	SLresult result;

	// configure audio source
	SLDataLocator_IODevice loc_dev = {SL_DATALOCATOR_IODEVICE, SL_IODEVICE_AUDIOINPUT,
			SL_DEFAULTDEVICEID_AUDIOINPUT, NULL};
	SLDataSource audioSrc = {&loc_dev, NULL};

	// configure audio sink
	SLDataLocator_AndroidSimpleBufferQueue loc_bq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
	SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM, 1, SL_SAMPLINGRATE_16,
			SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16,
			SL_SPEAKER_FRONT_CENTER, SL_BYTEORDER_LITTLEENDIAN};
	SLDataSink audioSnk = {&loc_bq, &format_pcm};

	// create audio recorder
	// (requires the RECORD_AUDIO permission)
	const SLInterfaceID id[1] = {SL_IID_ANDROIDSIMPLEBUFFERQUEUE};
	const SLboolean req[1] = {SL_BOOLEAN_TRUE};
	result = (*engineEngine)->CreateAudioRecorder(engineEngine, &recorderObject, &audioSrc,
			&audioSnk, 1, id, req);
	if (SL_RESULT_SUCCESS != result) {
		return JNI_FALSE;
	}

	// realize the audio recorder
	result = (*recorderObject)->Realize(recorderObject, SL_BOOLEAN_FALSE);
	if (SL_RESULT_SUCCESS != result) {
		return JNI_FALSE;
	}

	// get the record interface
	result = (*recorderObject)->GetInterface(recorderObject, SL_IID_RECORD, &recorderRecord);
	assert(SL_RESULT_SUCCESS == result);

	// get the buffer queue interface
	result = (*recorderObject)->GetInterface(recorderObject, SL_IID_ANDROIDSIMPLEBUFFERQUEUE,
			&recorderBufferQueue);
	assert(SL_RESULT_SUCCESS == result);

	// register callback on the buffer queue
	result = (*recorderBufferQueue)->RegisterCallback(recorderBufferQueue, bqRecorderCallback,
			NULL);
	assert(SL_RESULT_SUCCESS == result);

	return JNI_TRUE;
}


// set the recording state for the audio recorder
void Java_com_v2soft_V2AndLib_demoapp_ui_activities_OpenSLSample_startRecording(JNIEnv* env, jclass clazz)
{
	mBufferCount = 0;
	SLresult result;

	// in case already recording, stop recording and clear buffer queue
	result = (*recorderRecord)->SetRecordState(recorderRecord, SL_RECORDSTATE_STOPPED);
	assert(SL_RESULT_SUCCESS == result);
	result = (*recorderBufferQueue)->Clear(recorderBufferQueue);
	assert(SL_RESULT_SUCCESS == result);

	// the buffer is not valid for playback yet
	recorderSize = 0;

	// enqueue an empty buffer to be filled by the recorder
	// (for streaming recording, we would enqueue at least 2 empty buffers to start things off)
	result = (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recorderBuffer,
			RECORDER_FRAMES * sizeof(short));
	// the most likely other result is SL_RESULT_BUFFER_INSUFFICIENT,
	// which for this code example would indicate a programming error
	assert(SL_RESULT_SUCCESS == result);

	// start recording
	result = (*recorderRecord)->SetRecordState(recorderRecord, SL_RECORDSTATE_RECORDING);
	assert(SL_RESULT_SUCCESS == result);
}

// shut down the native audio system
void Java_com_v2soft_V2AndLib_demoapp_ui_activities_OpenSLSample_shutdown(JNIEnv* env, jclass clazz)
{

	// destroy audio recorder object, and invalidate all associated interfaces
	if (recorderObject != NULL) {
		(*recorderObject)->Destroy(recorderObject);
		recorderObject = NULL;
		recorderRecord = NULL;
		recorderBufferQueue = NULL;
	}

	// destroy engine object, and invalidate all associated interfaces
	if (engineObject != NULL) {
		(*engineObject)->Destroy(engineObject);
		engineObject = NULL;
		engineEngine = NULL;
	}

}
