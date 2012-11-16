package com.v2soft.AndLib.ui.dialogs;

import com.v2soft.AndLib.dataproviders.ITask;
import com.v2soft.AndLib.dataproviders.ITaskHub;
import com.v2soft.AndLib.dataproviders.ITaskListener;
import com.v2soft.AndLib.ui.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.content.DialogInterface.OnCancelListener;;;

/**
 * Dialog that show intermediate progress and cancel button
 * @author vshcryabets@gmail.com
 *
 */
public class BlockingProcessDialog 
extends Dialog 
implements android.view.View.OnClickListener, OnCancelListener {
    private ITask mAssociatedTask;
    private ITaskHub mTaskHub;
    private ITaskListener mListener;
    private Button mBtnCancel;
    private TextView mTxtMessage;

    public BlockingProcessDialog(Context context, ITaskListener listener, ITaskHub hub, ITask task) {
        super(context);
        mTaskHub = hub;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.v2andlib_dialog_progress);
        mBtnCancel = (Button) findViewById(R.id.btnCancle);
        mBtnCancel.setOnClickListener(this);
        mTxtMessage = (TextView) findViewById(R.id.txtDescription);
        setOnCancelListener(this);
        setTask(task);
    }

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
            mTaskHub.cancelTask(mListener, mAssociatedTask, true);
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
