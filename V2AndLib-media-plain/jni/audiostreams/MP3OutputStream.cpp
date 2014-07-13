#include "MP3OutputStream.h"
#include "PCMInputStreamException.h"
// #include <android/log.h>

namespace AudioHelpers {

const char* MP3OutputStream::TAG = "MP3OutputStream";

MP3OutputStream::MP3OutputStream(const char* filePath) {
    int err;
}
MP3OutputStream::~MP3OutputStream() {
}
void MP3OutputStream::write(void* buffer, size_t count) {
}

void MP3OutputStream::setSampleRate() {
    return;
}
void MP3OutputStream::setChannelsCount() {
    return;
}
}