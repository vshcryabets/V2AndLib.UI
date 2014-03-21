/*
 * Copyright (C) 2012 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.dataproviders.tasks;

import java.io.File;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.ITask;
import com.v2soft.AndLib.dataproviders.ITaskSimpleListener;

/**
 * Task that will decode bitmap file in background thread
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
// TODO BitmapHelper class should be used here.
public class LoadBitmapTask extends DummyTask<Serializable> {
    private static final long serialVersionUID = 1L;
    protected String mFilePath;
    transient protected Bitmap mBitmap;
    protected int mMaxWidth, mMaxHeight;
    protected boolean useMinimalFactor = true;

    public LoadBitmapTask(String filePath) {
        this(filePath, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }
    public LoadBitmapTask(String filePath, int maxWidth, int maxHeight) {
        mFilePath = filePath;
        setImageSizeLimits(maxWidth, maxHeight);
    }
    public LoadBitmapTask(File file, int maxWidth, int maxHeight) {
        this(file.getAbsolutePath(), maxWidth, maxHeight);
    }
    public void setUseMinimalScaleFactor(boolean value) {
        useMinimalFactor = value;
    }
    public void setImageSizeLimits(int maxWidth, int maxHeight) {
        mMaxHeight = maxHeight;
        mMaxWidth = maxWidth;
    }

	@Override
	public Serializable getResult() {
		return null;
	}

	@Override
    public LoadBitmapTask execute(ITaskSimpleListener hub) throws AbstractDataRequestException {
        // Get the dimensions of the bitmap
        final BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        checkCanceled();
        BitmapFactory.decodeFile(mFilePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = 1;
        checkCanceled();
        if ( mMaxWidth > 0 ) {
            scaleFactor = photoW/mMaxWidth;
        }
        if ( mMaxHeight > 0 ) {
            if ( useMinimalFactor ) {
                scaleFactor = Math.min(scaleFactor, photoH/mMaxHeight);
            } else {
                scaleFactor = Math.max(scaleFactor, photoH/mMaxHeight);
            }
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        checkCanceled();
        final Bitmap bitmap = BitmapFactory.decodeFile(mFilePath, bmOptions);
        setBitmap(bitmap);
		return this;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }
    
    /**
     * Return path to file with surce image
     */
    public String getFilePath() {
        return mFilePath;
    }
}
