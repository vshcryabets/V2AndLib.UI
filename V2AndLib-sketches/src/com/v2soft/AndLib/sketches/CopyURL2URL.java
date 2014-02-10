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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Copy any file from source URL to specified file in target URL.
 * Target now supports only file:// scheme.
 * Source was tested with:
 * - android assets
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public class CopyURL2URL extends AsyncTask<Void, Integer, Boolean>{
	private static final String FILE_SCHEME = "file";
	private static final String TAG = CopyURL2URL.class.getSimpleName();
	private static final String ANDROID_ASSETS = "/android_asset/";
	private URL mSource;
	private URL mTarget;
	private Context mContext;

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
			InputStream input = null;
			String path = mSource.getPath();
			if ( path.startsWith(ANDROID_ASSETS)) {
				input = mContext.getAssets().open(mSource.getPath().replace(ANDROID_ASSETS,""));
			} else {
				input = mSource.openStream();
			}
			FileOutputStream output = new FileOutputStream(mTarget.getPath());
			byte [] buffer = new byte[8192];
			int read = 0;
			long total = 0;
			while ( (read = input.read(buffer)) > 0 ) {
				output.write(buffer, 0, read);
				total += read;
			}
			input.close();
			output.close();
			return true;
		} catch (MalformedURLException e) {
		} catch (IOException e) {
			Log.e(TAG, e.toString(), e);

		}
		return false;
	}
}
