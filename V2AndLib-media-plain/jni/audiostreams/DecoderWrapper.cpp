#include "Wrapper.h"
#include "MP3DecoderStream.h"
#include "PCMInputStream.h"
#include "AudioStreamException.h"
#include <map>

using namespace AudioHelpers;

static size_t g_lastDecoderHandler = 1;
static std::map<int, MP3DecoderStream*> g_Decoders;

class CallBackDecoderStream : public PCMOutputStream {
protected:
    jobject mCallbackObject;
    JavaVM *mVM;
    jmethodID mMethod;
    jmethodID mSetSampleRate;
    jmethodID mSetChannelsCount;
public:
    CallBackDecoderStream(JNIEnv *env, jobject callback) {
        env->GetJavaVM(&mVM);
        if (callback != NULL) {
            mCallbackObject = env->NewGlobalRef(callback);
            jclass clazz = env->GetObjectClass(mCallbackObject);
            if (clazz == NULL) {
                throw new AudioStreamException("Failed to resolve object class");
            }
            mMethod = env->GetMethodID(clazz, "handleBuffer", "(Ljava/nio/ByteBuffer;)V");
            if (mMethod == NULL) {
                throw new AudioStreamException("Can't find callback method \"handleBuffer\"");
            }
            mSetSampleRate = env->GetMethodID(clazz, "setSampleRate", "(I)V");
            if (mSetSampleRate == NULL) {
                throw new AudioStreamException("Can't find callback method \"setSampleRate\"");
            }
            mSetChannelsCount = env->GetMethodID(clazz, "setChannelsCount", "(I)V");
            if (mSetChannelsCount == NULL) {
                throw new AudioStreamException("Can't find callback method \"setChannelsCount\"");
            }
        } else {
            mCallbackObject = NULL;
        }
    }
    virtual ~CallBackDecoderStream() {
        JNIEnv *environment;
        mVM->GetEnv((void **)&environment, JNI_VERSION_1_6);
        if (mCallbackObject != NULL) {
            environment->DeleteLocalRef(mCallbackObject);
            mCallbackObject = NULL;
        }
    };
    virtual size_t write(void* buffer, size_t count) {
        if ( mCallbackObject == NULL ) {
            return 0;
        }
        JNIEnv *env = prepareEnv();
        jobject byteBuffer = env->NewDirectByteBuffer(buffer, count);
        env->CallVoidMethod(mCallbackObject, mMethod, byteBuffer);
        return count;
    };
    virtual void flush() {};
    virtual void close() {};
    virtual void setInputSampleRate(size_t samplerate) {
        if ( mCallbackObject != NULL ) {
            JNIEnv *env = prepareEnv();
            env->CallVoidMethod(mCallbackObject, mSetSampleRate, samplerate);
        }
    };
    virtual void setInputChannelsCount(size_t channelsCount) {
        if ( mCallbackObject != NULL ) {
            JNIEnv *env = prepareEnv();
            env->CallVoidMethod(mCallbackObject, mSetChannelsCount, channelsCount);
        }
    };
    virtual void setOutputSampleRate(size_t samplerate) {};
    virtual void setOutputChannelsCount(size_t channelsCount) {};
    JNIEnv* prepareEnv() {
        JNIEnv *env;
        int getEnvStat = mVM->GetEnv((void **)&env, JNI_VERSION_1_6);
        if (getEnvStat == JNI_EDETACHED) {
            throw new AudioStreamException("This thread isn't attached to JavaVM");
        } else if (getEnvStat == JNI_EVERSION) {
            throw new AudioStreamException("Unsupported VM version");
        }
        return env;
    }
};

jint nativeOpenDecoder(JNIEnv *env, jobject clazz, jobject callback, jint maxBufferSize) {
    try {
        MP3DecoderStream* decoder = new MP3DecoderStream(new CallBackDecoderStream(env, callback), maxBufferSize);
        int handler = g_lastDecoderHandler++;
        g_Decoders.insert(std::pair<int, MP3DecoderStream*>(handler,decoder));
        return handler;
    } catch (AudioHelpers::AudioStreamException* err) {
        printf("ERR: %s\n", err->what());
    }
    return S_ERR;
}

jint nativeReleaseDecoder(JNIEnv *env, jobject clazz, jint handler) {
    std::map<int, MP3DecoderStream*>::iterator iterator = g_Decoders.find(handler);
    if ( iterator == g_Decoders.end() ) {
        printf("No such handler %d\n\n", handler);
        return ERR_NO_SUCH_HANDLER;
    }
    g_Decoders.erase(iterator);

    if ( iterator->second != NULL ) {
        delete iterator->second;
    }
    return S_OK;
}

jint nativeWriteDecoder(JNIEnv *env, jobject clazz, jint handler, jobject byteBuffer) {
    std::map<int, MP3DecoderStream*>::iterator iterator = g_Decoders.find(handler);
    if ( iterator == g_Decoders.end() ) {
        return ERR_NO_SUCH_HANDLER;
    }
    void *bufferAddr = (void *)env->GetDirectBufferAddress(byteBuffer);
    jlong size = env->GetDirectBufferCapacity(byteBuffer);
    try {
        iterator->second->write(bufferAddr, size);
    } catch (AudioStreamException *err) {
        printf("ERR: %s\n", err->what());
        return ERR_EXCEPTION;
    }
    return S_ERR;
}

