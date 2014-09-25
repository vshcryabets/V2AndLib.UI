#include "MP3OutputStream.h"
#include "AudioStreamException.h"

using namespace AudioHelpers;

const char* MP3OutputStream::TAG = "MP3OutputStream";

MP3OutputStream::MP3OutputStream(PCMOutputStream* outstream)
    : mOutput(outstream), mLameHandler(NULL) {
    if ( mOutput == NULL ) {
        throw new AudioStreamException("Output stream is null");
    }
    mLameHandler = lame_init();
}

MP3OutputStream::~MP3OutputStream() {
    close();
}

void MP3OutputStream::write(void* buffer, size_t count) {
    checkHandle();
    // lame_encode_buffer_interleaved(gfp, (short *) encbuf, chunk_samples, buf, max_len);
}

void MP3OutputStream::configure(size_t channelsCount, size_t samplerate) {
    checkHandle();

    setOutputChannelsCount(channelsCount);
    setInputChannelsCount(channelsCount);
    setInputSampleRate(samplerate);
    setOutputSampleRate(samplerate);

    // lame_set_VBR(lame, vbr_default);
    // lame_set_VBR_quality(lame, 2);
//    lame_set_brate(gfp, br_level[quality]);
//    lame_set_mode(gfp, JOINT_STEREO);
//    lame_set_disable_reservoir(gfp, TRUE);
//    lame_set_quality(gfp, ql_level[quality]);   /* 7=low  5=medium  2=high */

    int ret = lame_init_params(mLameHandler);
    if (ret < 0) {
        throw new AudioStreamException("Error occurred during parameters initializing. Code = %d\n", ret);
    }
}

void MP3OutputStream::flush() {
    checkHandle();
}

void MP3OutputStream::close() {
    if ( mLameHandler != NULL ) {
        flush();
        lame_close(mLameHandler);
        mLameHandler = NULL;
    }
}
void MP3OutputStream::setInputSampleRate(size_t samplerate) {
    checkHandle();
    lame_set_in_samplerate(mLameHandler, samplerate);
}

void MP3OutputStream::setInputChannelsCount(size_t channelsCount) {
    checkHandle();
    lame_set_num_channels(mLameHandler, channelsCount);
}

void MP3OutputStream::setOutputSampleRate(size_t samplerate) {
    checkHandle();
    lame_set_out_samplerate(mLameHandler, samplerate);
}

void MP3OutputStream::setOutputChannelsCount(size_t channelsCount) {
    checkHandle();
    lame_set_num_channels(mLameHandler, channelsCount);
}

void MP3OutputStream::checkHandle() {
    if ( mLameHandler == NULL ) {
        throw new AudioStreamException("Not initialized");
    }
}
