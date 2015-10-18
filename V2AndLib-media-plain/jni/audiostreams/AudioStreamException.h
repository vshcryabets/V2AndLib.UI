#ifndef AUDIOSTREAM_EXCEPTION_H
#define AUDIOSTREAM_EXCEPTION_H

#include <stdio.h>
#include <exception>

#ifdef ANDROID
    #define _NOEXCEPT throw()
#endif

namespace AudioHelpers {
class AudioStreamException : public std::exception {
private:
    static const char* TAG;
protected:
    const char* mText;
public:
    AudioStreamException(const char* text, ...);
    virtual ~AudioStreamException() _NOEXCEPT;
    virtual const char* what() const _NOEXCEPT;
};
}
#endif // AUDIOSTREAM_EXCEPTION_H
