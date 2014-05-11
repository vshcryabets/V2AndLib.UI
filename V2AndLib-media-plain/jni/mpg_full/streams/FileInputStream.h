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
    ~FilePCMInputStream();
    size_t read(void* buffer, size_t count);
    size_t getSampleRate();
};

}

#endif // FILE_INPUT_STREAM_H