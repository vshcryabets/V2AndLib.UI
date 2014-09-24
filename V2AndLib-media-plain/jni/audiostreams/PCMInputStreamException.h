#ifndef PCMINPUTSTREAM_EXCEPTION_H
#define PCMINPUTSTREAM_EXCEPTION_H

#include <stdio.h>
#include <exception>

namespace AudioHelpers {
class PCMInputStreamException : public std::exception {
private:
    static const char* TAG;
protected:
    const char* mText;
public:
    PCMInputStreamException(const char* text);
    virtual const char* what();
};
}

#endif // PCMINPUTSTREAM_EXCEPTION_H