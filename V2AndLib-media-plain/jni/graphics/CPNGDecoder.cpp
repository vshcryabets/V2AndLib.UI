#include "CPNGDecoder.h"
#include "JPEGException.h"
#include <string.h>

CPNGDecoder::CPNGDecoder(const char* sourceFilePath, size_t maxMemCahceSize) : mFile(NULL), mRowBuffer(NULL),
    mMaxMemCahceSize(maxMemCahceSize) {
    mFile = fopen(sourceFilePath, "rb");
    if ( mFile == NULL ) {
        throw new JPEGException(ERR_NO_FILE);
    }

    /* Initialize the 'png_image' structure. */
    mInfo = new png_image;
    memset(mInfo, 0, sizeof(png_image));
    mInfo->version = PNG_IMAGE_VERSION;

//    png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, png_voidp user_error_ptr, user_error_fn, user_warning_fn);
//    if (png_ptr == NULL) {
//        throw new JPEGException(ERR_JPEG_DECODER);
//    }
//
//    /* Allocate/initialize the memory for image information.  REQUIRED. */
//    info_ptr = png_create_info_struct(png_ptr);
//    if (info_ptr == NULL) {
//        png_destroy_read_struct(&png_ptr, NULL, NULL);
//        throw new JPEGException(ERR_JPEG_DECODER);
//    }

    mErrHandler = new cpng_error_mgr();
//    mErrHandler->pub.error_exit = cjpeg_error_handler;
    if (setjmp(mErrHandler->setjmp_buffer)) {
//        png_destroy_read_struct(&png_ptr, &info_ptr, NULL);
        throw new JPEGException(ERR_JPEG_DECODER);
    }

}

CPNGDecoder::~CPNGDecoder() {
    //free resources
    CLEAN(mRowBuffer);
    CLEAN(mErrHandler);
    if ( mFile != NULL ) {
        fclose(mFile);
        mFile = NULL;
    }
}

size_t CPNGDecoder::getWidth() {
    return -1;
}

size_t CPNGDecoder::getHeight() {
    return -1;
}

void CPNGDecoder::startDecompress() {
}

void CPNGDecoder::readLine() {
}

void CPNGDecoder::finishDecompress() {
    CLEAN(mRowBuffer);
}

void* CPNGDecoder::getLineBuffer() {
    return mRowBuffer;
}

size_t CPNGDecoder::getLineBufferStride() {
    return -1;
}

size_t CPNGDecoder::getOutputComponents() {
    return -1;
}
