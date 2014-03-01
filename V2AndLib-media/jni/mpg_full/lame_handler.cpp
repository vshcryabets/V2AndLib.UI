#include "lame_handler.h"
#include "lame.h"

#define mp3buf_size 8192//1.25 * nsamples + 7200;

LameDataUnit::LameDataUnit() {
    this->lgf = lame_init();
   	this->mp3buf = (unsigned char *) malloc(mp3buf_size);
}

unsigned int LameDataUnit::initialize() {
    if (lame_init_params(lgf) == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "LameWrapper[Native]", "Lame init params failed");
    	return 0;
    }
}
