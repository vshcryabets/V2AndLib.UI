/*
 * Copyright (C) 2011 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.UI.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import com.v2soft.AndLib.UI.R;

/**
 * Button view that hires onPress event periodically during it is pressed
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class RepeatButton 
extends Button  {
    //-------------------------------------------------------------------------------
    // Constants
    //-------------------------------------------------------------------------------
    protected static final int UPDATE_DELAY_FIRST_MS = 1000;
    protected static final int UPDATE_DELAY_MS = 350;
    protected static final int UPDATE_MINIMAL_DELAY_MS = 30;
    protected static final String LOG_TAG = RepeatButton.class.getSimpleName();
    //-------------------------------------------------------------------------------
    // Listener interface
    //-------------------------------------------------------------------------------
    public interface OnRepeatButton {
        void onPressed(RepeatButton view);
    }
    //-------------------------------------------------------------------------------
    // Class fields
    //-------------------------------------------------------------------------------
    private OnRepeatButton mListener;
    private int mStartDelay;
    private int mDelay;

    public RepeatButton(Context context) {
        this(context,null);
    }

    public RepeatButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // parse attributes
        final TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.RepeatButton,
                0, 0);
        setStartDelay(arr.getInt(R.styleable.RepeatButton_startDelay,UPDATE_DELAY_FIRST_MS));
        setDelay(arr.getInt(R.styleable.RepeatButton_delay,UPDATE_DELAY_MS));
        arr.recycle();

    }

    public OnRepeatButton getListener() {
        return mListener;
    }

    public void setListener(OnRepeatButton mListener) {
        this.mListener = mListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            postDelayed(mRunnable, mStartDelay);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_OUTSIDE:
        case MotionEvent.ACTION_CANCEL:
            boolean val = removeCallbacks(mRunnable);
            if ( mListener != null ) {
                mListener.onPressed(RepeatButton.this);
            }
            break;
        default:
            Log.d(LOG_TAG, ""+action);
        }
        return super.onTouchEvent(event);
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if ( mListener != null ) {
                mListener.onPressed(RepeatButton.this);
            }
            RepeatButton.this.postDelayed(mRunnable, mDelay);
        }
    };
    //-------------------------------------------------------------------------------
    // Getters and setters
    //-------------------------------------------------------------------------------
    public int getStartDelay() {
        return mStartDelay;
    }

    public void setStartDelay(int mStartDelay) {
        this.mStartDelay = mStartDelay;
    }

    public int getDelay() {
        return mDelay;
    }

    public void setDelay(int mDelay) {
        this.mDelay = mDelay;
    }
}
