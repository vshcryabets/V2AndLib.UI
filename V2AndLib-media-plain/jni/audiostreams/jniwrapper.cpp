#include <jni.h>
#include "MP3InputStream.h"
#include "PCMInputStream.h"
#include "AudioStreamException.h"
#include <map>
#include "MP3OutputStream.h"

__attribute__((constructor)) static void onDlOpen(void) {
}

const int S_OK = 1;
const int S_ERR = 0;
const int ERR_NO_SUCH_HANDLER = -2;
const int ERR_EXCEPTION = -3;
const int ERR_NOT_IMPLEMENTED = -4;

using namespace AudioHelpers;

static size_t g_lastHandler = 1;
static std::map<int, PCMInputStream*> g_Decoders;
static std::map<int, MP3OutputStream*> g_Encoders;

jint nativeOpenMP3(JNIEnv *env, jobject clazz, jstring path) {
    try {
        const char *nativeString = env->GetStringUTFChars(path, 0);
        PCMInputStream* fileStream = new MP3InputStream(nativeString);
        env->ReleaseStringUTFChars(path, nativeString);

        int handler = g_lastHandler++;
        g_Decoders.insert(std::pair<int, PCMInputStream*>(handler,fileStream));
        return handler;
    } catch (AudioHelpers::AudioStreamException* err) {
        printf("ERR: %s\n", err->what());
    }
    return S_ERR;
}

jint nativeReleaseMP3(jint handler) {
    std::map<int, PCMInputStream*>::iterator iterator = g_Decoders.find(handler);
    if ( iterator == g_Decoders.end() ) {
        return ERR_NO_SUCH_HANDLER;
    }
    g_Decoders.erase(iterator);

    if ( iterator->second != NULL ) {
        delete iterator->second;
    }
    return S_OK;
}

jint nativeReadMP3(JNIEnv *env, jobject clazz, jbyteArray buffer, jint offset, jint count, jint handler) {
    std::map<int, PCMInputStream*>::iterator iterator = g_Decoders.find(handler);
    if ( iterator == g_Decoders.end() ) {
        return ERR_NO_SUCH_HANDLER;
    }
    try {
        jboolean isCopy = false;
        jbyte* array = env->GetByteArrayElements(buffer, &isCopy);
        PCMInputStream* fileStream = iterator->second;
        jint read = fileStream->read(array+offset, count);
        env->ReleaseByteArrayElements(buffer, array, 0);
        return read;
    } catch (AudioHelpers::AudioStreamException* err) {
        printf("ERR: %s\n", err->what());
    }
    return ERR_EXCEPTION;
}

jint nativeDecoderGetChannelsCount(JNIEnv *env, jobject clazz, jint handler) {
    std::map<int, PCMInputStream*>::iterator iterator = g_Decoders.find(handler);
    if ( iterator == g_Decoders.end() ) {
        return ERR_NO_SUCH_HANDLER;
    }
    return iterator->second->getChannelsCount();
}

jint nativeDecoderGetGetSampleRate(JNIEnv *env, jobject clazz, jint handler) {
    std::map<int, PCMInputStream*>::iterator iterator = g_Decoders.find(handler);
    if ( iterator == g_Decoders.end() ) {
        return ERR_NO_SUCH_HANDLER;
    }
    return iterator->second->getSampleRate();
}

const JNINativeMethod method_decoder_table[] = {
  { "nativeOpen", "(Ljava/lang/String;)I", (void *) nativeOpenMP3 },
  { "nativeRelease", "(I)I", (void *) nativeReleaseMP3 },
  { "nativeRead", "([BIII)I", (void *) nativeReadMP3 },
  { "nativeDecoderGetChannelsCount", "(I)I", (void *) nativeDecoderGetChannelsCount },
  { "nativeDecoderGetGetSampleRate", "(I)I", (void *) nativeDecoderGetGetSampleRate }
};
static int method_decoder_table_size = sizeof(method_decoder_table) / sizeof(method_decoder_table[0]);


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
        MP3OutputStream* out = new MP3OutputStream(new CallBackOutputStream(env, callback), maxBufferSize);
        out->configure(channelsCount, samplerate, outSamplerate);
        int handler = g_lastHandler++;
        g_Encoders.insert(std::pair<int, MP3OutputStream*>(handler,out));
        return handler;
    } catch (AudioHelpers::AudioStreamException* err) {
        printf("ERR: %s\n", err->what());
    }
    return S_ERR;
}

jint nativeReleaseEncoder(JNIEnv *env, jobject clazz, jint handler) {
    std::map<int, MP3OutputStream*>::iterator iterator = g_Encoders.find(handler);
    if ( iterator == g_Encoders.end() ) {
            return ERR_NO_SUCH_HANDLER;
    }
    g_Encoders.erase(iterator);
    MP3OutputStream *encoder = iterator->second;
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
    std::map<int, MP3OutputStream*>::iterator iterator = g_Encoders.find(handler);
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

const JNINativeMethod method_encoder_table[] = {
  { "nativeOpenEncoder", "(Lcom/v2soft/AndLib/medianative/MP3EncoderStream$Callback;IIII)I", (void*) nativeOpenEncoder},
  { "nativeReleaseEncoder", "(I)I", (void*) nativeReleaseEncoder },
  { "nativeWriteEncoder", "(ILjava/nio/ByteBuffer;)I", (void*) nativeWriteEncoder }
};
static int method_encoder_table_size = sizeof(method_encoder_table) / sizeof(method_encoder_table[0]);


jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if ( vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    jclass clazz = env->FindClass( "com/v2soft/AndLib/medianative/MP3DecoderStream");
    if (clazz) {
        jint ret = env->RegisterNatives(clazz, method_decoder_table, method_decoder_table_size);
        env->DeleteLocalRef(clazz);
        if (  ret != 0 ) {
            return JNI_ERR;
        }
    } else {
        return JNI_ERR;
    }

    clazz = env->FindClass( "com/v2soft/AndLib/medianative/MP3EncoderStream");
    if (clazz) {
        jint ret = env->RegisterNatives(clazz, method_encoder_table, method_encoder_table_size);
        env->DeleteLocalRef(clazz);
        if (  ret != 0 ) {
            return JNI_ERR;
        }
    } else {
        return JNI_ERR;
    }

    return  JNI_VERSION_1_6;
}
