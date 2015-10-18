//
// Created by mrco on 10/18/15.
//

#include "AudioStreamException.h"
#include "OpenSLRecorder.h"
#include <stdlib.h>

void bqRecorderCallback(SLAndroidSimpleBufferQueueItf bq, void *context);

OpenSLRecorder::OpenSLRecorder(AudioHelpers::PCMOutputStream *outputStream) : out(outputStream),
                                                                              slEngineObject(NULL),
                                                                              slRecorderObject(
                                                                                      NULL) {
    // prepare OpenSL
    // create engine
    SLresult result = slCreateEngine(&slEngineObject, 0, NULL, 0, NULL, NULL);
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't create SL engine");
    }

    // realize the engine
    result = (*slEngineObject)->Realize(slEngineObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't realize SL engine");
    }

    // get the engine interface, which is needed in order to create other objects
    result = (*slEngineObject)->GetInterface(slEngineObject, SL_IID_ENGINE, &slEngine);
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't get SL engine interface");
    }

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
    result = (*slEngine)->CreateAudioRecorder(slEngine, &slRecorderObject, &audioSrc,
                                              &audioSnk, 1, id, req);
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't create AudioRecorder");
    }

    // realize the audio recorder
    result = (*slRecorderObject)->Realize(slRecorderObject, SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't realize AudioRecorder");
    }

    // get the record interface
    result = (*slRecorderObject)->GetInterface(slRecorderObject, SL_IID_RECORD, &slRecorder);
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't get AudioRecorder interface");
    }

    // get the buffer queue interface
    result = (*slRecorderObject)->GetInterface(slRecorderObject, SL_IID_ANDROIDSIMPLEBUFFERQUEUE,
                                               &slRecorderBufferQueue);
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't get record buffer queue");
    }

    // register callback on the buffer queue
    result = (*slRecorderBufferQueue)->RegisterCallback(slRecorderBufferQueue,
                                                        bqRecorderCallback, this);
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't register record buffer queue callback");
    }

    bufferSize = getBufferSize() * sizeof(short);
    buffer1 = (short *) malloc(bufferSize);
    buffer2 = (short *) malloc(bufferSize);
}

OpenSLRecorder::~OpenSLRecorder() {
    if (buffer1 != NULL) {
        free(buffer1);
        buffer1 = NULL;
    }
    if (buffer2 != NULL) {
        free(buffer2);
        buffer2 = NULL;
    }

    // destroy audio recorder object, and invalidate all associated interfaces
    if (slRecorderObject != NULL) {
        (*slRecorderObject)->Destroy(slRecorderObject);
        slRecorderObject = NULL;
        slRecorder = NULL;
        slRecorderBufferQueue = NULL;
    }

    // destroy engine object, and invalidate all associated interfaces
    if (slEngineObject != NULL) {
        (*slEngineObject)->Destroy(slEngineObject);
        slEngineObject = NULL;
        slEngine = NULL;
    }
}

void OpenSLRecorder::start() {
    SLresult result;

    // in case already recording, stop recording and clear buffer queue
    result = (*slRecorder)->SetRecordState(slRecorder, SL_RECORDSTATE_STOPPED);
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't stop recorder before start()");
    }
    result = (*slRecorderBufferQueue)->Clear(slRecorderBufferQueue);
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't clear buffer queue");
    }

    // enqueue an empty buffer to be filled by the recorder
    // (for streaming recording, we would enqueue at least 2 empty buffers to start things off)
    result = (*slRecorderBufferQueue)->Enqueue(slRecorderBufferQueue, buffer1, bufferSize);
    // the most likely other result is SL_RESULT_BUFFER_INSUFFICIENT,
    // which for this code example would indicate a programming error
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't enquque buffer");
    }

    // start recording
    result = (*slRecorder)->SetRecordState(slRecorder, SL_RECORDSTATE_RECORDING);
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't set recording state");
    }
}

void OpenSLRecorder::stop() {
    SLresult result;
    // start recording
    result = (*slRecorder)->SetRecordState(slRecorder, SL_RECORDSTATE_STOPPED);
    if (SL_RESULT_SUCCESS != result) {
        throw new AudioHelpers::AudioStreamException("Can't stop recorder");
    }
}

// this callback handler is called every time a buffer finishes recording
void bqRecorderCallback(SLAndroidSimpleBufferQueueItf bq, void *context) {
    (static_cast<OpenSLRecorder *>(context))->processSLCallback(bq);
}

size_t OpenSLRecorder::getBufferSize() {
    return 1024 * 16;
}

void OpenSLRecorder::processSLCallback(SLAndroidSimpleBufferQueueItf pItf_) {
    SLresult result;
    result = (*slRecorderBufferQueue)->Enqueue(slRecorderBufferQueue, buffer2, bufferSize);
    short *tmp = buffer1;
    out->write(tmp, bufferSize);
    buffer1 = buffer2;
    buffer2 = tmp;
}
