/*
 * Copyright (C) 2010 V.Shcryabets (vshcryabets@gmail.com)
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

import java.io.FileInputStream;
import java.util.ArrayList;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * MediaPlayer shell
 * @author V.Shcryabets
 * @version 1.0 (2010-12)
 */
public class CustomizableMediaPlayer implements Runnable, OnCompletionListener
{
	private static final int MIN_SLEEP = 500;
	private static final String LOG_TAG = CustomizableMediaPlayer.class.getSimpleName();
	//--------------------------------------------------------------------------------------------
	// Enums
	//--------------------------------------------------------------------------------------------
	/**
	 * Player states
	 *
	 */
	private enum PlayerState {PL_UNPREPARED,PL_PREPARED,PL_PLAYING,PL_PAUSED,PL_FREE,PL_STOPED};
	//--------------------------------------------------------------------------------------------
	// Variables
	//--------------------------------------------------------------------------------------------
	private MediaPlayer mMediaPlayer = null;
	private FileInputStream mInputStream = null;
	private int mEndPosition;
	private int mStartPosition;
	private PlayerState mState = PlayerState.PL_FREE;
    private ArrayList<CustomizableMediaPlayerListener> mListeners = new ArrayList<CustomizableMediaPlayerListener>();
    private Thread mBackgroundUpdate;
    private boolean isProcessing;
    private boolean mNeedFadedown = false;
    private SurfaceHolder mVideoHolder = null;
    private String mSourceName;
    private float mVolume = 1.0f;
	//--------------------------------------------------------------------------------------------
	// Constructors
	//--------------------------------------------------------------------------------------------
	public CustomizableMediaPlayer()
	{
		mBackgroundUpdate = new Thread(this);
		isProcessing =true;
		mBackgroundUpdate.setPriority(Thread.MIN_PRIORITY);
		mBackgroundUpdate.start();
	}
	
	public void close()
	{
		freePlayer();
		setState(PlayerState.PL_FREE);
		isProcessing = false;
	}
    //--------------------------------------------------------------------------------------------
    // event routines
    //--------------------------------------------------------------------------------------------
    public void addListener(CustomizableMediaPlayerListener listener)
    {
            mListeners.add(listener);
    }

