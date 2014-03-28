#ifndef _LAME_HANDLER_H
#define _LAME_HANDLER_H
#include "lame.h"
#include "lame_global_flags.h"

namespace lamewrapper {
class LameDataUnit {
public:
    lame_global_flags *mLameFlags;
    unsigned char *mEncodeBuffer;
    unsigned int mNumberOfChannels;

    LameDataUnit();
    ~LameDataUnit();
    unsigned int initialize(unsigned int numberOfChannels, unsigned int inputSampleRate,
        unsigned int outputSampleRate, MPEG_mode mode);
    unsigned int encodeBuffer(unsigned char* inputBuffer, unsigned char* outputBuffer, int inputCount);
    unsigned char* getEncoderBuffer();
};
}
#endif //_LAME_HANDLER_H
