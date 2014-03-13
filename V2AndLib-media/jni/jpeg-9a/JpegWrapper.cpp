#include "JpegWrapper.h"
#include <stdio.h>
#include <android/log.h>
#include <setjmp.h>
#include "jpeglib.h"


struct my_error_mgr {
    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};
typedef struct my_error_mgr * my_error_ptr;

METHODDEF(void) my_error_exit(j_common_ptr cinfo) {
    my_error_ptr myerr = (my_error_ptr) cinfo->err;
    (*cinfo->err->output_message)(cinfo);
    longjmp(myerr->setjmp_buffer, 1);
}

JNIEXPORT jstring JNICALL Java_com_v2soft_AndLib_media_JPEGHelper_getVersion(JNIEnv * env, jclass c) {
    return env->NewStringUTF("9.0");
}


JNIEXPORT jobject JNICALL Java_com_v2soft_AndLib_media_JPEGHelper_getJPEGInfo(JNIEnv* env,
    jclass c,
    jstring jniFileName) {

    const char* fileName = env->GetStringUTFChars(jniFileName, 0);

    FILE* file = fopen(fileName, "rb");
    env->ReleaseStringUTFChars(jniFileName, fileName);

    if ( file == NULL ) {
        return NULL;
    }

    struct jpeg_decompress_struct cinfo;
    struct my_error_mgr jerr;

    cinfo.err = jpeg_std_error(&jerr.pub);
    jerr.pub.error_exit = my_error_exit;
    if (setjmp(jerr.setjmp_buffer)) {
            jpeg_destroy_decompress(&cinfo);
            return NULL;
        }

    jpeg_create_decompress(&cinfo);
    jpeg_stdio_src(&cinfo, file);

    //получаем данные о картинке
    jpeg_read_header(&cinfo, TRUE);
    jpeg_start_decompress(&cinfo);

    __android_log_print(ANDROID_LOG_VERBOSE, "jpegwrapper", "A1.6 %d x %d", cinfo.image_width, cinfo.image_height);

        //JPEG не имеет прозрачности так что картинка всегда 3х-байтная (RGB)
//        int row = im.glWidth * 3;
    //    im.data = malloc(row * im.glHeight);
    //
    //    //построчно читаем данные
    //    unsigned char* line = (unsigned char*) (im.data);
    //    while (cinfo.output_scanline < cinfo.output_height) {
    //        jpeg_read_scanlines(&cinfo, &line, 1);
    //        line += row;
    //    }

        //подчищаем
        jpeg_finish_decompress(&cinfo);
        jpeg_destroy_decompress(&cinfo);

        return NULL;

}

