#ifndef _CJPEG_ENCODER_H_
#define _CJPEG_ENCODER_H_

#include "CJPEGCommon.h"

class CJPEGEncoder {
private:
    FILE* mFile;
    cjpeg_error_mgr *mErrHandler;
    jpeg_compress *mInfo;
public:
    CJPEGEncoder(const char* sourceFilePath, size_t width, size_t height, int quality);
    virtual ~CJPEGEncoder();
    virtual void startCompress();
    virtual void finishCompress();
    virtual void writeLine(void **rowPointers, size_t rowStride, size_t rowsCount);
};

#endif // _CJPEG_ENCODER_H_
