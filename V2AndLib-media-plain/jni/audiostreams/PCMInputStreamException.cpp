#include "PCMInputStreamException.h"

namespace AudioHelpers {

const char* PCMInputStreamException::TAG = "PCMInputStreamException";

PCMInputStreamException::PCMInputStreamException(const char* text) {
    mText = text;
}
const char* PCMInputStreamException::what() {
    return mText;
}
}