#ifndef OUT_STREAMS_H
#define OUT_STREAMS_H

#include <stdio.h>

namespace AudioHelpers {
const int NO_DATA = -1;

class PCMOutputStream {
public:
    PCMOutputStream() {}
    virtual ~PCMOutputStream() {};
    virtual void write(void* buffer, size_t count) = 0;
    virtual void setSampleRate() = 0;
    virtual void setChannelsCount() = 0;
};

}

#endif // OUT_STREAMS_H