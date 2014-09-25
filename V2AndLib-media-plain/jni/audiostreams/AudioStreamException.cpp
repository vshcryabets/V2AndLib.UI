#include "AudioStreamException.h"

using namespace AudioHelpers;

const char* AudioStreamException::TAG = "AudioStreamException";

AudioStreamException::AudioStreamException(const char* text, ... ) {
//    va_list ap;
//    va_start(ap,p);

    mText = text;

}

AudioStreamException::~AudioStreamException() _NOEXCEPT {
}

const char* AudioStreamException::what() const _NOEXCEPT {
    return mText;
}
