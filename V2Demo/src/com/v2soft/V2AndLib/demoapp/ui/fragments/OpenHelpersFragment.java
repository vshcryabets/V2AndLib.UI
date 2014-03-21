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
package com.v2soft.V2AndLib.demoapp.ui.fragments;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.v2soft.AndLib.dataproviders.AsyncTaskExecutor;
import com.v2soft.AndLib.dataproviders.ITask;
import com.v2soft.AndLib.dataproviders.tasks.CacheHTTPFile;
import com.v2soft.AndLib.dataproviders.tasks.DownloadTask;
import com.v2soft.AndLib.filecache.AndroidFileCache;
import com.v2soft.AndLib.filecache.FileCache;
import com.v2soft.AndLib.sketches.DownloadProgressDialog;
import com.v2soft.AndLib.sketches.HorizontalProgressDialog;
import com.v2soft.AndLib.sketches.OpenHelpers;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;

import java.net.URI;
import java.security.NoSuchAlgorithmException;

/**
 * Fragment that demonstarte work of open helpers.
 * @author vshcryabets@gmail.com
 *
 */
public class OpenHelpersFragment
		extends BaseFragment<DemoApplication, DemoAppSettings> {
	private DownloadProgressDialog mProgressDlg;
	private FileCache mCache;

    public static Fragment newInstance() {
		return new OpenHelpersFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_open_helpers, null);
		registerOnClickListener(new int[]{R.id.openRemotePDF, R.id.openLocalPDF, R.id.clear}, view);
        mCache = new AndroidFileCache.Builder(getActivity()).useExternalCacheFolder("pdf").build();
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.openRemotePDF:
                URI remotePDF = URI.create("https://dl.dropboxusercontent.com/u/18391781/Datasheets/FT232R.pdf");
                showPDFFrom(remotePDF);
				break;
			case R.id.openLocalPDF:
                remotePDF = URI.create("file:///android_asset/BT139_SERIES.pdf");
                showPDFFrom(remotePDF);
				break;
			case R.id.clear:
				mCache.clear();
				break;
		}
	}

    private void showPDFFrom(URI remotePDF) {
        mProgressDlg = new DownloadProgressDialog(getActivity(), 0,
                R.string.title_downloading_document, remotePDF, mCache,
                mDownloadListener);
        mProgressDlg.setSpeedLimit(7*DownloadTask.BYTES_IN_KB);
        mProgressDlg.show();
    }

    private DownloadProgressDialog.OnDownloadFinished mDownloadListener = new DownloadProgressDialog.OnDownloadFinished() {
        @Override
        public void onCanceled(DownloadTask task) {

        }

        @Override
        public void onTaskFinished(DownloadTask task) {
            DownloadTask iTask = (DownloadTask) task;
            if ( iTask.getResult() ) {
                try {
                    String filePath = null;
                    filePath = iTask.getLocalFilePath().getAbsolutePath();
                    OpenHelpers.openLocalPDFFile(getActivity(),
                            Uri.parse("file://"+filePath),
                            R.string.title_select_PDF_application,
                            R.string.error_cant_open);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onTaskFailed(DownloadTask task, Throwable error) {

        }

        @Override
        public void onMessageFromTask(ITask task, Object message) {

        }
    };

    /**
	 * Return sample display name
	 * @return
	 */
	public static String getSampleName() {
		return "Open helpers sample";
	}

}
