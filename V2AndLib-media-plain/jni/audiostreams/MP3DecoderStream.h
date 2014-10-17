#ifndef MP3_DECODER_STREAM_H
#define MP3_DECODER_STREAM_H

#include <stdio.h>
#include "PCMOutputStream.h"
#include "mpg123.h"

namespace AudioHelpers {
class MP3DecoderStream : public PCMOutputStream {
protected:
    PCMOutputStream* mOutput;
    static const char* TAG;
    mpg123_handle * mHandle;
    long mSampleRate;
    int mChannels;
    int mEncoding;

    void init();
public:
    MP3DecoderStream(PCMOutputStream* output, size_t maxBuffer);
    virtual ~MP3DecoderStream();
    virtual PCMOutputStream* getSubStream();

    virtual size_t write(void* buffer, size_t count);
    virtual void flush();
    virtual void close();
    virtual void setInputSampleRate(size_t samplerate);
    virtual void setInputChannelsCount(size_t channelsCount);
    virtual void setOutputSampleRate(size_t samplerate);
    virtual void setOutputChannelsCount(size_t channelsCount);
};
}
#endif // MP3_DECODER_STREAM_H
