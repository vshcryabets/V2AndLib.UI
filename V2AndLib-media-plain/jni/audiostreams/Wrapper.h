#ifndef WRAPPER_H
#define WRAPPER_H

#include <jni.h>

const int S_OK = 1;
const int S_ERR = 0;
const int ERR_NO_SUCH_HANDLER = -2;
const int ERR_EXCEPTION = -3;
const int ERR_NOT_IMPLEMENTED = -4;

jint nativeOpenDecoder(JNIEnv *env, jobject clazz, jobject callback, jint maxBufferSize);
jint nativeReleaseDecoder(JNIEnv *env, jobject clazz, jint handler);
jint nativeWriteDecoder(JNIEnv *env, jobject clazz, jint handler, jobject byteBuffer);

const JNINativeMethod method_decoder_table[] = {
  { "nativeOpenDecoder", "(Lcom/v2soft/AndLib/medianative/MP3DecoderStream$Callback;I)I", (void *) nativeOpenDecoder },
  { "nativeReleaseDecoder", "(I)I", (void *) nativeReleaseDecoder },
  { "nativeWriteDecoder", "(ILjava/nio/ByteBuffer;)I", (void *) nativeWriteDecoder }
};
static int method_decoder_table_size = sizeof(method_decoder_table) / sizeof(method_decoder_table[0]);

jint nativeOpenEncoder(JNIEnv *env, jobject clazz, jobject callback, jint maxBufferSize, jint channelsCount,
                       jint samplerate, jint outSamplerate, jint encodingMode);
jint nativeReleaseEncoder(JNIEnv *env, jobject clazz, jint handler);
jint nativeWriteEncoder(JNIEnv *env, jobject clazz, jint handler, jobject byteBuffer);

const JNINativeMethod method_encoder_table[] = {
  { "nativeOpenEncoder", "(Lcom/v2soft/AndLib/medianative/MP3EncoderStream$Callback;IIIII)I",(void*)nativeOpenEncoder},
  { "nativeReleaseEncoder", "(I)I", (void*) nativeReleaseEncoder },
  { "nativeWriteEncoder", "(ILjava/nio/ByteBuffer;)I", (void*) nativeWriteEncoder }
};
static int method_encoder_table_size = sizeof(method_encoder_table) / sizeof(method_encoder_table[0]);

#endif // WRAPPER_H
