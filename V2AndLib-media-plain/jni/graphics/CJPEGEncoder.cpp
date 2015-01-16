#include "CJPEGEncoder.h"
#include "JPEGException.h"

void cjpeg_error_handler(j_common_ptr cinfo) {
    my_error_mgr* myerr = (my_error_mgr*) cinfo->err;
    (*cinfo->err->output_message)(cinfo);
    longjmp(myerr->setjmp_buffer, 1);
}

CJPEGEncoder::CJPEGEncoder(const char* sourceFilePath) : mFile(NULL) {
    mFile = fopen(sourceFilePath, "w");
    if ( mFile == NULL ) {
        throw new JPEGException(ERR_CANT_CREATE_FILE);
    }
}

CJPEGEncoder::~CJPEGEncoder() {
    //free resources
    if ( mFile != NULL ) {
        fclose(mFile);
        mFile = NULL;
    }
}
