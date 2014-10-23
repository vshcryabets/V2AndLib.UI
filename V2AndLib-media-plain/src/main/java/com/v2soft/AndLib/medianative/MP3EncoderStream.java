/*
 * Copyright (C) 2014 V.Shcryabets (vshcryabets@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.v2soft.AndLib.medianative;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author V.Shcryabets (vshcryabets@gmail.com)
 */
public class MP3EncoderStream extends BufferedOutputStream {
    private static final int BUFFER_SIZE = 8192;



    public interface Callback {
        void processEncodedBuffer(ByteBuffer buffer) throws IOException;
    }

    public enum EncodingMode {
        stereo,
        jstereo,
        dualchannel, // LAME doesn't supports this!
        mono,
        not_set,
        max_indicator // Don't use this! It's used for sanity checks.
    };

    protected OutputStream mInternalStream;
    protected int mEncoderHandle;
    protected ByteBuffer mBuffer;

    /**
     * @param output output stream
     * @param numberOfChannels number of channels
     * @param inSampleRate input sample rate in Hz
     * @param outSampleRate output sample rate
     * @param mode stereo, jstereo, dual channel (not supported), mono default: lame picks based on
     *             compression ration and input channels
     */
    public MP3EncoderStream(final OutputStream output,
                            int numberOfChannels,
                            int inSampleRate,
                            int outSampleRate,
                            EncodingMode mode) {
        this(output);
        setEncodingParams(numberOfChannels, inSampleRate, outSampleRate, mode);
    }

    /**
     * @param output           output stream
     */
    public MP3EncoderStream(final OutputStream output) {
        super(output, BUFFER_SIZE);
        mInternalStream = output;
        mBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    /**
     * @param numberOfChannels number of channels
     * @param inSampleRate input sample rate in Hz
     * @param outSampleRate output sample rate
     * @param mode stereo, jstereo, dual channel (not supported), mono default: lame picks based on
     *             compression ration and input channels
     */
    public void setEncodingParams(int numberOfChannels,
                                  int inSampleRate,
                                  int outSampleRate,
                                  EncodingMode mode) {
        mEncoderHandle = nativeOpenEncoder(mCallback, BUFFER_SIZE, numberOfChannels, inSampleRate, outSampleRate,
            mode.ordinal());
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        if ( mEncoderHandle != 0 ) {
            mInternalStream.close();
            int res = nativeReleaseEncoder(mEncoderHandle);
            checkError(res);
        }
        mEncoderHandle = 0;
    }

    @Override
    public synchronized void flush() throws IOException {
        nativeEncoderFlush(mEncoderHandle);
        super.flush();
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {
        while ( count > 0 ) {
            mBuffer.clear();
            int blockSize = ( count > mBuffer.remaining() ? mBuffer.remaining() : count );
            mBuffer.put(buffer, offset, blockSize);
            // send data to encoder
            int res = nativeWriteEncoder(mEncoderHandle, mBuffer, blockSize);
            checkError(res);
            count -= blockSize;
            offset += blockSize;
        }
    }

    private void checkError(int res) throws IOException {
        switch (res) {
            case -2:
                throw new IllegalStateException("Wrong decoder handler (-2)");
            case -3:
                throw new IOException("Internal decoder exception (-3)");
            case -4:
                throw new IOException("Not implemented (-4)");
            case 0:
            default:
                break;
        }
    }

    private final Callback mCallback = new Callback() {
        @Override
        public void processEncodedBuffer(ByteBuffer buffer) throws IOException {
            byte[] bytebuffer = new byte[buffer.remaining()]; // TODO reuse buffer
            buffer.get(bytebuffer);
            mInternalStream.write(bytebuffer);
        }
    };

    /**
     * Native routines.
     */
    private native int nativeOpenEncoder(Callback callback, int maxBufferSize, int channelsCount,
                                         int sampleRate, int outSamplerate, int encodingMode);
    private native int nativeReleaseEncoder(int handle);
    private native int nativeWriteEncoder(int handle, ByteBuffer buffer, int size);
    private native void nativeEncoderFlush(int handle);
}
