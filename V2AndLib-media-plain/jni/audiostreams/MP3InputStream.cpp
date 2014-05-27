#include "MP3InputStream.h"
#include "PCMInputStreamException.h"
// #include <android/log.h>

namespace AudioHelpers {

const char* MP3InputStream::TAG = "MP3InputStream";

MP3InputStream::MP3InputStream(const char* filePath) {
    int err;
    mHandle = mpg123_new(NULL, &err);
    if ( err != MPG123_OK || mHandle == NULL ) {
        PCMInputStreamException* exc = new PCMInputStreamException("Can't initialize MPG123");
        throw exc;
    }
    err = mpg123_open(mHandle, filePath);
    if ( err != MPG123_OK ) {
//        __android_log_print(ANDROID_LOG_DEBUG, TAG, "failed to open MP3 file %s", mpg123_strerror(mHandle));
        throw new PCMInputStreamException("Can't initialize MPG123");
    }
    err = mpg123_getformat(mHandle, &mSampleRate, &mChannels, &mEncoding);
    if ( err != MPG123_OK ) {
//        __android_log_print(ANDROID_LOG_DEBUG, TAG, "failed to get format MP3 file %s", mpg123_strerror(mHandle));
        throw new PCMInputStreamException("Can't initialize MPG123");
    }
    mpg123_format_none(mHandle);
    mpg123_format(mHandle, mSampleRate, mChannels, mEncoding);
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
//    if ( mFile == NULL ) {
//        return 0;
//    }
//    __android_log_print(ANDROID_LOG_INFO, TAG, "Read buffer %d", count);
//    size_t result = fread(buffer, sizeof(char), count, mFile);
    return 0;
}

size_t MP3InputStream::getSampleRate() {
    return mSampleRate;
}
size_t MP3InputStream::getChannelsCount() {
    return mChannels;
}
}