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
import java.net.URL;

import com.v2soft.AndLib.dataproviders.ITaskHub;

/**
 * Task that will download & encode bitmap file in background thread
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class DownloadAndDecodeImage extends LoadBitmapTask {
    private URL mURL;
    private File mCacheDir;

    public DownloadAndDecodeImage(URL imageURL, File cacheDir) {
        super("");
        mURL = imageURL;
        mCacheDir = cacheDir;
    }

    @Override
    public void execute(ITaskHub hub) throws Exception {
        final CacheHTTPFile cache = new CacheHTTPFile(mURL, mCacheDir);
        cache.execute(hub);
        mFilePath = new File(mCacheDir, cache.getLocalPath()).getAbsolutePath();
        super.execute(hub);
    }
    
}
