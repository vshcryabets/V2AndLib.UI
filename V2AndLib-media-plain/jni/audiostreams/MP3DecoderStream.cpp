#include "MP3DecoderStream.h"
#include "AudioStreamException.h"
#include "mpg123.h"

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
    close();
    //mpg123_exit()
}
size_t MP3DecoderStream::write(void* buffer, size_t count) {
    if ( mHandle == NULL ) {
        return AudioHelpers::NO_DATA;
    }
    int err = mpg123_feed(mHandle, (const unsigned char*)buffer, count);
    if ( err != MPG123_OK ) {
            throw new AudioStreamException("Error at mpg123_feed");
    }

    size_t decoded = 0;
    do {
        off_t frameNumber;
        unsigned char *internalBuffer;

        err = mpg123_decode_frame(mHandle, &frameNumber, &internalBuffer, &decoded);
        if ( err == MPG123_NEW_FORMAT ) {
            long samplerate;
            int channelsCount;
            int encodings;
            mpg123_getformat(mHandle, &samplerate, &channelsCount, &encodings);
            // notify about format change
            mOutput->setInputSampleRate(samplerate);
            mOutput->setInputChannelsCount(channelsCount);
        } else if ( err == MPG123_OK ) {
            mOutput->write(internalBuffer, decoded);
        }
    } while ( decoded > 0 );
    return count;
}

void MP3DecoderStream::flush() {

}


void MP3DecoderStream::close() {
    if ( mHandle != NULL ) {
        mpg123_close(mHandle);
        mpg123_delete(mHandle);
        mHandle = NULL;
    }
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
