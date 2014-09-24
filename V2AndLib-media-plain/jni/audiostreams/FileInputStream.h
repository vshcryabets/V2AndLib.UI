#ifndef FILE_INPUT_STREAM_H
#define FILE_INPUT_STREAM_H

#include <stdio.h>
#include "PCMInputStream.h"

namespace AudioHelpers {
class FilePCMInputStream : public PCMInputStream {
private:
    static const char* TAG;
    FILE* mFile;
public:
    FilePCMInputStream(const char* filePath);
    virtual ~FilePCMInputStream();
    virtual size_t read(void* buffer, size_t count);
    virtual size_t getSampleRate();
    virtual size_t getChannelsCount();
};

}

#endif // FILE_INPUT_STREAM_H