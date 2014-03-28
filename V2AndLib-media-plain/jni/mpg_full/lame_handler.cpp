#include "lame_handler.h"
#include <android/log.h>
#include <stdlib.h>

#define mp3buf_size 8192//1.25 * nsamples + 7200;

namespace lamewrapper {
const int BYTES_PER_SAMPLE = 2;
LameDataUnit::LameDataUnit() {
    mLameFlags = lame_init();
   	mEncodeBuffer = (unsigned char *) malloc(mp3buf_size);
}
LameDataUnit::~LameDataUnit() {
	free(mEncodeBuffer);
	lame_close(mLameFlags);
}
unsigned int LameDataUnit::initialize(unsigned int numberOfChannels, unsigned int inputSampleRate,
        unsigned int outputSampleRate, MPEG_mode mode) {
	lame_set_mode(mLameFlags, mode);
	lame_set_num_channels(mLameFlags, numberOfChannels);
    lame_set_in_samplerate(mLameFlags, inputSampleRate);
    lame_set_out_samplerate(mLameFlags, outputSampleRate);

    mNumberOfChannels = numberOfChannels;

    if (lame_init_params(mLameFlags) == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "LameWrapper[Native]", "Lame init params failed");
    	return 0;
    }
}

unsigned int LameDataUnit::encodeBuffer(unsigned char* inputBuffer,
    unsigned char* outputBuffer,
    int inputCount) {
    size_t samplesCount = inputCount/BYTES_PER_SAMPLE/mNumberOfChannels;
//    __android_log_print(ANDROID_LOG_VERBOSE, "LAME", "encodeBuffer %p, %d, %p, %d", inputBuffer, samplesCount, mEncodeBuffer, mp3buf_size);
    size_t encodedCount = lame_encode_buffer_interleaved(mLameFlags, (short int*)inputBuffer,
        samplesCount, mEncodeBuffer, mp3buf_size);
//    __android_log_print(ANDROID_LOG_VERBOSE, "LAME", "encodeBuffer result=%d", encodedCount);
    return encodedCount;
}
unsigned char* LameDataUnit::getEncoderBuffer() {
    return mEncodeBuffer;
}
}