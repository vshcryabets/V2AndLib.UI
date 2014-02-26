/*
 * Copyright (C) 2012-2013 V.Shcryabets (vshcryabets@gmail.com)
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;

import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.ITask;
import com.v2soft.AndLib.dataproviders.ITaskSimpleListener;

/**
 * Task that will encode bitmap file in background thread
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class WriteBitmapWithSpecifiedSizeTask extends DummyTask<Serializable> {
    private static final long serialVersionUID = 1L;

    public enum RegressionMode {
        DIVIDE2, DECREMENT
    }
    private String mInputFile, mOutputFile;
    private int mMaxSize;
    private int mMaxWidth;
    private int mMaxHeight;
    private boolean useMinimalScaleFactor;
    private RegressionMode mMode = RegressionMode.DIVIDE2;
    private int mRegresionParam;

    public WriteBitmapWithSpecifiedSizeTask(String inputPath, String outputPath, int maxSize) {
        mInputFile = inputPath;
        mOutputFile = outputPath;
        mMaxSize = maxSize;
    }

    public void setSizeLimits(int maxWidth, int maxHeight, boolean useMinimalScaleFactor) {
        mMaxHeight = maxHeight;
        mMaxWidth = maxWidth;
        this.useMinimalScaleFactor = useMinimalScaleFactor;
    }
    
    public void setRegression(RegressionMode mode, int param) {
        mMode = mode;
        mRegresionParam = param;
    }

	@Override
	public Serializable getResult() {
		return null;
	}

	@Override
    public WriteBitmapWithSpecifiedSizeTask execute(ITaskSimpleListener hub) throws AbstractDataRequestException {
        final LoadBitmapTask loader = new LoadBitmapTask(mInputFile, mMaxWidth, mMaxHeight);
        loader.setUseMinimalScaleFactor(useMinimalScaleFactor);
        loader.execute(hub);
        checkCanceled();
        final Bitmap thumbnail = loader.getBitmap();
        final File file = new File(mOutputFile);
        int quality = 100;
        checkCanceled();
        while ( !file.exists() || file.length() > mMaxSize ) {
			final FileOutputStream of;
			try {
				of = new FileOutputStream(file);
				checkCanceled();
				thumbnail.compress(Bitmap.CompressFormat.JPEG, quality, of);
				if ( mMode == RegressionMode.DECREMENT ) {
					quality -= mRegresionParam;
				} else {
					quality /= 2;
				}
				of.close();
			} catch (FileNotFoundException e) {
				throw new DummyTaskException(e.toString());
			} catch (IOException e) {
				throw new DummyTaskException(e.toString());
			}
		}
        thumbnail.recycle();
		return this;
    }
}
