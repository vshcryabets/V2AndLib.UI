#include "CJPEGEncoder.h"
#include "JPEGException.h"

void cjpeg_error_handler(j_common_ptr cinfo) {
    cjpeg_error_mgr* myerr = (cjpeg_error_mgr*) cinfo->err;
    (*cinfo->err->output_message)(cinfo);
    longjmp(myerr->setjmp_buffer, 1);
}

CJPEGEncoder::CJPEGEncoder(const char* sourceFilePath, size_t width, size_t height, int quality) : mFile(NULL) {
    mFile = fopen(sourceFilePath, "w");
    if ( mFile == NULL ) {
        throw new JPEGException(ERR_CANT_CREATE_FILE);
    }

    mInfo = new jpeg_compress();
    mErrHandler = new cjpeg_error_mgr();
    mInfo->err = jpeg_std_error(&mErrHandler->pub);
    mErrHandler->pub.error_exit = cjpeg_error_handler;
    if (setjmp(mErrHandler->setjmp_buffer)) {
            jpeg_destroy_compress(mInfo);
            throw new JPEGException(ERR_JPEG_ENCODER);
    }

    /* Now we can initialize the JPEG compression object. */
    jpeg_create_compress(mInfo);
    jpeg_stdio_dest(mInfo, mFile);

    mInfo->image_width = width;
    mInfo->image_height = height;
    mInfo->input_components = 3;
    mInfo->in_color_space = JCS_RGB;

    jpeg_set_defaults(mInfo);
    jpeg_set_quality(mInfo, quality, TRUE /* limit to baseline-JPEG values */);
}

CJPEGEncoder::~CJPEGEncoder() {
    //free resources
    if ( mFile != NULL ) {
        fclose(mFile);
        mFile = NULL;
    }
    if ( mInfo != NULL ) {
        jpeg_destroy_compress(mInfo);
        delete mInfo;
        mInfo = NULL;
    }
    CLEAN(mErrHandler);
}

void CJPEGEncoder::startCompress() {
    jpeg_start_compress(mInfo, TRUE);
}

void CJPEGEncoder::finishCompress() {
    jpeg_finish_compress(mInfo);
}

void CJPEGEncoder::writeLine(void **rowPointers, size_t rowStride, size_t rowsCount) {
    jpeg_write_scanlines(mInfo, (JSAMPARRAY)rowPointers, rowsCount);
}
