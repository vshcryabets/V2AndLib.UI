#include "FileInputStream.h"

//#include <android/log.h>

namespace AudioHelpers {

const char* FilePCMInputStream::TAG = "PCMInputStream";

FilePCMInputStream::FilePCMInputStream(const char* filePath) {
    mFile = fopen(filePath, "r");
}
FilePCMInputStream::~FilePCMInputStream() {
    if ( mFile != NULL ) {
        fclose(mFile);
        mFile = NULL;
    }
}
size_t FilePCMInputStream::read(void* buffer, size_t count) {
    if ( mFile == NULL ) {
        return 0;
    }
//    __android_log_print(ANDROID_LOG_INFO, TAG, "Read buffer %d", count);
    size_t result = fread(buffer, sizeof(char), count, mFile);
    return result;
}

size_t FilePCMInputStream::getSampleRate() {
    return 48000;
}
size_t FilePCMInputStream::getChannelsCount() {
    return 1;
}
}