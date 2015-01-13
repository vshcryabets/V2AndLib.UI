#ifndef _CJPEG_DECODER_H_
#define _CJPEG_DECODER_H_

#include "CJPEGCommon.h"

class CJPEGDecoder {
private:
    jpeg_handle *mInfo;
    my_error_mgr *mErrHandler;
    FILE* mFile;
public:
    CJPEGDecoder(const char* sourceFilePath);
    virtual ~CJPEGDecoder();
};

#endif // _CJPEG_DECODER_H_
