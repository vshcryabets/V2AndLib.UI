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
package com.v2soft.AndLib.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.v2soft.AndLib.dataproviders.AndroidDataStreamWrapper;
import com.v2soft.AndLib.dataproviders.DataStreamWrapper;

import java.io.IOException;
import java.net.URI;

/**
 * Bitmap operations helper.
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 */
public class BitmapOperations {
	protected Context mContext;

	public BitmapOperations(Context context) {
		mContext = context;
	}

	/**
	 * Load bitmap options.
	 * @param uri
	 * @return bitmap options.
	 * @throws IOException
	 */
	public BitmapFactory.Options getBitmapOptions(URI uri) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		loadBitmap(uri, options);
		return options;
	}

	/**
	 * Load bitmap from specified source.
	 * @param uri
	 * @param options
	 * @return
	 */
	public Bitmap loadBitmap(URI uri, BitmapFactory.Options options) throws IOException {
		DataStreamWrapper stream = AndroidDataStreamWrapper.getStream(mContext, uri);
		Bitmap result = BitmapFactory.decodeStream(stream.getInputStream(), null, options);
		stream.close();
		return result;
	}

	/**
	 * Load bitmap from specified source.
	 * @param uri
	 * @return
	 */
	public Bitmap loadBitmap(URI uri) throws IOException {
		return loadBitmap(uri, null);
	}
}
