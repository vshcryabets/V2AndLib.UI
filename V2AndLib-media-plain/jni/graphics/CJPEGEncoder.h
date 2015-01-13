#ifndef _CJPEG_ENCODER_H_
#define _CJPEG_ENCODER_H_

#include "CJPEGCommon.h"

class CJPEGEncoder {
private:
    jpeg_handle *mInfo;
    my_error_mgr *mErrHandler;
    FILE* mFile;
public:
    CJPEGEncoder(const char* sourceFilePath);
    virtual ~CJPEGEncoder();
};

#endif // _CJPEG_ENCODER_H_
