#include "LameWrapper.h"
#include "lame_handler.h"
#include <android/log.h>
#include <map>

unsigned int g_lastHandler = 1;
std::map<int, lamewrapper::LameDataUnit*> g_Handlers;

JNIEXPORT jstring JNICALL Java_com_v2soft_AndLib_media_MP3Helper_getVersion(JNIEnv * env, jclass c) {
    return env->NewStringUTF(get_lame_version());
}

JNIEXPORT jint JNICALL Java_com_v2soft_AndLib_media_MP3Helper_allocateEncoderNative(JNIEnv * env, jclass c,
        jint numberOfChannels, jint inputSampleRate, jint outputSampleRate, jint mode) {
    lamewrapper::LameDataUnit *unit = new lamewrapper::LameDataUnit();
    unit->initialize(numberOfChannels, inputSampleRate, outputSampleRate, static_cast<MPEG_mode>(mode));
    // register data unit at the poll
    int handler = g_lastHandler++;
    g_Handlers.insert(std::pair<int, lamewrapper::LameDataUnit*>(handler,unit));
	return handler;
}

JNIEXPORT jint JNICALL Java_com_v2soft_AndLib_media_MP3Helper_lameEncodeBufferNative(JNIEnv * env,
    jclass c, jbyteArray jInputBuffer, jbyteArray bufferOutput, jint handler) {
    std::map<int, lamewrapper::LameDataUnit*>::iterator iterator = g_Handlers.find(handler);
    if ( jInputBuffer == NULL || bufferOutput == NULL) {
        return ERR_NULL_BUFFER;
    }
    if ( iterator == g_Handlers.end() ) {
        return ERR_NO_SUCH_HANDLER;
    } else {
        // TODO use env->GetByteArrayRegion(array, 0, len, buffer);
        jbyte* inputBuffer = env->GetByteArrayElements(jInputBuffer, 0);
        if (inputBuffer != NULL) {
            size_t inputBufferLength = env->GetArrayLength(jInputBuffer);
            int encodedCount = iterator->second->encodeBuffer((unsigned char*)inputBuffer, NULL, inputBufferLength);
            env->ReleaseByteArrayElements(jInputBuffer, inputBuffer, JNI_ABORT);
            if ( encodedCount < 0 ) {
                __android_log_print(ANDROID_LOG_VERBOSE, "LAME", "A1.6 %d", encodedCount);
                return ERR_LAME_ERROR;
            } else if ( encodedCount > env->GetArrayLength(bufferOutput)) {
                return ERR_SMALL_OUTPUT_BUFFER;
            }
            env->SetByteArrayRegion(bufferOutput, 0, encodedCount, (const jbyte*)iterator->second->getEncoderBuffer());
            return encodedCount;
        } else {
            return ERR_WRONG_INPUT_BUFFER;
        }
    }
}

JNIEXPORT jint JNICALL Java_com_v2soft_AndLib_media_MP3Helper_finishNative(JNIEnv * env, jclass c, jint handler) {
    std::map<int, lamewrapper::LameDataUnit*>::iterator iterator = g_Handlers.find(handler);
    if ( iterator == g_Handlers.end() ) {
        return ERR_NO_SUCH_HANDLER;
    } else {
        delete iterator->second;
        g_Handlers.erase(handler);
    }
	return ERR_OPPERATION_SUCCESS;
}

