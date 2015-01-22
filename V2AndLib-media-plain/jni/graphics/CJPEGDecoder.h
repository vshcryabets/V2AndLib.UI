#ifndef _CJPEG_DECODER_H_
#define _CJPEG_DECODER_H_

#include "CJPEGCommon.h"
#include <stdint.h>

class CJPEGDecoder {
private:
    jpeg_decompress *mInfo;
    my_error_mgr *mErrHandler;
    FILE* mFile;
    uint8_t *mRowBuffer;
    size_t mMaxMemCahceSize;
public:
    CJPEGDecoder(const char* sourceFilePath, size_t maxMemCahceSize);
    virtual ~CJPEGDecoder();
    virtual size_t getWidth();
    virtual size_t getHeight();
    virtual void startDecompress();
    virtual void readLine();
    virtual void finishDecompress();
    virtual void* getLineBuffer();
    virtual size_t getLineBufferStride();
};

#endif // _CJPEG_DECODER_H_
