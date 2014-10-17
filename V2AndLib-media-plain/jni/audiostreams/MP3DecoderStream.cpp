#include "MP3DecoderStream.h"
#include "AudioStreamException.h"

using namespace AudioHelpers;

const char* MP3DecoderStream::TAG = "MP3DecoderStream";

MP3DecoderStream::MP3DecoderStream(PCMOutputStream* output, size_t maxBuffer) : mOutput(output) {
    init();
    int err = mpg123_open_feed(mHandle);
    if ( err != MPG123_OK ) {
            throw new AudioStreamException("Can't open MP3 feed");
    }
}

//MP3DecoderStream::MP3DecoderStream(const char* filePath) {
//    int err;
//    init();
//    err = mpg123_open(mHandle, filePath);
//    if ( err != MPG123_OK ) {
//        throw new AudioStreamException("Can't initialize MPG123");
//    }
//    err = mpg123_getformat(mHandle, &mSampleRate, &mChannels, &mEncoding);
//    if ( err != MPG123_OK ) {
//        throw new AudioStreamException("Can't initialize MPG123");
//    }
//    mpg123_format_none(mHandle);
//    mpg123_format(mHandle, mSampleRate, mChannels, mEncoding);
//}

void MP3DecoderStream::init() {
    int err;
    mHandle = mpg123_new(NULL, &err);
    if ( err == MPG123_NOT_INITIALIZED ) {
        err = mpg123_init();
        if ( err != MPG123_OK ) {
            throw new AudioStreamException("Can't initialize MPG123 library");
        }
        mHandle = mpg123_new(NULL, &err);
    }
    if ( err != MPG123_OK || mHandle == NULL ) {
        throw new AudioStreamException("Can't initialize MPG123 handler");
    }
}

MP3DecoderStream::~MP3DecoderStream() {
    if ( mHandle != NULL ) {
        mpg123_close(mHandle);
        mpg123_delete(mHandle);
        mHandle = NULL;
    }
    //mpg123_exit()
}
size_t MP3DecoderStream::write(void* buffer, size_t count) {
    if ( mHandle == NULL ) {
        return AudioHelpers::NO_DATA;
    }
//    size_t read;
//    int result = mpg123_read(mHandle, (unsigned char*)buffer, count, &read);
//    if ( result == MPG123_DONE ) {
//        throw new AudioStreamException("End of MP3 stream reached.");
//    }
//    if ( result != MPG123_OK ) {
////        __android_log_print(ANDROID_LOG_DEBUG, TAG, "failed to get format MP3 file %s", mpg123_strerror(mHandle));
//        throw new AudioStreamException("Error during read");
//    }
//    return read;
}

void MP3DecoderStream::flush() {

}


void MP3DecoderStream::close() {

}
//
//size_t MP3DecoderStream::getSampleRate() {
//    return mSampleRate;
//}
//size_t MP3DecoderStream::getChannelsCount() {
//    return mChannels;
//}
//size_t MP3DecoderStream::getDurationInSamples() {
//    if ( mHandle != NULL ) {
//        return mpg123_length(mHandle);
//    } else {
//        throw new AudioStreamException("MPG123 input stream not initialized (handle is NULL)");
//    }
//}

void MP3DecoderStream::setInputSampleRate(size_t samplerate) {
    // we can ignore it
}
void MP3DecoderStream::setInputChannelsCount(size_t channelsCount) {
    // we can ignore it
}
void MP3DecoderStream::setOutputSampleRate(size_t samplerate) {
    // TODO do something
}
void MP3DecoderStream::setOutputChannelsCount(size_t channelsCount) {
    // TODO do something
}

PCMOutputStream* MP3DecoderStream::getSubStream() {
    return mOutput;
}
