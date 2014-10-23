#include "MP3EncoderStream.h"
#include "AudioStreamException.h"

using namespace AudioHelpers;

const char* MP3EncoderStream::TAG = "MP3EncoderStream";

MP3EncoderStream::MP3EncoderStream(PCMOutputStream* outstream, size_t maxBuffer)
    : mOutput(outstream), mLameHandler(NULL), mMaxInputBuffer(maxBuffer) {
    if ( mOutput == NULL ) {
        throw new AudioStreamException("Output stream is null");
    }
    mLameHandler = lame_init();
    mMaxOutputBuffer = mMaxInputBuffer;
    mEncodedBuffer = new char[mMaxOutputBuffer];
}

MP3EncoderStream::~MP3EncoderStream() {
    close();
    if ( mEncodedBuffer != NULL ) {
        delete [] mEncodedBuffer;
        mEncodedBuffer = NULL;
    }
}

size_t MP3EncoderStream::write(void* buffer, size_t count) {
    checkHandle();
    int res = 0;
    size_t inputSamplesCount = count / mChannelsCount / 2;
    if ( mChannelsCount == 1 ) {
        res = lame_encode_buffer(mLameHandler, (short*)buffer,
                    NULL,
                    inputSamplesCount,
                    (unsigned char*)mEncodedBuffer,
                    getEncodedBufferSize());
    } else {
        res = lame_encode_buffer_interleaved(mLameHandler, (short*)buffer,
                    inputSamplesCount,
                    (unsigned char*)mEncodedBuffer,
                    getEncodedBufferSize());
        printf("A.1.1 %d %d\n", res, inputSamplesCount );
    }
    /**
     return code     number of bytes output in mp3buf. Can be 0
                     -1:  mp3buf was too small
                     -2:  malloc() problem
                     -3:  lame_init_params() not called
                     -4:  psycho acoustic problems
                     */
    switch (res) {
        case -1:
            throw new AudioStreamException("mp3buf was too small");
        case -2:
            throw new AudioStreamException("malloc() problem");
        case -3:
            throw new AudioStreamException("lame_init_params() not called");
        case -4:
            throw new AudioStreamException("psycho acoustic problems");
        default:
            mOutput->write(mEncodedBuffer, res);
            return res;
    }
}

void MP3EncoderStream::flush() {
    checkHandle();
    size_t result = lame_encode_flush(mLameHandler, (unsigned char*)mEncodedBuffer, getEncodedBufferSize());
    if ( result > 0 ) {
        mOutput->write(mEncodedBuffer, result);
    }
}

void MP3EncoderStream::configure(size_t channelsCount, size_t samplerate, size_t outSampleRate,
                                MPEG_mode encodingMode) {
    checkHandle();

    setOutputChannelsCount(channelsCount);
    setInputChannelsCount(channelsCount);
    setInputSampleRate(samplerate);
    setOutputSampleRate(outSampleRate);
    lame_set_mode(mLameHandler, encodingMode);

    lame_set_VBR(mLameHandler, vbr_default);
    // lame_set_VBR_quality(lame, 2);
    lame_set_brate(mLameHandler, 128);
//    lame_set_disable_reservoir(gfp, TRUE);
//    lame_set_quality(gfp, ql_level[quality]);   /* 7=low  5=medium  2=high */

    int ret = lame_init_params(mLameHandler);
    if (ret < 0) {
        throw new AudioStreamException("Error occurred during parameters initializing. Code = %d\n", ret);
    }
}

void MP3EncoderStream::close() {
    if ( mLameHandler != NULL ) {
        flush();
        lame_close(mLameHandler);
        mLameHandler = NULL;
    }
}
void MP3EncoderStream::setInputSampleRate(size_t samplerate) {
    checkHandle();
    lame_set_in_samplerate(mLameHandler, samplerate);
}

void MP3EncoderStream::setInputChannelsCount(size_t channelsCount) {
    checkHandle();
    lame_set_num_channels(mLameHandler, channelsCount);
}

void MP3EncoderStream::setOutputSampleRate(size_t samplerate) {
    checkHandle();
    lame_set_out_samplerate(mLameHandler, samplerate);
}

void MP3EncoderStream::setOutputChannelsCount(size_t channelsCount) {
    checkHandle();
    mChannelsCount = channelsCount;
    lame_set_num_channels(mLameHandler, channelsCount);
}

void MP3EncoderStream::checkHandle() {
    if ( mLameHandler == NULL ) {
        throw new AudioStreamException("Encoder wasn't initialized");
    }
}

PCMOutputStream* MP3EncoderStream::getSubStream() {
    return mOutput;
}

size_t MP3EncoderStream::getEncodedBufferSize() {
    return mMaxOutputBuffer;
}

size_t MP3EncoderStream::getInputBufferSize() {
    return mMaxInputBuffer;
}
