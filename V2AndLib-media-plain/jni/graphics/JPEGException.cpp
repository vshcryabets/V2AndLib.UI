#include "JPEGException.h"

const char* JPEGException::TAG = "JPEGException";

JPEGException::JPEGException(int code) {
    mCode = code;
}

JPEGException::~JPEGException() _NOEXCEPT {
}

const char* JPEGException::what() const _NOEXCEPT {
    return mText;
}

int JPEGException::getCode() {
    return mCode;
}
