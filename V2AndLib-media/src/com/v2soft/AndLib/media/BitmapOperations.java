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
import android.graphics.Rect;

import com.v2soft.AndLib.dataproviders.AndroidDataStreamWrapper;
import com.v2soft.AndLib.dataproviders.DataStreamWrapper;

import java.io.IOException;
import java.net.URI;

/**
 * Bitmap operations helper.
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 */
public class BitmapOperations {
	public enum ScaleType {CENTER_CROP, FIT_CENTER, NONE};
	protected Context mContext;

	public BitmapOperations(Context context) {
		mContext = context;
	}

	/**
	 * Load bitmap options.
	 * @param uri bitmap source uri.
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
	 * @param uri bitmap source uri.
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
	 * @param uri bitmap source uri.
	 * @return bitmap from specified source.
	 */
	public Bitmap loadBitmap(URI uri) throws IOException {
		return loadBitmap(uri, null);
	}
	/**
	 * Load bitmap from specified source.
	 * @param uri bitmap source uri.
	 * @param destRect destination rectangle.
	 * @param scaleType scale type.
	 * @param filter    true if the source should be filtered.
	 * @return bitmap from specified source.
	 */
	public Bitmap loadBitmap(URI uri, Rect destRect, ScaleType scaleType, boolean filter) throws IOException {
		BitmapFactory.Options options = getBitmapOptions(uri);
		Rect srcRect = new Rect(0,0, options.outWidth, options.outHeight);
		float scaleDownFactor = getScaleFactor(scaleType, destRect, srcRect);
		int outputWidth = (int) (options.outWidth*scaleDownFactor);
		int outputHeight = (int) (options.outHeight*scaleDownFactor);

		options = getBitmapScaleOptions(uri, destRect, scaleType);
		Bitmap bitmap = loadBitmap(uri, options);
		Bitmap result = Bitmap.createScaledBitmap(bitmap, outputWidth, outputHeight, filter);
		bitmap.recycle();
		return result;
	}


	/**
	 * Calculate scale factor.
	 * @param scaleType scale type
	 * @param destRect
	 * @param sourceRect
	 * @return
	 */
	public float getScaleFactor(ScaleType scaleType, Rect destRect, Rect sourceRect) {
		float verticalScaleFactor = (float)destRect.width()/(float)sourceRect.width();
		float horizontalScaleFactor = (float)destRect.height()/(float)sourceRect.height();
		float maxFactor = Math.max(verticalScaleFactor, horizontalScaleFactor);
		float minFactor = Math.min(verticalScaleFactor, horizontalScaleFactor);
		if ( scaleType == ScaleType.FIT_CENTER ) {
			return minFactor;
		} else if ( scaleType == ScaleType.CENTER_CROP ) {
			return maxFactor;
		}
		return 1.0f;
	}

	/**
	 * Calculate BitmapFactory.Options::inScaleSize value to fit bitmap in destRect
	 * @param uri bitmap source uri.
	 * @param destRect destination rectangle.
	 * @param scaleType scale type.
	 * @return
	 */
	public BitmapFactory.Options getBitmapScaleOptions(URI uri, Rect destRect, ScaleType scaleType) throws IOException {
		BitmapFactory.Options options = getBitmapOptions(uri);
		Rect srcRect = new Rect(0,0, options.outWidth, options.outHeight);
		float scaleDownFactor = 1/getScaleFactor(scaleType, destRect, srcRect);
		options = new BitmapFactory.Options();
		double scaleDownPower2 = Math.log(scaleDownFactor)/Math.log(2);
		scaleDownPower2 = Math.floor(scaleDownPower2);
		int scaleDownInt = (int) Math.pow(2, scaleDownPower2);
		options.inSampleSize = scaleDownInt;
		return options;
	}

}
