#include "PCMInputStream.h"
#include "AudioStreamException.h"
#include <map>
#include "MP3EncoderStream.h"
#include "Wrapper.h"

__attribute__((constructor)) static void onDlOpen(void) {
}

using namespace AudioHelpers;

static size_t g_lastEncoderHandler = 1;
static std::map<int, MP3EncoderStream*> g_Encoders;

class CallBackOutputStream : public PCMOutputStream {
protected:
    jobject mCallbackObject;
    JavaVM *mVM;
    jmethodID mMethod;
public:
    CallBackOutputStream(JNIEnv *env, jobject callback) {
        env->GetJavaVM(&mVM);
        if (callback != NULL) {
            mCallbackObject = env->NewGlobalRef(callback);
            jclass clazz = env->GetObjectClass(mCallbackObject);
            if (clazz == NULL) {
                throw new AudioStreamException("Failed to resolve object class");
            }
            mMethod = env->GetMethodID(clazz, "processEncodedBuffer", "(Ljava/nio/ByteBuffer;)V");
            if (mMethod == NULL) {
                throw new AudioStreamException("Can't find callback method \"processEncodedBuffer\"");
            }
        } else {
            mCallbackObject = NULL;
        }
    }
    virtual ~CallBackOutputStream() {
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
        JNIEnv *env;
        int getEnvStat = mVM->GetEnv((void **)&env, JNI_VERSION_1_6);
        if (getEnvStat == JNI_EDETACHED) {
            throw new AudioStreamException("This thread isn't attached to JavaVM");
        } else if (getEnvStat == JNI_EVERSION) {
            throw new AudioStreamException("Unsupported VM version");
        }
        jobject byteBuffer = env->NewDirectByteBuffer(buffer, count);
        env->CallVoidMethod(mCallbackObject, mMethod, byteBuffer);
        return count;
    };
    virtual void flush() {};
    virtual void close() {};
    virtual void setInputSampleRate(size_t samplerate) {};
    virtual void setInputChannelsCount(size_t channelsCount) {};
    virtual void setOutputSampleRate(size_t samplerate) {};
    virtual void setOutputChannelsCount(size_t channelsCount) {};
};

jint nativeOpenEncoder(JNIEnv *env, jobject clazz, jobject callback, jint maxBufferSize, jint channelsCount,
                       jint samplerate, jint outSamplerate) {
    try {
        MP3EncoderStream* out = new MP3EncoderStream(new CallBackOutputStream(env, callback), maxBufferSize);
        out->configure(channelsCount, samplerate, outSamplerate);
        int handler = g_lastEncoderHandler++;
        g_Encoders.insert(std::pair<int, MP3EncoderStream*>(handler,out));
        return handler;
    } catch (AudioHelpers::AudioStreamException* err) {
        printf("ERR: %s\n", err->what());
    }
    return S_ERR;
}

jint nativeReleaseEncoder(JNIEnv *env, jobject clazz, jint handler) {
    std::map<int, MP3EncoderStream*>::iterator iterator = g_Encoders.find(handler);
    if ( iterator == g_Encoders.end() ) {
            return ERR_NO_SUCH_HANDLER;
    }
    g_Encoders.erase(iterator);
    MP3EncoderStream *encoder = iterator->second;
    if ( encoder != NULL ) {
        encoder->flush();
        encoder->close();
        PCMOutputStream* substream = encoder->getSubStream();
        delete encoder;
        if ( substream != NULL ) {
            delete substream;
        }
    }
    return S_ERR;
}

jint nativeWriteEncoder(JNIEnv *env, jobject clazz, jint handler, jobject byteBuffer) {
    std::map<int, MP3EncoderStream*>::iterator iterator = g_Encoders.find(handler);
    if ( iterator == g_Encoders.end() ) {
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
