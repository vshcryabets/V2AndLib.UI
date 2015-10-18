//
// Created by mrco on 10/18/15.
//

#ifndef STARSKY_OPENSLRECORDER_H
#define STARSKY_OPENSLRECORDER_H

#include "PCMOutputStream.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

class OpenSLRecorder {
private:
    size_t bufferSize;
    AudioHelpers::PCMOutputStream *out;

    SLObjectItf slEngineObject;
    SLEngineItf slEngine;
    SLObjectItf slRecorderObject;
    SLRecordItf slRecorder;
    SLAndroidSimpleBufferQueueItf slRecorderBufferQueue;
    short *buffer1, *buffer2;
public:
    OpenSLRecorder(AudioHelpers::PCMOutputStream *out);
    ~OpenSLRecorder();
    void start();
    void stop();
    size_t getBufferSize();
    void processSLCallback(SLAndroidSimpleBufferQueueItf pItf_);
};


#endif //STARSKY_OPENSLRECORDER_H
