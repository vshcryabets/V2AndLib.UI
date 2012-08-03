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
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.LinkedList;
import java.util.List;

import com.v2soft.AndLib.media.CustomizableRecorder.CustomizableRecorderChannels;
import com.v2soft.AndLib.media.CustomizableRecorder.CustomizableRecorderPCMEncoding;

/**
 * Customizable PCM audio player
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public abstract class CustomizableRawPlayer {   
    public enum StreamType {
        VOICE_CALL, 
        SYSTEM, 
        RING, 
        MUSIC, 
        ALARM, 
        NOTIFICATION 
    }

    private static final String LOG_TAG = CustomizableRawPlayer.class.getSimpleName();
    
    private AudioTrack mPlayer;
    private int mMinimalBufferSize;
    private Thread mWriterThread;
    private List<byte[]> mBuffers;

    public CustomizableRawPlayer() {
        mBuffers = new LinkedList<byte[]>();
    }

    public void prepare(StreamType streamType, int samplerate, 
            CustomizableRecorderChannels channels,
            CustomizableRecorderPCMEncoding encoding) {
        int streamTypeint = 0;
        switch (streamType) {
        case VOICE_CALL:
            streamTypeint = AudioManager.STREAM_VOICE_CALL;
            break;
        case SYSTEM:
            streamTypeint = AudioManager.STREAM_SYSTEM;
            break;
        case RING:
            streamTypeint = AudioManager.STREAM_RING;
            break;
        case ALARM:
            streamTypeint = AudioManager.STREAM_ALARM;
            break;
        case NOTIFICATION:
            streamTypeint = AudioManager.STREAM_NOTIFICATION;
            break;
        case MUSIC:
        default:
            streamTypeint = AudioManager.STREAM_MUSIC;
            break;
        }
        int channelConfig = ( channels == CustomizableRecorderChannels.MONO ? AudioFormat.CHANNEL_OUT_MONO :
            AudioFormat.CHANNEL_OUT_STEREO );
        int audioFormat = ( encoding == CustomizableRecorderPCMEncoding.SIGNED_16BPS ?
                AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT);
        mMinimalBufferSize = AudioTrack.getMinBufferSize(samplerate, channelConfig, audioFormat);
        mPlayer = new AudioTrack(streamTypeint, 
                samplerate, 
                channelConfig, 
                audioFormat, 
                mMinimalBufferSize, 
                AudioTrack.MODE_STREAM);
        mWriterThread = new Thread(mBackgroundReader, LOG_TAG);
    }

    public void stopPlay() {
        if ( mWriterThread.isAlive() ) {
            mWriterThread.interrupt();
        }
    }

    public void startPlay() {
        if ( mPlayer == null ) {
            throw new IllegalStateException("Recorder wasn't prepared");
        }
        mWriterThread.start();
    }

    private Runnable mBackgroundReader = new Runnable() {
        @Override
        public void run() {
            // preload few buffers
            for ( int i =0; i < 3; i ++ ) {
                final byte[] buffer = new byte[mMinimalBufferSize];
                int read = getBuffer(buffer);
                mBuffers.add(buffer);
                if ( read <= 0 ) {
                    break;
                }
            }
            processStartPlay();
            mPlayer.play();
            while ( true ) {
                if ( mWriterThread.isInterrupted() ) {
                    break;
                }
                if ( mBuffers.size() < 1 ) {
                    break;
                }
                byte buffer[] = mBuffers.get(0);
                mPlayer.write(buffer, 0, buffer.length);
                mBuffers.remove(0);
                // reuse buffer
                int read = getBuffer(buffer);
                if ( read > 0 ) {
                    mBuffers.add(buffer);    
                }
                if ( mWriterThread.isInterrupted() ) {
                    break;
                }
            }
            processStopPlay();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    };
    // =====================================================================
    // Abstract methods
    // =====================================================================
    protected abstract int getBuffer(byte[] buffer);
    protected abstract void processStopPlay();
    protected abstract void processStartPlay();
}
