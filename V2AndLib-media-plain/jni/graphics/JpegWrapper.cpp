#include "jni.h"
#include <stdio.h>
#include <setjmp.h>
#include "jpeglib.h"
#include "CJPEGEncoder.h"
#include "CJPEGDecoder.h"
#include "JPEGException.h"
#include <string.h>
#include <stdint.h>

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

JNIEXPORT jint JNICALL nativeCropJPEG(JNIEnv* env, jclass c, jstring input, jintArray cropArea, jstring output,
    jint quality) {
    jint result = ERR_OK;
    try {
        const char* fileName = env->GetStringUTFChars(input, 0);
        CJPEGDecoder decoder(fileName, 1024*64);
        env->ReleaseStringUTFChars(input, fileName);

        // get crop area
        jsize length = env->GetArrayLength(cropArea);
        if ( length != 4 ) {
            return ERR_INCORRECT_GEOMETRY_PARAMETER;
        }
        int *cropAreaInt= env->GetIntArrayElements(cropArea, NULL);
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

        if ( fromX >= decoder.getWidth() || tillX > decoder.getWidth() ) {
            return ERR_INCORRECT_GEOMETRY_PARAMETER;
        }

        if ( fromY >= imageHeight || tillY > imageHeight ) {
            return ERR_INCORRECT_GEOMETRY_PARAMETER;
        }

        fileName = env->GetStringUTFChars(output, 0);
        CJPEGEncoder encoder(fileName, tillX - fromX, tillY - fromY, quality);
        env->ReleaseStringUTFChars(output, fileName);

        decoder.startDecompress();
        encoder.startCompress();

        size_t currentLine = 0;
        size_t croppedLineSize = (tillX - fromX) * decoder.getOutputComponents();
        size_t cropOffset = fromX * decoder.getOutputComponents();
        while (currentLine < imageHeight) {
            decoder.readLine();
            if ( (currentLine >= fromY) && (currentLine < tillY)) {
                void *buffer = ((char*)decoder.getLineBuffer() + cropOffset);
                encoder.writeLine(&buffer, croppedLineSize, 1);
            }
            currentLine++;
        }
        encoder.finishCompress();
        decoder.finishDecompress();


    } catch (JPEGException* error) {
        result = error->getCode();
    }
    return result;
}

JNIEXPORT jbyteArray JNICALL nativeLoadJPEG(JNIEnv* env, jclass c, jstring input, jintArray cropArea) {
    try {
        const char* fileName = env->GetStringUTFChars(input, 0);
        CJPEGDecoder decoder(fileName, 1024*64);
        env->ReleaseStringUTFChars(input, fileName);

        int imageHeight = decoder.getHeight();
        int fromX = 0;
        int fromY = 0;
        int tillX = decoder.getWidth();
        int tillY = decoder.getHeight();
        if (cropArea != NULL) {
            int *cropAreaInt= env->GetIntArrayElements(cropArea, NULL);
            fromX = cropAreaInt[0];
            fromY = cropAreaInt[1];
            tillX = cropAreaInt[2];
            tillY = cropAreaInt[3];
            env->ReleaseIntArrayElements(cropArea, cropAreaInt, JNI_ABORT);
            if ( fromX < 0 || fromX >= tillX ) {
                return NULL;
            }
            if ( fromY < 0 || fromY >= tillY ) {
                return NULL;
            }
            if ( fromX >= decoder.getWidth() || tillX > decoder.getWidth() ) {
                return NULL;
            }
            if ( fromY >= imageHeight || tillY > imageHeight ) {
                return NULL;
            }
        }

        decoder.startDecompress();
        size_t bufferSize = (tillX-fromX)*(tillY-fromY)*decoder.getOutputComponents();
        jbyte *outputBuffer = new jbyte[bufferSize];

        size_t currentLine = 0;
        off_t outputPosition = 0;
        size_t croppedLineSize = (tillX - fromX) * decoder.getOutputComponents();
        size_t cropOffset = fromX * decoder.getOutputComponents();

        while (currentLine < imageHeight) {
            decoder.readLine();
            if ( (currentLine >= fromY) && (currentLine < tillY)) {
                void *buffer = ((char*)decoder.getLineBuffer() + cropOffset);
                memcpy(outputBuffer + outputPosition, buffer, croppedLineSize);
                outputPosition += croppedLineSize;
            }
            currentLine++;
        }
        decoder.finishDecompress();
        jbyteArray result = env->NewByteArray(bufferSize);
        env->SetByteArrayRegion (result, 0, bufferSize, outputBuffer);
        delete [] outputBuffer;
        return result;
    } catch (JPEGException* error) {

    }
    return NULL;
}

JNIEXPORT jint JNICALL nativeSaveJPEG(JNIEnv* env, jclass c, jstring output, jbyteArray data,
    jint width, jint height, jint quality) {
    jint result = ERR_OK;
    try {
        const char* fileName = env->GetStringUTFChars(output, 0);
        CJPEGEncoder encoder(fileName, width, height, quality);
        env->ReleaseStringUTFChars(output, fileName);
        encoder.startCompress();
        jbyte *inputBuffer = (jbyte *)env->GetByteArrayElements(data, NULL);
        size_t currentLine = 0;
        size_t croppedLineSize = width * 3;
        while (currentLine < height) {
            void *buffer = ((char*)inputBuffer + croppedLineSize * currentLine);
            encoder.writeLine(&buffer, croppedLineSize, 1);
            currentLine++;
        }
        env->ReleaseByteArrayElements(data, inputBuffer, JNI_ABORT);
        encoder.finishCompress();
    } catch (JPEGException* error) {
        result = error->getCode();
    }
    return result;
}