    public void removeListener(CustomizableMediaPlayerListener listener)
    {
            mListeners.remove(listener);
    }
	//--------------------------------------------------------------------------------------------
	// Private methods
	//--------------------------------------------------------------------------------------------
	private synchronized void freePlayer() 
	{
		try
		{
			setState(PlayerState.PL_FREE);
			if ( mMediaPlayer != null )
			{
				mMediaPlayer.stop();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
			if ( mInputStream != null )
			{
				mInputStream.close();
				mInputStream = null;
			}
		}
		catch (Exception e) 
		{
			Log.e(LOG_TAG, e.toString(), e);
		}
	}	
	
	private void setState(PlayerState newState)
	{
		mState = newState;
		switch (newState) {
		case PL_UNPREPARED:
			for (CustomizableMediaPlayerListener listener : mListeners) 
			{
				listener.onReadyStateChanged(false);
			}							
			break;
		case PL_PREPARED:
			for (CustomizableMediaPlayerListener listener : mListeners) 
			{
				listener.onReadyStateChanged(true);
			}							
			break;
		case PL_STOPED:
			// inform listeners
			for (CustomizableMediaPlayerListener listener : mListeners) 
			{
				listener.onFinishPlay();
			}
			break;
		case PL_PAUSED:
			// inform listeners
			for (CustomizableMediaPlayerListener listener : mListeners) 
			{
				listener.onPausePlay(true);
			}
			break;
		case PL_PLAYING:
			// inform listeners
			for (CustomizableMediaPlayerListener listener : mListeners) 
			{
				listener.onBeginPlay();
			}
			break;
		}
	}	
	//--------------------------------------------------------------------------------------------
	// Public methods
	//--------------------------------------------------------------------------------------------
	public synchronized void setSourceFile(String filename) throws Exception
	{
		if ( mMediaPlayer != null ) {
            freePlayer();
        }
		mSourceName = filename;
		mInputStream = new FileInputStream(mSourceName);
		Log.d("MyMediaPlayer::setSourceFile","New Media player, src="+filename);
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setDataSource(mInputStream.getFD());
		setVolume(mVolume);
		setState(PlayerState.PL_UNPREPARED);
		prepare();
	}
	public String getSourceFilename() {return mSourceName;}

	public synchronized void play() throws Exception
	{
		switch ( mState ) {
		case PL_UNPREPARED:
		case PL_STOPED:
			prepare();
		case PL_PREPARED:
			mMediaPlayer.seekTo(mStartPosition);
		case PL_PAUSED:
			mMediaPlayer.start();
			mNeedFadedown = false;
			setState(PlayerState.PL_PLAYING);			
			break;
		}
	}
	
	public synchronized void pause()
	{
		if ( mState != PlayerState.PL_PLAYING ) {
            return;
        }
		mMediaPlayer.pause();
		setState(PlayerState.PL_PAUSED);
	}

	public synchronized void seek(int pos)
	{
		if ( ( mState == PlayerState.PL_UNPREPARED ) ||
			( mState == PlayerState.PL_FREE ) ||
			( mState == PlayerState.PL_STOPED )
			) {
            return;
        }

		if ( pos < mStartPosition ) {
            pos = mStartPosition;
        }
		if ( pos > mEndPosition ) {
            pos = mEndPosition;
        }
		mMediaPlayer.seekTo(pos);
		// inform listeners
		for (CustomizableMediaPlayerListener listener : mListeners) 
		{
			listener.onUpdatePosition(pos);
		}		
	}
	
	public synchronized void stop()
	{
		if ( ( mState != PlayerState.PL_PLAYING ) &&
				( mState != PlayerState.PL_PAUSED )) {
            return;
        }
		mEndPosition = 0;
		mStartPosition = 0;
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
	
	private synchronized void prepare() throws Exception {
		if ( ( mState != PlayerState.PL_UNPREPARED ) &&
				( mState != PlayerState.PL_STOPED )) {
            return;
        }
		// prepare video surface
		if ( mVideoHolder !=  null ) {
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.setDisplay(mVideoHolder);
		}
		mMediaPlayer.prepare();
		final int duration = mMediaPlayer.getDuration();
		if ( duration >  0) {
            if ( mEndPosition <= 0 ) {
                mEndPosition = duration;
            }
            setState(PlayerState.PL_PREPARED);
        }
	}
	
	public void setEndPosition(int pos)
	{
		if ( pos > 0 ) {
            if ( pos < mStartPosition ) {
                return;
            }
        }
		mEndPosition = pos;
	}

	/**
	 * Specify startposition of player
	 * @param i position in miliseconds
	 */
	public void setStartPosition(int i) 
	{
		if ( ( mEndPosition > 0 ) && ( i > mEndPosition )) {
            return;
        }
		mStartPosition = i;
	}
	
	public void fadedown()
	{
		mNeedFadedown = true;
	}

	@Override
	public void run() 
	{
		while ( isProcessing )
		{
			try
			{
				Thread.sleep(MIN_SLEEP);
				if ( mState == PlayerState.PL_PLAYING )
				{
					final int pos = mMediaPlayer.getCurrentPosition();
					//Log.d("MyMediaPlayer::run", "e="+end_position+" p="+pos);
					if ( ( mEndPosition>0) && ( pos > mEndPosition ))
					{
						Log.d("MyMediaPlayer::run", "We reach end position");
						stop();
					}
					// inform listeners
					for (CustomizableMediaPlayerListener listener : mListeners) 
					{
						listener.onUpdatePosition(pos);
					}
					
					if ( mNeedFadedown )
					{
                        // fade down media player volume^M
                        for ( int i = 10; i > 0; i-- )
                        {
                                mMediaPlayer.setVolume((float)i/10, (float)i/10);
                                Thread.sleep(200);
                        }
                        stop();
					}
				}
			}
			catch (Exception e) 
			{
				Log.e(LOG_TAG, e.toString(), e);
			}
		}
	}

	public boolean isPaused() {return mState == PlayerState.PL_PAUSED;}

	public void setVideoHolder(SurfaceHolder holder)
	{
		mVideoHolder = holder;
//		Log.d("setVideoHolder","="+( holder == null ? "null" : holder.toString()));
		if ( isReady() )
		{
			stop();
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) 
	{
		try
		{
			Log.d(LOG_TAG,"onCompletion");
			stop();
		}
		catch (Exception e) 
		{
			Log.e(LOG_TAG, e.toString(), e);
		}
	}

	/**
	 * Get current position in milliseconds
	 * @return Current position of playback
	 */
	public int getCurrentPosition() 
	{
		int res = 0;
		try
		{
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
	
	public float getVolume()
	{
		return mVolume;
	}
	
	public void setVolume(float vol)
	{
		if ( mMediaPlayer != null ) {
            mMediaPlayer.setVolume(vol, vol);
        }
		mVolume = vol;
	}

}
