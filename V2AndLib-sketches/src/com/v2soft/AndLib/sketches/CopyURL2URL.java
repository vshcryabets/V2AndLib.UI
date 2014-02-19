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
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.v2soft.AndLib.dataproviders.Cancelable;
import com.v2soft.AndLib.dataproviders.DataStreamWrapper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Copy any file from source URL to specified file in target URL.
 * Target now supports only file:// scheme.
 * Source was tested with:
 * - android assets
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public class CopyURL2URL extends AsyncTask<Void, Long, Boolean> implements Cancelable, DataStreamWrapper.StreamPositionListener {
	private static final String FILE_SCHEME = "file";
	private static final String TAG = CopyURL2URL.class.getSimpleName();
	private URL mSource;
	private URL mTarget;
	private Context mContext;
	private boolean isCanceled;

	public CopyURL2URL(Context context, URL source, URL target) {
		if ( !target.getProtocol().equalsIgnoreCase(FILE_SCHEME)) {
			throw new IllegalArgumentException("Can't copy to non-local file");
		}
		mContext = context;
		mSource = source;
		mTarget = target;
	}
	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			isCanceled = false;
			DataStreamWrapper input = DataStreamWrapper.getStream(mContext, new URI(mSource.toString()));
			FileOutputStream output = new FileOutputStream(mTarget.getPath());
			input.copyToOutputStream(output, this).close();
			output.close();
			return !isCanceled;
		} catch (MalformedURLException e) {
			Log.e(TAG, e.toString(), e);
		} catch (IOException e) {
			Log.e(TAG, e.toString(), e);
		} catch (URISyntaxException e) {
			Log.e(TAG, e.toString(), e);
		}
		return false;
	}

	@Override
	public void cancel() {
		isCanceled = true;
	}

	@Override
	public boolean canBeCanceled() {
		return true;
	}

	@Override
	public boolean isCanceled() {
		return isCanceled;
	}

	@Override
	public void onPositionChanged(long position, long maxPosition) {
		publishProgress(new Long[]{position, maxPosition});
	}
}
