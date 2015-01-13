#ifndef _CJPEG_COMMON_H_
#define _CJPEG_COMMON_H_

#include <stdio.h>
#include <setjmp.h>
#include "jpeglib.h"

typedef struct jpeg_decompress_struct jpeg_handle;

struct my_error_mgr {
    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};

void cjpeg_error_handler(j_common_ptr cinfo);

#endif // _CJPEG_COMMON_H_
