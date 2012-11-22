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
import java.io.FileOutputStream;

import android.graphics.Bitmap;

import com.v2soft.AndLib.dataproviders.ITaskHub;

/**
 * Task that will encode bitmap file in background thread
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class WriteBitmapWithSpecifiedSizeTask extends DummyTask {
    private String mInputFile, mOutputFile;
    private int mMaxSize;
    private int mMaxWidth;
    private int mMaxHeight;
    private boolean useMinimalScaleFactor;

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

    @Override
    public void execute(ITaskHub hub) throws Exception {
        final LoadBitmapTask loader = new LoadBitmapTask(mInputFile, mMaxWidth, mMaxHeight);
        loader.setUseMinimalScaleFactor(useMinimalScaleFactor);
        loader.execute(hub);
        final Bitmap thumbnail = loader.getBitmap();
        final File file = new File(mOutputFile);
        int quality = 100;
        while (file.length() > mMaxSize ) {
            final FileOutputStream of = new FileOutputStream(file);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, quality, of);
            quality /= 2;
            of.close();
        }
        thumbnail.recycle();
    }
}
