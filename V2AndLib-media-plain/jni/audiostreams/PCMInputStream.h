#ifndef STREAMS_H
#define STREAMS_H

#include <stdio.h>

namespace AudioHelpers {
const int NO_DATA = -1;

class PCMInputStream {
public:
    PCMInputStream() {}
    ~PCMInputStream() {};
    virtual size_t read(void* buffer, size_t count) = 0;
    virtual size_t getSampleRate() = 0;
    virtual size_t getChannelsCount() = 0;
    virtual size_t getDurationInSamples() = 0;
};

}

#endif // STREAMS_H