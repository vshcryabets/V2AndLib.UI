#ifndef _LAME_HANDLER_H
#define _LAME_HANDLER_H
namespace lamewrapper {
class LameDataUnit {
public:
    lame_global_flags *lgf;
    unsigned char *mp3buf;

    LameDataUnit();
    unsigned int initialize();
}
}
#endif //_LAME_HANDLER_H
