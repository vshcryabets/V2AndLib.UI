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

import com.v2soft.AndLib.dataproviders.AndroidStreamHelper;
import com.v2soft.AndLib.streams.StreamHelper;

import java.io.IOException;
import java.net.URI;

/**
 * Bitmap operations helper.
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 */
public class BitmapOperations {
    public enum ScaleType {CENTER_CROP,
        FIT_CENTER,
        SCALE_HORIZONTAL,
        SCALE_VERTICAL,
        NONE}
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
	 * @return bitmap object.
	 */
	public Bitmap loadBitmap(URI uri, BitmapFactory.Options options) throws IOException {
		StreamHelper stream = AndroidStreamHelper.getStream(mContext, uri);
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
	 * @param margins destination rectangle.
	 * @param scaleType scale type.
	 * @param filter    true if the source should be filtered.
	 * @return bitmap from specified source.
	 */
	public Bitmap loadBitmap(URI uri, Rect margins, ScaleType scaleType, boolean filter) throws IOException {
		BitmapFactory.Options options = getBitmapOptions(uri);
		Rect destRect = scaleRect(options, margins, scaleType);
		options = getBitmapScaleOptions(uri, destRect, scaleType);
		Bitmap bitmap = loadBitmap(uri, options);
        if ( bitmap != null ) {
            Bitmap result = Bitmap.createScaledBitmap(bitmap, destRect.width(), destRect.height(), filter);
            if ( result != bitmap ) {
                bitmap.recycle();
            }
            return result;
        } else {
            throw new IOException("Can't read bitmap");
        }
	}

	/**
	 * Calculate scale factor.
	 * @param scaleType scale type
	 * @param destRect margins.
	 * @param sourceRect source bitmap rectangle.
	 * @return scale factor.
	 */
    public float getScaleFactor(ScaleType scaleType, Rect destRect, Rect sourceRect) {
        float horizontalScaleFactor = (float)destRect.width()/(float)sourceRect.width();
        float verticalScaleFactor = (float)destRect.height()/(float)sourceRect.height();
        float maxFactor = Math.max(verticalScaleFactor, horizontalScaleFactor);
        float minFactor = Math.min(verticalScaleFactor, horizontalScaleFactor);
        if ( scaleType == ScaleType.FIT_CENTER ) {
            return minFactor;
        } else if ( scaleType == ScaleType.CENTER_CROP ) {
            return maxFactor;
        } else if ( scaleType == ScaleType.SCALE_HORIZONTAL ) {
            return horizontalScaleFactor;
        } else if ( scaleType == ScaleType.SCALE_VERTICAL ) {
            return verticalScaleFactor;
        }
        return 1.0f;
    }


    /**
	 * Calculate BitmapFactory.Options::inScaleSize value to fit bitmap in specified margins.
	 * @param uri bitmap source uri.
	 * @param destRect destination margins.
	 * @param scaleType scale type.
	 * @return bitmap loading options.
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

	/**
	 * Scale bitmap to fit inside specified margins.
	 * @param bitmap source bitmap.
	 * @param margins margins.
	 * @return scaled bitmap object.
	 */
	public Bitmap scaleBitmap(Bitmap bitmap, Rect margins, ScaleType scaleType, boolean filter) {
		Rect rect = scaleRect(bitmap, margins, scaleType);
		Bitmap result = Bitmap.createScaledBitmap(bitmap, rect.width(), rect.height(), filter);
		return result;
	}

	protected Rect scaleRect(Bitmap bitmap, Rect margins, ScaleType type) {
		Rect source = new Rect(0,0, bitmap.getWidth(), bitmap.getHeight());
		return scaleRect(source, margins, type);
	}
	protected Rect scaleRect(BitmapFactory.Options options, Rect margins, ScaleType scaleType) {
		Rect srcRect = new Rect(0,0, options.outWidth, options.outHeight);
		return scaleRect(srcRect, margins, scaleType);
	}
	protected Rect scaleRect(Rect source, Rect margins, ScaleType type) {
		float scaleDownFactor = getScaleFactor(type, margins, source);
		return multipleRect(source, scaleDownFactor);
	}

	protected Rect multipleRect(Rect src, float factor) {
		return new Rect((int)(src.left*factor),
				(int)(src.top*factor),
				(int)(src.right*factor),
				(int)(src.bottom*factor));
	}

}
