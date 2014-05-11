#include "MP3InputStream.h"

#include <android/log.h>

namespace AudioHelpers {

const char* FilePCMInputStream::TAG = "MP3InputStream";

MP3InputStream::MP3InputStream(const char* filePath) {
    int err;
    mHandle = mpg123_new(NULL, &err);
    if ( err != MPG123_OK || mHandle == NULL ) {
        throw new PCMInputStreamException("Can't initialize MPG123");
    }
    err = mpg123_open(mHandle, filePath);
    if ( err != MPG123_OK ) {
        __android_log_print(ANDROID_LOG_DEBUG, "failed to open MP3 file %s", mpg123_strerror(res));
        throw new PCMInputStreamException("Can't initialize MPG123");
    }
    err = mpg123_getformat(mHandle, rate, channels, encoding);
    if ( err != MPG123_OK ) {
        __android_log_print(ANDROID_LOG_DEBUG, "failed to get format MP3 file %s", mpg123_strerror(res));
        throw new PCMInputStreamException("Can't initialize MPG123");
    }
    mpg123_format_none(mHandle);
    mpg1233_format(mHandle, *rate, *channels, *encoding);
}
MP3InputStream::~MP3InputStream() {
    if ( mHandle != NULL ) {
        mpg123_close(mHandle);
        mpg123_delete(mHandle);
        mHandle = NULL;
    }
    // mpg123_exit()
}
size_t MP3InputStream::read(void* buffer, size_t count) {
    if ( mFile == NULL ) {
        return 0;
    }
    __android_log_print(ANDROID_LOG_INFO, TAG, "Read buffer %d", count);
    size_t result = fread(buffer, sizeof(char), count, mFile);
    return result;
}

size_t MP3InputStream::getSampleRate() {
    return 48000;
}

}