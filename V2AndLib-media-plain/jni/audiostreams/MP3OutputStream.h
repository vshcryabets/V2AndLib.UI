#ifndef MP3_OUTPUT_STREAM_H
#define MP3_OUTPUT_STREAM_H

#include <stdio.h>
#include "PCMOutputStream.h"
#include "lame.h"

namespace AudioHelpers {
class MP3OutputStream : public PCMOutputStream {
protected:
    static const char* TAG;
    PCMOutputStream* mOutput;
    lame_t  mLameHandler;

    void checkHandle();
public:
    MP3OutputStream(PCMOutputStream* outstream);
    virtual ~MP3OutputStream();
    virtual void write(void* buffer, size_t count);
    virtual void configure(size_t channelsCount, size_t samplerate);

    virtual void flush();
    virtual void close();
    virtual void setInputSampleRate(size_t samplerate);
    virtual void setInputChannelsCount(size_t channelsCount);
    virtual void setOutputSampleRate(size_t samplerate);
    virtual void setOutputChannelsCount(size_t channelsCount);

};

}

#endif // MP3_OUTPUT_STREAM_H
