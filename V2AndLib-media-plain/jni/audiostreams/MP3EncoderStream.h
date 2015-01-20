#ifndef MP3_ENCODER_STREAM_H
#define MP3_ENCODER_STREAM_H

#include <stdio.h>
#include "PCMOutputStream.h"
#include "lame.h"

namespace AudioHelpers {
class MP3EncoderStream : public PCMOutputStream {
protected:
    static const char* TAG;
    PCMOutputStream* mOutput;
    lame_t  mLameHandler;
    char*   mEncodedBuffer;
    size_t mChannelsCount;
    size_t mMaxInputBuffer;
    size_t mMaxOutputBuffer;

    void checkHandle();
    size_t getEncodedBufferSize();
    size_t getInputBufferSize();
public:
    MP3EncoderStream(PCMOutputStream* outstream, size_t maxBuffer);
    virtual ~MP3EncoderStream();
    virtual size_t write(void* buffer, size_t count);
    virtual void configure(size_t channelsCount, size_t samplerate, size_t outSampleRate, MPEG_mode encodingMode);
    virtual void flush();
    virtual void close();
    virtual void setInputSampleRate(size_t samplerate);
    virtual void setInputChannelsCount(size_t channelsCount);
    virtual void setOutputSampleRate(size_t samplerate);
    virtual void setOutputChannelsCount(size_t channelsCount);
    virtual PCMOutputStream* getSubStream();
};
}

#endif // MP3_ENCODER_STREAM_H