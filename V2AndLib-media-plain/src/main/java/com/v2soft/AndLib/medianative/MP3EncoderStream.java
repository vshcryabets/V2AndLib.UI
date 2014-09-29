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
        void processEncodedBuffer(ByteBuffer buffer);
    }

    public enum LAMEMode {
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
     * @param output
     * @param inSampleRate input sample rate in Hz
     * @param mode         stereo, jstereo, dual channel (not supported), mono default: lame picks based on compression ration and input channels
     */
    public MP3EncoderStream(final OutputStream output,
                            int numberOfChannels,
                            int inSampleRate,
                            int outSampleRate,
                            LAMEMode mode) {
        super(output, BUFFER_SIZE);
        mInternalStream = output;
        mEncoderHandle = nativeOpenEncoder();
        mBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        mInternalStream.close();
        nativeReleaseEncoder(mEncoderHandle);
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {
        mBuffer.clear();
        mBuffer.put(buffer, offset, count);
        // send data to encoder
        nativeWriteEncoder(mEncoderHandle, mBuffer);
    }

    private native int nativeOpenEncoder();

    private native int nativeReleaseEncoder(int handle);

    private native int nativeWriteEncoder(int handle, ByteBuffer buffer);
}
