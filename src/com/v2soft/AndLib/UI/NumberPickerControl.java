package com.v2soft.AndLib.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class NumberPickerControl extends LinearLayout {
	private EditText mEdit;
	private int mMinValue;
	private int mMaxValue;
	private int mStep;
	private int mCurrentValue;
	
	public NumberPickerControl(Context context) {
		this(context,null);
	}

	public NumberPickerControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		// inflate XML
		inflate(getContext(), R.layout.v2andlib_number_picker, this);
		mEdit = (EditText) findViewById(R.id.v2andlib_edit);
		findViewById(R.id.v2andlib_btn_decrement).setOnClickListener(mListener);
		findViewById(R.id.v2andlib_btn_increment).setOnClickListener(mListener);
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
		mEdit.setText(value+"");
		this.mCurrentValue = value;
	}
	
	private OnClickListener mListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if ( id == R.id.v2andlib_btn_decrement ) {
				setProgress(mCurrentValue-mStep);
			} else if ( id == R.id.v2andlib_btn_increment) {
				setProgress(mCurrentValue+mStep);
			}
		}
	};
}
