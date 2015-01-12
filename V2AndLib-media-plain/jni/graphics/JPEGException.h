#ifndef JPEG_EXCEPTION_H
#define JPEG_EXCEPTION_H

#include <stdio.h>
#include <exception>

const int ERR_OK = 0;
const int ERR_NO_SUCH_HANDLER = -1012;
const int ERR_SMALL_OUTPUT_BUFFER = -1013;
const int ERR_OPPERATION_SUCCESS = -1014;
const int ERR_WRONG_INPUT_BUFFER = -1015;
const int ERR_NULL_BUFFER = -1016;
const int ERR_CANT_GET_RESULT_CLASS = -1017;
const int ERR_NO_FILE = -1018;
const int ERR_JPEG_DECODER = -1019;
const int ERR_CANT_CREATE_FILE = -1020;
const int ERR_INCORRECT_GEOMETRY_PARAMETER = -1021;

class JPEGException : public std::exception {
private:
    static const char* TAG;
protected:
    const char* mText;
    int mCode;
public:
    JPEGException(int code);
    virtual ~JPEGException() _NOEXCEPT;
    virtual const char* what() const _NOEXCEPT;
    virtual int getCode();
};

#endif // JPEG_EXCEPTION_H
