#include "MP3InputStream.h"
#include "PCMInputStreamException.h"
// #include <android/log.h>

namespace AudioHelpers {

const char* MP3InputStream::TAG = "MP3InputStream";

MP3InputStream::MP3InputStream(const char* filePath) {
    int err;
    mHandle = mpg123_new(NULL, &err);
    if ( err == MPG123_NOT_INITIALIZED ) {
        err = mpg123_init();
        if ( err != MPG123_OK ) {
            PCMInputStreamException* exc = new PCMInputStreamException("Can't initialize MPG123 library");
            throw exc;
        }
        mHandle = mpg123_new(NULL, &err);
    }
    if ( err != MPG123_OK || mHandle == NULL ) {
        PCMInputStreamException* exc = new PCMInputStreamException("Can't initialize MPG123 handler");
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
    //mpg123_exit()
}
size_t MP3InputStream::read(void* buffer, size_t count) {
    if ( mHandle == NULL ) {
        return AudioHelpers::NO_DATA;
    }
    size_t read;
    int result = mpg123_read(mHandle, (unsigned char*)buffer, count, &read);
    if ( result == MPG123_DONE ) {
        throw new PCMInputStreamException("End of MP3 stream reached.");
    }
    if ( result != MPG123_OK ) {
//        __android_log_print(ANDROID_LOG_DEBUG, TAG, "failed to get format MP3 file %s", mpg123_strerror(mHandle));
        throw new PCMInputStreamException("Error during read");
    }
    return read;
}

size_t MP3InputStream::getSampleRate() {
    return mSampleRate;
}
size_t MP3InputStream::getChannelsCount() {
    return mChannels;
}
size_t MP3InputStream::getDurationInSamples() {
    if ( mHandle != NULL ) {
        return mpg123_length(mHandle);
    } else {
        throw new PCMInputStreamException("MPG123 input stream not initialized (handle is NULL)");
    }
}
}