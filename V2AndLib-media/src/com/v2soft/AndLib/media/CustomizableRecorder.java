/*
 * Copyright (C) 2012-2015 V.Shcryabets (vshcryabets@gmail.com)
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
 * Customizable PCM audio recorder
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class CustomizableRecorder {
    public enum CustomizableRecorderChannels {
        MONO, STEREO
    };
    public enum CustomizableRecorderPCMEncoding {
        SIGNED_16BPS, SIGNED_8BPS
    };

    public static final int PCM_FRAME_RATE_44100 = 44100;
    public static final int PCM_FRAME_RATE_22050 = 22050;
    public static final int PCM_FRAME_RATE_16000 = 16000;
    public static final int PCM_FRAME_RATE_11025 = 11025;
    public static final int PCM_FRAME_RATE_8000 = 8000;

    public static final int SUPPORTED_FRAMERATES[] = {PCM_FRAME_RATE_44100, PCM_FRAME_RATE_22050,
            PCM_FRAME_RATE_16000, PCM_FRAME_RATE_11025, PCM_FRAME_RATE_8000};
    private static final String LOG_TAG = CustomizableRecorder.class.getSimpleName();

    private AudioRecord mRecorder;
    protected int mBufferSizeInBytes;
    public int mSampleRate = CustomizableRecorder.SUPPORTED_FRAMERATES[0];
    private ByteBuffer[] mBuffers;
    private int mCurrentBuffer = 0;
    protected int mMaxbuffersCount;
    private Thread mReaderThread;
    protected int mAudioSource = MediaRecorder.AudioSource.DEFAULT;

    /**
     * Prepare recorder, autoselect samplerate
     */
    public void prepare(CustomizableRecorderChannels channels,
            CustomizableRecorderPCMEncoding encoding) throws IOException {
        prepare(SUPPORTED_FRAMERATES, channels, encoding);
    }

    /**
     * Prepare recorder with
     */
    public void prepare(int [] prefferedSampleRates,
            CustomizableRecorderChannels channels,
            CustomizableRecorderPCMEncoding encoding) throws IOException {
        for (int sampleRate : prefferedSampleRates ){
            mRecorder = createRecorder(sampleRate, channels, encoding);
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
    public void startRecord() throws IOException {
        if ( mRecorder == null ) {
            throw new IllegalStateException("Recorder wasn't prepared");
        }
        mReaderThread.start();
    }

    /**
     * Stop recording process
     */
    public void stopRecord() throws IOException {
        if ( mReaderThread.isAlive() ) {
            mReaderThread.interrupt();
        }
    }
    // =============================================================================
    // Getters
    // =============================================================================
    /**
     * @return Recorder samplerate
     */
    public int getSampleRate() {
        return mSampleRate;
    }
    // =============================================================================
    // Setters
    // =============================================================================
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

    private AudioRecord createRecorder(int sampleRate, CustomizableRecorderChannels channels,
            CustomizableRecorderPCMEncoding encoding) {
        AudioRecord res = null;
        int channelConfig = ( channels == CustomizableRecorderChannels.MONO ? AudioFormat.CHANNEL_IN_MONO :
            AudioFormat.CHANNEL_IN_STEREO );
        int audioFormat = ( encoding == CustomizableRecorderPCMEncoding.SIGNED_16BPS ?
                AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT);
        // Create a new AudioRecord object to record the audio.
        // TODO there is possible bug, mBufferSize may be not in bytes
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRate, 
                channelConfig,  audioFormat);
        res = new AudioRecord(mAudioSource, 
                sampleRate, 
                channelConfig, 
                audioFormat, 
                mBufferSizeInBytes);
        if ( res != null ) {
            Log.d(LOG_TAG, "Initialized recorder "+sampleRate+"x"+
                    channelConfig+"x"+audioFormat+" BS"+mBufferSizeInBytes);
        }
        return res;
    }

    private Runnable mBackgroundReader = new Runnable() {
        @Override
        public void run() {
            processStartRecord();
            mRecorder.startRecording();
            while ( true ) {
                if ( mReaderThread.isInterrupted() ) {
                    break;
                }
                final int read = mRecorder.read(mBuffers[mCurrentBuffer], mBufferSizeInBytes);
                if ( read ==  AudioRecord.ERROR_INVALID_OPERATION  ) {
                    processError(read, 
                            "ERROR_INVALID_OPERATION: Denotes a failure due to the improper use of a method.");
                    break;
                }
                if ( read ==  AudioRecord.ERROR_BAD_VALUE   ) {
                    processError(read, 
                            "ERROR_BAD_VALUE: Denotes a failure due to the use of an invalid value.");
                    break;
                }
                if ( mReaderThread.isInterrupted() ) {
                    break;
                }
                if ( mCurrentBuffer >= mBuffers.length ) {
                    mCurrentBuffer = 0;
                }
                processBuffer(mBuffers[mCurrentBuffer], read);
                mCurrentBuffer++;
                if ( mCurrentBuffer >= mBuffers.length ) {
                    mCurrentBuffer = 0;
                }
            }
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            processStopRecord();
        }
    };
    // =====================================================================
    // Abstract methods
    // =====================================================================
    /**
     * 
     * @return how many buffers will be used, at least 2.
     */
    protected abstract int getMaxBuffersCount();
    protected abstract void processBuffer(ByteBuffer buffer, int read);
    protected abstract void processError(int code, String someText);
    protected abstract void processStartRecord();
    protected abstract void processStopRecord();
}
