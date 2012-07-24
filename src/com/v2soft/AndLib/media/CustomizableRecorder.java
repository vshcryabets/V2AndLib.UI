/*
 * Copyright (C) 2012 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.media;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class CustomizableRecorder {
    private static final int  DEFAULT_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    public static final int SUPPORTED_FRAMERATES[] = {44100,22050,11025,8000};
    private static final String LOG_TAG = CustomizableRecorder.class.getSimpleName();

    protected AudioRecord mRecorder;
    protected int mBufferSizeInBytes;
    public int mSampleRate = CustomizableRecorder.SUPPORTED_FRAMERATES[0];
    protected int mChannels = AudioFormat.CHANNEL_IN_MONO;
    protected int mChannelsCount = 1;
    private ByteBuffer[] mBuffers;
    private int mCurrentBuffer = 0;
    protected int mMaxbuffersCount;
    private Thread mReaderThread;
    protected int mAudioSource = MediaRecorder.AudioSource.DEFAULT;
    protected int mBitsPerSample = 16;

    /**
     * Prepare recorder
     */
    public void prepare() throws IOException {
        for (int sampleRate : SUPPORTED_FRAMERATES) {
            mRecorder = createRecorder(sampleRate);
            if ( mRecorder != null ) {
                mSampleRate = sampleRate;
                break;
            }
        }

        if (mBufferSizeInBytes<0) {
            throw new IllegalStateException("Can't create audio recorder - buffer size below zero");
        }
        mMaxbuffersCount = getMaxBuffersCount();
        mBuffers = new ByteBuffer[mMaxbuffersCount];
        for ( int i = 0; i < mMaxbuffersCount; i++ ) {
            mBuffers[i] = ByteBuffer.allocateDirect(mBufferSizeInBytes); 
        }
        mReaderThread = new Thread(mBackgroundReader, LOG_TAG);
    }


    /**
     * Start recording process
     */
    public void startRecord() {
        if ( mRecorder == null ) {
            throw new IllegalStateException("Recorder wasn't prepared");
        }
        mReaderThread.start();
        mRecorder.startRecording();
    }

    /**
     * Stop recording process
     */
    public void stopRecord() throws IOException {
        if ( mReaderThread.isAlive() ) {
            mReaderThread.interrupt();
        }
        if (mRecorder!=null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }
    /**
     * Sets the number of audio channels for recording. Call this method before prepare(). 
     * Prepare() may perform additional checks on the parameter to make sure whether 
     * the specified number of audio channels are applicable.
     * @param channels the number of audio channels. Usually it is either 1 (mono) or 2 (stereo).
     */
    public void setAudioChannels(int channels) {
        mChannels = channels;
        if ( channels == AudioFormat.CHANNEL_IN_MONO || 
                channels == AudioFormat.CHANNEL_CONFIGURATION_MONO ) {
            mChannelsCount = 1;
        } else {
            mChannelsCount = 2;
        }
    }
    /**
     * Sets the audio source to be used for recording.
     * @param source the audio source to use. Check MediaRecorder.AudioSource
     */
    public void setAudioSource(int source) {
        mAudioSource = source;
    }    
    public boolean isRecording() {
        if ( mRecorder == null ) {
            return false;
        } else {
            return mReaderThread.isAlive();
        }
    }

    private AudioRecord createRecorder(int sampleRate) {
        AudioRecord res = null;
        // Create a new AudioRecord object to record the audio.
        // TODO there is possible bug, mBufferSize may be not in bytes
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRate, 
                mChannels,  DEFAULT_ENCODING);
        res = new AudioRecord(mAudioSource, 
                sampleRate, 
                mChannels, 
                DEFAULT_ENCODING, 
                mBufferSizeInBytes);
        if ( res != null ) {
            Log.d(LOG_TAG, "Initialized recorder "+sampleRate+"x"+
                    mChannels+"x"+DEFAULT_ENCODING+" BS"+mBufferSizeInBytes);
        }
        return res;
    }

    private Runnable mBackgroundReader = new Runnable() {
        @Override
        public void run() {
            while ( true ) {
                if ( mReaderThread.isInterrupted() ) {
                    return;
                }
                final int read = mRecorder.read(mBuffers[mCurrentBuffer], mBufferSizeInBytes);
                if ( read ==  AudioRecord.ERROR_INVALID_OPERATION  ) {
                    processError(read, "ERROR_INVALID_OPERATION: Denotes a failure due to the improper use of a method.");
                    break;
                }
                if ( read ==  AudioRecord.ERROR_BAD_VALUE   ) {
                    processError(read, "ERROR_BAD_VALUE: Denotes a failure due to the use of an invalid value.");
                    break;
                }
                if ( mReaderThread.isInterrupted() ) {
                    return;
                }
                processBuffer(mBuffers[mCurrentBuffer], read);
                mCurrentBuffer++;
                if ( mCurrentBuffer <= mMaxbuffersCount ) {
                    mCurrentBuffer = 0;
                }
            }
        }
    };

    /**
     * 
     * @return how many buffers will be used, at least 2.
     */
    protected abstract int getMaxBuffersCount();
    protected abstract void processBuffer(ByteBuffer buffer, int read);
    protected abstract void processError(int code, String someText);
}
