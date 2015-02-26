#ifndef _CPNG_DECODER_H_
#define _CPNG_DECODER_H_

#include "CPNGCommon.h"
#include <stdint.h>

class CPNGDecoder {
private:
    cpng_error_mgr *mErrHandler;
    png_image *mInfo;
    FILE* mFile;
    uint8_t *mRowBuffer;
    size_t mMaxMemCahceSize;
public:
    CPNGDecoder(const char* sourceFilePath, size_t maxMemCahceSize);
    virtual ~CPNGDecoder();
    virtual size_t getWidth();
    virtual size_t getHeight();
    virtual void startDecompress();
    virtual void readLine();
    virtual void finishDecompress();
    virtual void* getLineBuffer();
    virtual size_t getLineBufferStride();
    virtual size_t getOutputComponents();
};

#endif // _CJPEG_DECODER_H_
