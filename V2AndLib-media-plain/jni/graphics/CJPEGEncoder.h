#ifndef _CJPEG_ENCODER_H_
#define _CJPEG_ENCODER_H_

#include <stdio.h>
#include <setjmp.h>
#include "jpeglib.h"

typedef struct jpeg_decompress_struct jpeg_handle;

struct my_error_mgr {
    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};

//typedef struct my_error_mgr * my_error_ptr;

class CJPEGEncoder {
private:
    jpeg_handle *mInfo;
    my_error_mgr *mErrHandler;
    FILE* mFile;
public:
    CJPEGEncoder(const char* sourceFilePath);
    virtual ~CJPEGEncoder();
};



#endif // _CJPEG_ENCODER_H_
