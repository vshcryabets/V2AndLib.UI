#include "CJPEGDecoder.h"
#include "JPEGException.h"

CJPEGDecoder::CJPEGDecoder(const char* sourceFilePath) : mFile(NULL) {
    mFile = fopen(sourceFilePath, "rb");
    if ( mFile == NULL ) {
        throw new JPEGException(ERR_NO_FILE);
    }

    mInfo = new jpeg_handle();
    mErrHandler = new my_error_mgr();
    mInfo->err = jpeg_std_error(&mErrHandler->pub);
    mErrHandler->pub.error_exit = cjpeg_error_handler;
    if (setjmp(mErrHandler->setjmp_buffer)) {
            jpeg_destroy_decompress(mInfo);
            throw new JPEGException(ERR_JPEG_DECODER);
    }

    jpeg_create_decompress(mInfo);
    jpeg_stdio_src(mInfo, mFile);

    //reading JPEG header
    jpeg_read_header(mInfo, TRUE);
}

CJPEGDecoder::~CJPEGDecoder() {
    //free resources
    if ( mInfo != NULL ) {
        delete mInfo;
        mInfo = NULL;
    }
    if ( mErrHandler != NULL ) {
        delete mErrHandler;
        mErrHandler = NULL;
    }
    if ( mInfo != NULL ) {
        jpeg_destroy_decompress(mInfo);
        delete mInfo;
        mInfo = NULL;
    }
    if ( mFile != NULL ) {
        fclose(mFile);
        mFile = NULL;
    }
}
