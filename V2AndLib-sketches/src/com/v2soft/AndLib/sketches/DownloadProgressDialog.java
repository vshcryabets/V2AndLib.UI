/*
 * Copyright (C) 2014 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.sketches;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;

import com.v2soft.AndLib.dataproviders.AsyncTaskExecutor;
import com.v2soft.AndLib.dataproviders.ITaskListener;
import com.v2soft.AndLib.dataproviders.tasks.CacheHTTPFile;
import com.v2soft.AndLib.dataproviders.tasks.DownloadTask;
import com.v2soft.AndLib.filecache.FileCache;

import java.net.URI;
import java.security.NoSuchAlgorithmException;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class DownloadProgressDialog extends HorizontalProgressDialog {
    public interface OnDownloadFinished extends ITaskListener<DownloadTask, Object> {
        public void onCanceled(DownloadTask task);
    }
    protected CacheHTTPFile mTask;
    protected OnDownloadFinished mListener;
    protected FileCache mCache;
    protected URI mDownloadURI;

	public DownloadProgressDialog(Context context, int theme, int title, int message,
                                  boolean showAfterCreate, URI downloadURI, FileCache cache,
                                  OnDownloadFinished listener) {
		super(context, theme, title, message, 0, showAfterCreate);
        configureButtons();
        setOnDismissListener(mDismissListener);
        mTask = new CacheHTTPFile(context, downloadURI, cache);
        mListener = listener;
        mDownloadURI = downloadURI;
	}

    public void setSpeedLimit(int bytesPerSecondLimit) {
        mTask.setSpeedLimit(bytesPerSecondLimit);
    }

    @Override
    public void show() {
//        try {
//            if ( mCache.isInCache(mDownloadURI)) {
//                DownloadProgressDialog.this.mListener.onTaskFinished(iTask);
//            }
//        } catch (NoSuchAlgorithmException e) {
//        }
        setMessage(getContext().getString(R.string.v2andlib_loading));
        AsyncTaskExecutor executor = new AsyncTaskExecutor<DownloadTask>() {
            @Override
            protected void onProgressUpdate(Object... values) {
                super.onProgressUpdate(values);
                Message message = (Message) values[0];
                if ( message.what == CacheHTTPFile.MSG_RECEIVED_LENGTH_ARRAY ) {
                    long max = ((long[])message.obj)[CacheHTTPFile.ARRAY_MAX];
                    long position = ((long[])message.obj)[CacheHTTPFile.ARRAY_POSITION];
                    setMax((int) (max / DownloadTask.BYTES_IN_KB));
                    setIndeterminate(false);
                    setMessage(getContext().getString(R.string.v2andlib_downloaded_kb,
                            (position) / DownloadTask.BYTES_IN_KB));
                    setProgress((int) (position) / DownloadTask.BYTES_IN_KB);
                }
            }
            @Override
            protected void onPostExecute(DownloadTask iTask) {
                super.onPostExecute(iTask);
                dismiss();
                if ( DownloadProgressDialog.this.mListener != null ) {
                    if (iTask.getResult()) {
                        DownloadProgressDialog.this.mListener.onTaskFinished(iTask);
                    } else {
                        DownloadProgressDialog.this.mListener.onTaskFailed(iTask, null);
                    }
                }
            }
        };
        executor.execute(new DownloadTask[]{mTask});
        super.show();
    }

    /**
     * Configure dialog buttons.
     */
    protected void configureButtons() {
        setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getString(android.R.string.cancel),
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
    }

    private final OnDismissListener mDismissListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            mTask.cancel();
        }
    };

}
