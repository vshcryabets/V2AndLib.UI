/*
 * Copyright (C) 2010-2014 V.Shcryabets (vshcryabets@gmail.com)
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

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 * MediaPlayer wrapper.
 * @author V.Shcryabets
 * @version 2.0 (2010-14)
 */
public class CustomizableMediaPlayer
        implements Closeable {
    private static final int MIN_SLEEP = 500;
    private static final String LOG_TAG = CustomizableMediaPlayer.class.getSimpleName();
    private long DEFAULT_POSITION_UPDATE = 333;

    //--------------------------------------------------------------------------------------------
    // Enums
    //--------------------------------------------------------------------------------------------
    /**
     * Player states
     *
     */
    private enum PlayerState {
        PL_UNPREPARED,
        PL_PREPARED,
        PL_PLAYING,
        PL_PAUSED,
        PL_FREE,
        PL_STOPED
    };
    //--------------------------------------------------------------------------------------------
    // Variables
    //--------------------------------------------------------------------------------------------
    private MediaPlayer mMediaPlayer = null;
    private FileInputStream mInputStream = null;
    //    private int mEndPosition = 0;
//    private int mStartPosition = 0;
    private PlayerState mState = PlayerState.PL_FREE;
    private ArrayList<CustomizableMediaPlayerListener> mListeners = new ArrayList<CustomizableMediaPlayerListener>();
    private boolean isProcessing;
    //    private boolean mNeedFadedown = false;
//    private SurfaceHolder mVideoHolder = null;
//    private float mVolume = 1.0f;
//    protected CustomizableMediaPlayerListener mPositionListener;
    protected Context mContext;
    protected ScheduledFuture mPositionUpdater;
    protected Handler mHandler;
    //--------------------------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------------------------
    public CustomizableMediaPlayer(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        isProcessing =true;
    }

    @Override
    public void close() {
        freePlayer();
        isProcessing = false;
    }
    //--------------------------------------------------------------------------------------------
    // event routines
    //--------------------------------------------------------------------------------------------
    public void addListener(CustomizableMediaPlayerListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(CustomizableMediaPlayerListener listener) {
        mListeners.remove(listener);
    }
    //--------------------------------------------------------------------------------------------
    // Private methods
    //--------------------------------------------------------------------------------------------
    private synchronized void freePlayer() {
        try {
            setState(PlayerState.PL_FREE);
            if ( mMediaPlayer != null ) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            if ( mInputStream != null ) {
                mInputStream.close();
                mInputStream = null;
            }
        }
        catch (Exception e) {
            Log.e(LOG_TAG, e.toString(), e);
        }
    }

    private void setState(PlayerState newState) {
        mState = newState;
        switch (newState) {
            case PL_UNPREPARED:
                for (CustomizableMediaPlayerListener listener : mListeners) {
                    listener.onReadyStateChanged(false);
                }
                break;
            case PL_PREPARED:
                for (CustomizableMediaPlayerListener listener : mListeners) {
                    listener.onReadyStateChanged(true);
                }
                break;
            case PL_STOPED:
                // inform listeners
                for (CustomizableMediaPlayerListener listener : mListeners) {
                    listener.onFinishPlay();
                }
                break;
            case PL_PAUSED:
                // inform listeners
                for (CustomizableMediaPlayerListener listener : mListeners) {
                    listener.onPausePlay(true);
                }
                break;
            case PL_PLAYING:
                // inform listeners
                for (CustomizableMediaPlayerListener listener : mListeners) {
                    listener.onBeginPlay();
                }
                break;
        }
    }
    //--------------------------------------------------------------------------------------------
    // Public methods
    //--------------------------------------------------------------------------------------------
    /**
     * Set source media file name
     * @param filename path to local media file.
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    public synchronized void setSourceFile(String filename)
            throws IllegalArgumentException, IllegalStateException, IOException  {
        freePlayer();
        mMediaPlayer = prepareMediaPlayerObject();
        mInputStream = new FileInputStream(filename);
        mMediaPlayer.setDataSource(mInputStream.getFD());
//        setVolume(mVolume);
        setState(PlayerState.PL_UNPREPARED);
        prepare();
    }
    public synchronized void setSourceUri(Uri uri) throws IOException {
        freePlayer();
        mMediaPlayer = prepareMediaPlayerObject();
        mMediaPlayer.setDataSource(mContext, uri);
//        setVolume(mVolume);
        setState(PlayerState.PL_UNPREPARED);
        prepare();
    }

    protected MediaPlayer prepareMediaPlayerObject() {
        MediaPlayer result = new MediaPlayer();
        result.setOnPreparedListener(mPreparedListener);
        result.setOnCompletionListener(mCompletedListener);
        return result;
    }

    public synchronized void play() throws Exception {
        switch ( mState ) {
            case PL_UNPREPARED:
            case PL_STOPED:
                prepare();
            case PL_PREPARED:
//            mMediaPlayer.seekTo(mStartPosition);
            case PL_PAUSED:
                mMediaPlayer.start();
//            mNeedFadedown = false;
                setState(PlayerState.PL_PLAYING);
                break;
        }
    }

    public synchronized void pause() {
        if ( mState != PlayerState.PL_PLAYING ) {
            return;
        }
        mMediaPlayer.pause();
        setState(PlayerState.PL_PAUSED);
    }

    /**
     * Seeks to specified time position.
     * @param pos the offset in milliseconds from the start to seek to
     */
    public synchronized void seek(int pos) {
        if ( ( mState == PlayerState.PL_UNPREPARED ) ||
                ( mState == PlayerState.PL_FREE ) ||
                ( mState == PlayerState.PL_STOPED )
                ) {
            return;
        }
        if ( pos < 0 ) {
            pos = 0;
        }
//        if ( pos < mStartPosition ) {
//            pos = mStartPosition;
//        }
//        if ( mEndPosition > 0 && pos > mEndPosition ) {
//            pos = mEndPosition;
//        }
        mMediaPlayer.seekTo(pos);
        // inform listeners
        for (CustomizableMediaPlayerListener listener : mListeners) {
            listener.onUpdatePosition(pos);
        }
    }

    public synchronized void stop() {
        if ( ( mState != PlayerState.PL_PLAYING ) &&
                ( mState != PlayerState.PL_PAUSED )) {
            return;
        }
//        mEndPosition = 0;
//        mStartPosition = 0;
        try
        {
            mMediaPlayer.stop();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, e.toString(), e);
        }
        setState(PlayerState.PL_STOPED);
    }

    private synchronized void prepare() throws IllegalStateException, IOException {
        if ( ( mState != PlayerState.PL_UNPREPARED ) &&
                ( mState != PlayerState.PL_STOPED )) {
            return;
        }
        // prepare video surface
//        if ( mVideoHolder !=  null ) {
//            mMediaPlayer.setScreenOnWhilePlaying(true);
//            mMediaPlayer.setDisplay(mVideoHolder);
//        }
        mMediaPlayer.prepare();
//        final int duration = mMediaPlayer.getDuration();
//        if ( duration >  0) {
//            mEndPosition = 0;
//        }
        setState(PlayerState.PL_PREPARED);
    }

//    public void setEndPosition(int pos) {
//        if ( pos > 0 ) {
//            if ( pos < mStartPosition ) {
//                return;
//            }
//        }
//        mEndPosition = pos;
//    }
//
//    /**
//     * Specify startposition of player
//     * @param startPosition position in miliseconds
//     */
//    public void setStartPosition(int startPosition)
//    {
//        if ( ( mEndPosition > 0 ) && ( startPosition > mEndPosition )) {
//            return;
//        }
//        mStartPosition = startPosition;
//    }

//    public void fadedown() {
//        mNeedFadedown = true;
//    }

//    @Override
//    public void run() {
//        while ( isProcessing ) {
//            try {
//                Thread.sleep(MIN_SLEEP);
//                if ( mState == PlayerState.PL_PLAYING ) {
//                    final int pos = mMediaPlayer.getCurrentPosition();
//                    if ( ( mEndPosition>0) && ( pos > mEndPosition )) {
//                        // We reached end position
//                        stop();
//                    }
//                    // inform listeners
//                    for (CustomizableMediaPlayerListener listener : mListeners) {
//                        listener.onUpdatePosition(pos);
//                    }
//
//                    if ( mNeedFadedown ) {
//                        // fade down media player volume
//                        for ( int i = 10; i > 0; i-- ) {
//                            mMediaPlayer.setVolume((float)i/10, (float)i/10);
//                            Thread.sleep(200);
//                        }
//                        stop();
//                    }
//                }
//            }
//            catch (Exception e)
//            {
//                Log.e(LOG_TAG, e.toString(), e);
//            }
//        }
//    }

    public boolean isPaused() {return mState == PlayerState.PL_PAUSED;}

//    public void setVideoHolder(SurfaceHolder holder) {
//        mVideoHolder = holder;
//        if ( isReady() ) {
//            stop();
//        }
//    }

    /**
     * Get current position in milliseconds
     * @return Current position of playback
     */
    public int getCurrentPosition() {
        int res = 0;
        try {
            if ( isReady()) {
                res = mMediaPlayer.getCurrentPosition();
            }
        }
        catch (Exception e) {}
        return res;
    }

    /**
     * Get media file duration in milliseconds
     * @return Media file duration in miliseconds
     */
    public int getDuration()
    {
        int res = 0;
        try
        {
            if ( isReady()) {
                res = mMediaPlayer.getDuration();
            }
        }
        catch (Exception e) {}
        return res;
    }

    public boolean isReady()
    {
        return ( ( mState != PlayerState.PL_FREE ) &&
                ( mState != PlayerState.PL_STOPED ) &&
                ( mState != PlayerState.PL_UNPREPARED ));
    }

    public boolean isPlaying()
    {
        return ( mState == PlayerState.PL_PLAYING );
    }

//    public float getVolume()
//    {
//        return mVolume;
//    }

    //    public void setVolume(float volume) {
//        if ( mMediaPlayer != null ) {
//            mMediaPlayer.setVolume(volume, volume);
//        }
//        mVolume = volume;
//    }
    //--------------------------------------------------------------------------------------------
    // OnPrepare listener
    //--------------------------------------------------------------------------------------------
    private OnPreparedListener mPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            setState(PlayerState.PL_PREPARED);
        }
    };
    //--------------------------------------------------------------------------------------------
    // OnCompletionListener listener
    //--------------------------------------------------------------------------------------------
    private OnCompletionListener mCompletedListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer player) {
            try {
                stop();
            }
            catch (Exception e)
            {
                Log.e(LOG_TAG, e.toString(), e);
            }
        }
    };

//    /**
//     * Set position listener.
//     *
//     * @param listener update position listener.
//     */
//    public void setPositionListener(CustomizableMediaPlayerListener listener) {
//        mPositionListener = listener;
//        if ( mPositionUpdater != null ) {
//            mPositionUpdater.cancel(true);
//            mPositionUpdater = null;
//        }
//        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
//        mPositionUpdater = executorService.scheduleWithFixedDelay(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        if ( mState == PlayerState.PL_PLAYING && mMediaPlayer != null) {
//                            final int pos = mMediaPlayer.getCurrentPosition();
//                            mHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mPositionListener.onUpdatePosition(pos);
//                                }
//                            });
//                        }
//                    }
//                },
//                200, //initialDelay
//                getPositionUpdateDelay(), //delay
//                TimeUnit.MILLISECONDS
//        );
//    }
//
//    protected long getPositionUpdateDelay() {
//        return DEFAULT_POSITION_UPDATE;
//    }

}
