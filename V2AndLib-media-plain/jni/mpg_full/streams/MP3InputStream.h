#ifndef MP3_INPUT_STREAM_H
#define MP3_INPUT_STREAM_H

#include <stdio.h>
#include "PCMInputStream.h"
#include "mpg123.h"

namespace AudioHelpers {
class MP3InputStream : public PCMInputStream {
private:
    static const char* TAG;
    mpg123_handle * mHandle;
public:
    MP3InputStream(const char* filePath);
    ~MP3InputStream();
    size_t read(void* buffer, size_t count);
    size_t getSampleRate();
};

}

#endif // MP3_INPUT_STREAM_H