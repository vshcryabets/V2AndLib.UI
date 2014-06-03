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
package com.v2soft.AndLib.dataproviders;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.v2soft.AndLib.streams.StreamHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 */
public class AndroidStreamHelper extends StreamHelper {
	private static final String ANDROID_ASSETS = "/android_asset/";

	protected AndroidStreamHelper() {}

	public AndroidStreamHelper(InputStream in, long dataSize) {
		super(in, dataSize);
	}

	public static StreamHelper getStream(Context context, URI uri) throws IOException {
		if ( context == null ) {
		    throw new NullPointerException("context is null");
		}
		if ( uri == null ) {
		    throw new NullPointerException("uri is null");
		}
		StreamHelper result;
		String path = uri.getPath();
		if ( uri.getScheme().equalsIgnoreCase(ContentResolver.SCHEME_FILE) && path.startsWith(ANDROID_ASSETS)) {
			result = new AndroidStreamHelper();
			try {
				AssetFileDescriptor fd = context.getAssets().openFd(path.replace(ANDROID_ASSETS, ""));
                result.setStreamData(fd.createInputStream(), fd.getLength());
			} catch (FileNotFoundException e) {
                result.setStreamData(context.getAssets().open(path.replace(ANDROID_ASSETS, "")), Long.MIN_VALUE);
			}
		} else if ( uri.getScheme().equalsIgnoreCase(ContentResolver.SCHEME_CONTENT)) {
			result = new AndroidStreamHelper();
			ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(Uri.parse(uri.toString()), "r");
            result.setStreamData(new ParcelFileDescriptor.AutoCloseInputStream(fd), fd.getStatSize());
		} else {
			result = StreamHelper.getStream(uri);
		}
		return result;
	}
}
