#include "jni.h"
#include <stdio.h>
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
//    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "A1.E2");
    longjmp(myerr->setjmp_buffer, 1);
}

JNIEXPORT jstring JNICALL nativeGetVersion(JNIEnv * env, jclass c) {
    return env->NewStringUTF("9.0");
}

JNIEXPORT jint JNICALL nativeGetJPEGInfo(JNIEnv* env, jclass c, jstring jniFileName, jobject result) {
    int rescode = ERR_OK;
    const char* fileName = env->GetStringUTFChars(jniFileName, 0);
    FILE* file = fopen(fileName, "rb");
    env->ReleaseStringUTFChars(jniFileName, fileName);
    if ( file == NULL ) {
        return ERR_NO_FILE;
    }

    struct jpeg_decompress_struct cinfo;
    struct my_error_mgr jerr;

    try {
        cinfo.err = jpeg_std_error(&jerr.pub);
        jerr.pub.error_exit = my_error_exit;
        if (setjmp(jerr.setjmp_buffer)) {
                jpeg_destroy_decompress(&cinfo);
                throw ERR_JPEG_DECODER;
        }

        jpeg_create_decompress(&cinfo);
        jpeg_stdio_src(&cinfo, file);

        //reading JPEG header
        jpeg_read_header(&cinfo, TRUE);

        // get class
        jclass optionsClass = env->FindClass("com/v2soft/AndLib/medianative/JPEGOptions");
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
           throw ERR_CANT_GET_RESULT_CLASS;
        }

        jfieldID fieldWidth = env->GetFieldID(optionsClass, "mWidth", "I");
        jfieldID fieldHeight = env->GetFieldID(optionsClass, "mHeight", "I");
        env->SetIntField(result, fieldWidth, cinfo.image_width);
        env->SetIntField(result, fieldHeight, cinfo.image_height);

        //free resources
        jpeg_destroy_decompress(&cinfo);
    } catch (int error) {
        rescode = error;
    }
    fclose(file);
    return rescode;
}

JNIEXPORT jint JNICALL nativeCropJPEG(JNIEnv* env, jclass c, jstring input, jintArray cropArea, jstring output) {
    jint result = ERR_OK;
    FILE* file = NULL;
    FILE* outfile = NULL;

    // get crop area
    jsize length = env->GetArrayLength(cropArea);
    if ( length != 4 ) {
        return ERR_INCORRECT_GEOMETRY_PARAMETER;
    }
    jboolean isCopy;
    int *cropAreaInt= env->GetIntArrayElements(cropArea, &isCopy);
    int fromX = cropAreaInt[0];
    int fromY = cropAreaInt[1];
    int tillX = cropAreaInt[2];
    int tillY = cropAreaInt[3];
    env->ReleaseIntArrayElements(cropArea, cropAreaInt, JNI_ABORT);
    if ( fromX < 0 || fromX >= tillX ) {
        return ERR_INCORRECT_GEOMETRY_PARAMETER;
    }
    if ( fromY < 0 || fromY >= tillY ) {
        return ERR_INCORRECT_GEOMETRY_PARAMETER;
    }

    try {
        // prepare input file
        const char* fileName = env->GetStringUTFChars(input, 0);
        file = fopen(fileName, "rb");
        env->ReleaseStringUTFChars(input, fileName);
        if ( file == NULL ) {
            throw ERR_NO_FILE;
        }

        // prepare output file
        fileName = env->GetStringUTFChars(output, 0);
        outfile = fopen(fileName, "w");
        env->ReleaseStringUTFChars(output, fileName);
        if ( outfile == NULL ) {
            throw ERR_CANT_CREATE_FILE;
        }

        struct jpeg_decompress_struct cinfo;
        struct my_error_mgr jerr;

        cinfo.err = jpeg_std_error(&jerr.pub);
        jerr.pub.error_exit = my_error_exit;
        if (setjmp(jerr.setjmp_buffer)) {
                jpeg_destroy_decompress(&cinfo);
                throw ERR_JPEG_DECODER;
            }

        jpeg_create_decompress(&cinfo);
        jpeg_stdio_src(&cinfo, file);

        //reading JPEG header
        jpeg_read_header(&cinfo, TRUE);

        if ( fromX >= cinfo.image_width || tillX >= cinfo.image_width ) {
            throw ERR_INCORRECT_GEOMETRY_PARAMETER;
        }

        if ( fromY >= cinfo.image_height || tillY >= cinfo.image_height ) {
            throw ERR_INCORRECT_GEOMETRY_PARAMETER;
        }

        jpeg_start_decompress(&cinfo);
        size_t row_stride = cinfo.output_width * cinfo.output_components;
        JSAMPARRAY buffer = (*cinfo.mem->alloc_sarray)((j_common_ptr) &cinfo, JPOOL_IMAGE, row_stride, 1);

        while (cinfo.output_scanline < cinfo.output_height) {
            /* jpeg_read_scanlines expects an array of pointers to scanlines.
             * Here the array is only one element long, but you could ask for
             * more than one scanline at a time if that's more convenient.
             */
            (void) jpeg_read_scanlines(&cinfo, buffer, 1);
            /* Assume put_scanline_someplace wants a pointer and sample count. */
//            put_scanline_someplace(buffer[0], row_stride);
        }

        jpeg_finish_decompress(&cinfo);

        //free resources
        jpeg_destroy_decompress(&cinfo);
    } catch (int error) {
        result = error;
    }
    if ( file != NULL ) {
        fclose(file);
    }
    if ( outfile != NULL ) {
        fclose(file);
    }
    return result;
}

