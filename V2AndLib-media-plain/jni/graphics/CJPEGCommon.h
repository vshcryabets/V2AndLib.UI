#ifndef _CJPEG_COMMON_H_
#define _CJPEG_COMMON_H_

#include <stdio.h>
#include <setjmp.h>
#include "jpeglib.h"

#define CLEAN(X) {if (X != NULL) {delete X; X = NULL;}}

typedef struct jpeg_decompress_struct jpeg_decompress;
typedef struct jpeg_compress_struct jpeg_compress;

struct cjpeg_error_mgr {
    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};

void cjpeg_error_handler(j_common_ptr cinfo);

#endif // _CJPEG_COMMON_H_
