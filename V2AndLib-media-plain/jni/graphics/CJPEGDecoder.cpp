#include "CJPEGDecoder.h"
#include "JPEGException.h"

CJPEGDecoder::CJPEGDecoder(const char* sourceFilePath, size_t maxMemCahceSize) : mFile(NULL), mRowBuffer(NULL),
    mMaxMemCahceSize(maxMemCahceSize) {
    mFile = fopen(sourceFilePath, "rb");
    if ( mFile == NULL ) {
        throw new JPEGException(ERR_NO_FILE);
    }

    mInfo = new jpeg_decompress();
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
    CLEAN(mRowBuffer);
    CLEAN(mErrHandler);
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

size_t CJPEGDecoder::getWidth() {
    return mInfo->image_width;
}

size_t CJPEGDecoder::getHeight() {
    return mInfo->image_height;
}

void CJPEGDecoder::startDecompress() {
    jpeg_start_decompress(mInfo);
    size_t row_stride = getLineBufferStride();
    mRowBuffer = new uint8_t[row_stride];
}

void CJPEGDecoder::readLine() {
    jpeg_read_scanlines(mInfo, &mRowBuffer, 1);
}

void CJPEGDecoder::finishDecompress() {
    jpeg_finish_decompress(mInfo);
    if ( mRowBuffer != NULL ) {
        delete mRowBuffer;
        mRowBuffer = NULL;
    }
}

void* CJPEGDecoder::getLineBuffer() {
    return mRowBuffer;
}

size_t CJPEGDecoder::getLineBufferStride() {
    return mInfo->output_width * mInfo->output_components;
}

size_t CJPEGDecoder::getOutputComponents() {
    return mInfo->output_components;
}
