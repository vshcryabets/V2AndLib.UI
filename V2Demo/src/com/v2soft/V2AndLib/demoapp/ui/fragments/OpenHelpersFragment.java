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

import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.v2soft.AndLib.dataproviders.AsyncTaskExecutor;
import com.v2soft.AndLib.dataproviders.DataStreamWrapper;
import com.v2soft.AndLib.dataproviders.tasks.CacheHTTPFile;
import com.v2soft.AndLib.dataproviders.tasks.DownloadTask;
import com.v2soft.AndLib.filecache.AndroidFileCache;
import com.v2soft.AndLib.filecache.FileCache;
import com.v2soft.AndLib.filecache.MD5CacheFactory;
import com.v2soft.AndLib.sketches.CopyURL2URL;
import com.v2soft.AndLib.sketches.HorizontalProgressDialog;
import com.v2soft.AndLib.sketches.OpenHelpers;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * Fragment that demonstarte work of open helpers.
 * @author vshcryabets@gmail.com
 *
 */
public class OpenHelpersFragment
		extends BaseFragment<DemoApplication, DemoAppSettings> {
	private HorizontalProgressDialog mProgressDlg;
	private FileCache mCache;

	public static Fragment newInstance() {
		return new OpenHelpersFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_open_helpers, null);
		registerOnClickListener(new int[]{R.id.openRemotePDF, R.id.openLocalPDF, R.id.clear}, view);
		mProgressDlg = new HorizontalProgressDialog(getActivity(), 0,
				R.string.title_downloading_document, R.string.v2andlib_loading, 0, false);
		AndroidFileCache.Builder builder = new AndroidFileCache.Builder(getActivity());
		builder.useExternalCacheFolder("pdf");
		mCache = builder.build();
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
        mProgressDlg.setMessage(getString(R.string.v2andlib_loading));
        mProgressDlg.show();
        final CacheHTTPFile downloadTask = new CacheHTTPFile(getActivity(), remotePDF, mCache);
        AsyncTaskExecutor executor = new AsyncTaskExecutor<DownloadTask>() {
            @Override
            protected void onProgressUpdate(Object... values) {
                super.onProgressUpdate(values);
                Message message = (Message) values[0];
                if ( message.what == CacheHTTPFile.MSG_CONTENT_LENGTH ) {
                    mProgressDlg.setMax((int) (((Long)message.obj) / 1024));
                } else if ( message.what == CacheHTTPFile.MSG_RECEIVED_LENGTH ) {
                    mProgressDlg.setMessage(getString(R.string.v2andlib_downloaded_kb,
                            ((Long)message.obj) / 1024));
                    mProgressDlg.setProgress((int) (((Long)message.obj) / 1024));
                }
            }
            @Override
            protected void onPostExecute(DownloadTask iTask) {
                super.onPostExecute(iTask);
                mProgressDlg.dismiss();
                if ( iTask.getResult() ) {
                    String filePath = iTask.getLocalFilePath().getAbsolutePath();
                    OpenHelpers.openLocalPDFFile(getActivity(),
                            Uri.parse("file://"+filePath),
                            R.string.title_select_PDF_application,
                            R.string.error_cant_open);
                }
            }
        };
        executor.execute(new DownloadTask[]{downloadTask});
        mProgressDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel();
            }
        });
    }

    /**
	 * Return sample display name
	 * @return
	 */
	public static String getSampleName() {
		return "Open helpers sample";
	}

}
