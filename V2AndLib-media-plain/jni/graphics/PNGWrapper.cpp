#include "jni.h"
#include <stdio.h>
#include "CPNGDecoder.h"
#include "JPEGException.h"
#include <string.h>
#include <stdint.h>

JNIEXPORT jbyteArray JNICALL nativeLoadPNG(JNIEnv* env, jclass c, jstring input, jintArray cropArea) {
    try {
        const char* fileName = env->GetStringUTFChars(input, 0);
        CPNGDecoder decoder(fileName, 1024*64);
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
