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
package com.v2soft.AndLib.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.v2soft.AndLib.commonandroid.R;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class NumberPickerControl 
extends LinearLayout 
implements TextWatcher {
    //-------------------------------------------------------------------------------
    // Constants
    //-------------------------------------------------------------------------------
    protected static final long UPDATE_DELAY_FIRST_MS = 1000;
    protected static final long UPDATE_DELAY_MS = 350;
    protected static final long UPDATE_MINIMAL_DELAY_MS = 30;
    protected static final String LOG_TAG = NumberPickerControl.class.getSimpleName();
    //-------------------------------------------------------------------------------
    // Listener interface
    //-------------------------------------------------------------------------------
    public interface OnNumberPickerChangeListener {
        void onNumberPickerChanged(NumberPickerControl view, int value);
    }
    //-------------------------------------------------------------------------------
    // Class fields
    //-------------------------------------------------------------------------------
    private OnNumberPickerChangeListener mListener;
	private EditText mEdit;
	private int mMinValue;
	private int mMaxValue;
	private int mStep;
	private int mCurrentValue;
	private View mPressedButton;	
	private long mCurrentDelay;
	
	public NumberPickerControl(Context context) {
		this(context,null);
	}

	public NumberPickerControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// inflate XML
		inflate(getContext(), R.layout.v2andlib_number_picker, this);
		mEdit = (EditText) findViewById(R.id.v2andlib_edit);
		mEdit.addTextChangedListener(this);
		
		findViewById(R.id.v2andlib_btn_decrement).setOnTouchListener(mTouchListener);
		findViewById(R.id.v2andlib_btn_increment).setOnTouchListener(mTouchListener);
		
		// parse attributes
		final TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerControl,
				0, 0);
		setMax(arr.getInt(R.styleable.NumberPickerControl_maxValue,100));
		setMin(arr.getInt(R.styleable.NumberPickerControl_minValue,0));
		setStep(arr.getInt(R.styleable.NumberPickerControl_step,1));
		setProgress(arr.getInt(R.styleable.NumberPickerControl_progress, 0));
		arr.recycle();
	}

	public int getMax() {
		return mMaxValue;
	}

	public void setMax(int mMaxValue) {
		this.mMaxValue = mMaxValue;
	}

	public int getMin() {
		return mMinValue;
	}

	public void setMin(int mMinValue) {
		this.mMinValue = mMinValue;
	}

	public int getStep() {
		return mStep;
	}

	public void setStep(int mStep) {
		this.mStep = mStep;
	}

	public int getProgress() {
		return mCurrentValue;
	}

	/**
	 * Set the current value.
	 * @param value
	 */
	public void setProgress(int value) {
		if ( value > mMaxValue ) value = mMaxValue;
		if ( value < mMinValue ) value = mMinValue;
		mEdit.setText(String.valueOf(value));
		this.mCurrentValue = value;
	}
	
	public OnNumberPickerChangeListener getListener() {
        return mListener;
    }

    public void setListener(OnNumberPickerChangeListener mListener) {
        this.mListener = mListener;
    }

    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPressedButton = v;
                Log.d(LOG_TAG, "Down "+v.toString());
                mCurrentDelay = UPDATE_DELAY_MS;
                v.postDelayed(mRunnable, UPDATE_DELAY_FIRST_MS);
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
                Log.d(LOG_TAG, "Up "+v.toString());
                boolean val = v.removeCallbacks(mRunnable);
                if ( val ) {
                    mRunnable.run();
                }
                mPressedButton = null;
                if ( mListener != null ) {
                    mListener.onNumberPickerChanged(NumberPickerControl.this, mCurrentValue);
                }
                return false;
            }
            return false;
        }
    };
    
	private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if ( mPressedButton == null ) return;
            int id = mPressedButton.getId();
            if ( R.id.v2andlib_btn_decrement == id ) {
                setProgress(mCurrentValue-mStep);
            } else if ( R.id.v2andlib_btn_increment == id) {
                setProgress(mCurrentValue+mStep);
            }
            updateDelay();
            mPressedButton.postDelayed(mRunnable, mCurrentDelay);
        }

        private void updateDelay() {
            if ( mCurrentDelay < UPDATE_MINIMAL_DELAY_MS ) return;
            mCurrentDelay = mCurrentDelay*90/100;
        }
    };
    //-------------------------------------------------------------------------------
    // Text watcher
    //-------------------------------------------------------------------------------
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        String value = s.toString();
        if ( !value.equals("")) {
            try {
                mCurrentValue = Integer.parseInt(value);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
            }
        }
    }
}
