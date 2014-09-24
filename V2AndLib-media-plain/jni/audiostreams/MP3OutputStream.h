#ifndef MP3_OUTPUT_STREAM_H
#define MP3_OUTPUT_STREAM_H

#include <stdio.h>
#include "PCMOutputStream.h"

namespace AudioHelpers {
class MP3OutputStream : public PCMOutputStream {
protected:
    static const char* TAG;
public:
    MP3OutputStream(const char* filePath);
    virtual ~MP3OutputStream();
    void write(void* buffer, size_t count);
    void setSampleRate();
    void setChannelsCount();
};

}

#endif // MP3_OUTPUT_STREAM_H