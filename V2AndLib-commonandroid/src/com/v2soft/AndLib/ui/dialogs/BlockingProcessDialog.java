/*
 * Copyright (C) 2012-2013 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.ui.dialogs;

import com.v2soft.AndLib.commonandroid.R;
import com.v2soft.AndLib.dataproviders.ITask;
import com.v2soft.AndLib.dataproviders.ITaskListener;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.DialogInterface.OnCancelListener;

/**
 * Dialog that show intermediate or normal progress and cancel button
 * @author vshcryabets@gmail.com
 *
 */
public class BlockingProcessDialog 
extends Dialog 
implements android.view.View.OnClickListener, OnCancelListener {
    private ITask mAssociatedTask;
    private ITaskListener mListener;
    private Button mBtnCancel;
    private TextView mTxtMessage;
    private ProgressBar mProgress, mIndeterminateProgress;

    public BlockingProcessDialog(Context context, ITaskListener listener, ITask task) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.v2andlib_dialog_progress);
        mProgress = (ProgressBar) findViewById(R.id.v2andlib_progress);
        mIndeterminateProgress = (ProgressBar) findViewById(R.id.v2andlib_indeterminate_progress);
        mBtnCancel = (Button) findViewById(R.id.btnCancle);
        mBtnCancel.setOnClickListener(this);
        mTxtMessage = (TextView) findViewById(R.id.txtDescription);
        setOnCancelListener(this);
        setTask(task);
    }
    
    /**
     * Set the range of the progress bar to 0...max.
     * @param value
     */
    public void setMax(int value) {
        mProgress.setMax(value);
        if ( value > 0 ) {
            mIndeterminateProgress.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);
        } else {
            mIndeterminateProgress.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
        }
    }

    /**
     * Set the current progress to the specified value. Does not do anything if the dialog is in indeterminate mode.
     * @param value
     */
    public void setProgress(int value) {
        mProgress.setProgress(value);
    }

    /**
     * Set Cancel button text
     * @param text
     */
    public void setCancelButtonText(CharSequence text) {
        mBtnCancel.setText(text);
    }
    
    public void setMessage(CharSequence text) {
        mTxtMessage.setText(text);
    }
    
    @Override
    public void onClick(View arg0) {
        cancel();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if ( mAssociatedTask != null ) {
			mAssociatedTask.cancel();
        }
    }

    public void setTask(ITask task) {
        mAssociatedTask = task;
        if ( task == null ) {
            mBtnCancel.setVisibility(View.GONE);
        } else {
            mBtnCancel.setVisibility(View.VISIBLE);
        }
    }
}
