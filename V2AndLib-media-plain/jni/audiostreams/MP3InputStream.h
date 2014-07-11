#ifndef MP3_INPUT_STREAM_H
#define MP3_INPUT_STREAM_H

#include <stdio.h>
#include "PCMInputStream.h"
#include "mpg123.h"

namespace AudioHelpers {
class MP3InputStream : public PCMInputStream {
protected:
    static const char* TAG;
    mpg123_handle * mHandle;
    long mSampleRate;
    int mChannels;
    int mEncoding;
public:
    MP3InputStream(const char* filePath);
    ~MP3InputStream();
    size_t read(void* buffer, size_t count);
    size_t getSampleRate();
    size_t getChannelsCount();
    size_t getDurationInSamples();
};

}

#endif // MP3_INPUT_STREAM_H