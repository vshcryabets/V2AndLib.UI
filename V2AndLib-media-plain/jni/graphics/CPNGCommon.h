#ifndef _CPNG_COMMON_H_
#define _CPNG_COMMON_H_

#include <stdio.h>
#include <png.h>

#define CLEAN(X) {if (X != NULL) {delete X; X = NULL;}}

struct cpng_error_mgr {
//    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};

#endif // _CPNG_COMMON_H_
