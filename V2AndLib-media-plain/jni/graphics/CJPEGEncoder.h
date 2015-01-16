#ifndef _CJPEG_ENCODER_H_
#define _CJPEG_ENCODER_H_

#include "CJPEGCommon.h"

class CJPEGEncoder {
private:
    FILE* mFile;
public:
    CJPEGEncoder(const char* sourceFilePath);
    virtual ~CJPEGEncoder();
};

#endif // _CJPEG_ENCODER_H_
