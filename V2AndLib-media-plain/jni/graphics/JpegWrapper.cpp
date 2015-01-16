#include "jni.h"
#include <stdio.h>
#include <setjmp.h>
#include "jpeglib.h"
#include "CJPEGEncoder.h"
#include "CJPEGDecoder.h"
#include "JPEGException.h"

METHODDEF(void) my_error_exit(j_common_ptr cinfo) {
    my_error_mgr* myerr = (my_error_mgr*) cinfo->err;
    (*cinfo->err->output_message)(cinfo);
    longjmp(myerr->setjmp_buffer, 1);
}

JNIEXPORT jstring JNICALL nativeGetVersion(JNIEnv * env, jclass c) {
    return env->NewStringUTF("9.0");
}

JNIEXPORT jint JNICALL nativeGetJPEGInfo(JNIEnv* env, jclass c, jstring jniFileName, jobject result) {
    int rescode = ERR_OK;
    try {
        const char* fileName = env->GetStringUTFChars(jniFileName, 0);
        CJPEGDecoder decoder(fileName, 1024*64);
        env->ReleaseStringUTFChars(jniFileName, fileName);

        // get class
        jclass optionsClass = env->FindClass("com/v2soft/AndLib/medianative/JPEGOptions");
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
           throw ERR_CANT_GET_RESULT_CLASS;
        }

        jfieldID fieldWidth = env->GetFieldID(optionsClass, "mWidth", "I");
        jfieldID fieldHeight = env->GetFieldID(optionsClass, "mHeight", "I");
        env->SetIntField(result, fieldWidth, decoder.getWidth());
        env->SetIntField(result, fieldHeight, decoder.getHeight());
    } catch (JPEGException* error) {
        rescode = error->getCode();
    }
    return rescode;
}

JNIEXPORT jint JNICALL nativeCropJPEG(JNIEnv* env, jclass c, jstring input, jintArray cropArea, jstring output) {
    jint result = ERR_OK;
    try {
        const char* fileName = env->GetStringUTFChars(input, 0);
        CJPEGDecoder decoder(fileName, 1024*64);
        env->ReleaseStringUTFChars(input, fileName);

        fileName = env->GetStringUTFChars(output, 0);
        CJPEGEncoder encoder(fileName);
        env->ReleaseStringUTFChars(output, fileName);

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

        int imageHeight = decoder.getHeight();

        if ( fromX >= decoder.getWidth() || tillX >= decoder.getWidth() ) {
            return ERR_INCORRECT_GEOMETRY_PARAMETER;
        }

        if ( fromY >= imageHeight || tillY >= imageHeight ) {
            return ERR_INCORRECT_GEOMETRY_PARAMETER;
        }

        decoder.startDecompress();
        size_t currentLine = 0;
        while (currentLine < imageHeight) {
            decoder.readLine();
            currentLine++;
        }
        decoder.finishDecompress();


    } catch (JPEGException* error) {
        result = error->getCode();
    }
    return result;
}

