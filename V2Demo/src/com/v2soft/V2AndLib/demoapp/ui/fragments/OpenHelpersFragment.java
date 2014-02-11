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

import android.accounts.Account;
import android.accounts.AccountManagerFuture;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.v2soft.AndLib.services.AndroidAccountHelper;
import com.v2soft.AndLib.sketches.CopyURL2URL;
import com.v2soft.AndLib.sketches.HorizontalProgressDialog;
import com.v2soft.AndLib.sketches.OpenHelpers;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.V2AndLib.demoapp.providers.DemoListProvider;
import com.v2soft.V2AndLib.demoapp.providers.DemoSyncAdapter;
import com.v2soft.V2AndLib.demoapp.services.DemoAuthService;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Fragment that demonstarte work of open helpers.
 * @author vshcryabets@gmail.com
 *
 */
public class OpenHelpersFragment
		extends BaseFragment<DemoApplication, DemoAppSettings> {
	private HorizontalProgressDialog mProgressDlg;

	public static Fragment newInstance() {
		return new OpenHelpersFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_open_helpers, null);
		registerOnClickListener(new int[]{R.id.openRemotePDF, R.id.openLocalPDF}, view);
		mProgressDlg = new HorizontalProgressDialog(getActivity(), 0,
				R.string.title_downloading_document, R.string.v2andlib_loading, 0, false);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.openRemotePDF:
//				OpenHelpers.openRemotePDFFile(getActivity(),
//						Uri.parse("http://www.w3.org/Protocols/HTTP/1.1/rfc2616.pdf"),//"https://dl.dropboxusercontent.com/u/18391781/Datasheets/FT232R.pdf"),
//						R.string.title_select_PDF_application,
//						R.string.error_cant_open);
				mProgressDlg.setMessage(getString(R.string.v2andlib_loading));
				mProgressDlg.show();
				final File outputFileRemote = new File(getActivity().getExternalCacheDir(), "sample.pdf");
				try {
					final CopyURL2URL copyTask = new CopyURL2URL(getActivity(),
							new URL("https://dl.dropboxusercontent.com/u/18391781/Datasheets/FT232R.pdf"),
							new URL(Uri.fromFile(outputFileRemote).toString())
					){
						@Override
						protected void onProgressUpdate(Long... values) {
							super.onProgressUpdate(values);
							mProgressDlg.setMessage(getString(R.string.v2andlib_downloaded_kb, (int) (values[0] / 1024)));
						}

						@Override
						protected void onPostExecute(Boolean aBoolean) {
							super.onPostExecute(aBoolean);
							mProgressDlg.dismiss();
							if ( aBoolean ) {
								OpenHelpers.openLocalPDFFile(getActivity(),
										Uri.fromFile(outputFileRemote),
										R.string.title_select_PDF_application,
										R.string.error_cant_open);
							}
						}
					};
					copyTask.execute(new Void[0]);
					mProgressDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							copyTask.cancel();
						}
					});
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}				break;
			case R.id.openLocalPDF:
				final File outputFile = new File(getActivity().getExternalCacheDir(), "sample.pdf");
				try {
					CopyURL2URL copyTask = new CopyURL2URL(getActivity(),
							new URL("file:///android_asset/BT139_SERIES.pdf"),
							new URL(Uri.fromFile(outputFile).toString())
					){
						@Override
						protected void onPostExecute(Boolean aBoolean) {
							super.onPostExecute(aBoolean);
							if ( aBoolean ) {
								OpenHelpers.openLocalPDFFile(getActivity(),
										Uri.fromFile(outputFile),
										R.string.title_select_PDF_application,
										R.string.error_cant_open);
							}
						}
					};
					copyTask.execute(new Void[0]);
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
				break;
		}
	}
	/**
	 * Return sample display name
	 * @return
	 */
	public static String getSampleName() {
		return "Open helpers sample";
	}

}
