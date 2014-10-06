#ifndef OUT_STREAMS_H
#define OUT_STREAMS_H

#include <stdio.h>

namespace AudioHelpers {

class PCMOutputStream {
public:
    PCMOutputStream() {}
    virtual ~PCMOutputStream() {};
    virtual size_t write(void* buffer, size_t count) = 0;
    virtual void flush() = 0;
    virtual void close() = 0;
    virtual void setInputSampleRate(size_t samplerate) = 0;
    virtual void setInputChannelsCount(size_t channelsCount) = 0;
    virtual void setOutputSampleRate(size_t samplerate) = 0;
    virtual void setOutputChannelsCount(size_t channelsCount) = 0;
};

}

#endif // OUT_STREAMS_H
