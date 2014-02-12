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
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.ITask;
import com.v2soft.AndLib.dataproviders.ITaskSimpleListener;

/**
 * Task that will download & encode bitmap file in background thread
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class DownloadAndDecodeImage extends LoadBitmapTask {
    private static final long serialVersionUID = 1L;
    private URL mURL;
    private File mCacheDir;
    protected String mCustomHashString;

    public DownloadAndDecodeImage(URL imageURL, File cacheDir) {
        super("");
        mURL = imageURL;
        mCacheDir = cacheDir;
    }
    public DownloadAndDecodeImage(URL imageURL, File cacheDir, String customHash) {
        super("");
        mURL = imageURL;
        mCacheDir = cacheDir;
        mCustomHashString = customHash;
    }

    @Override
    public ITask<Serializable, Void, Void> execute(ITaskSimpleListener hub) throws AbstractDataRequestException {
        final CacheHTTPFile cache = new CacheHTTPFile(mURL, mCacheDir, mCustomHashString);
        cache.execute(hub);
        checkCanceled();
		try {
			mFilePath = new File(mCacheDir, cache.getLocalPath()).getAbsolutePath();
		} catch (NoSuchAlgorithmException e) {
			throw new DummyTaskException(e.toString());
		} catch (UnsupportedEncodingException e) {
			throw new DummyTaskException(e.toString());
		}
		return super.execute(hub);
    }
    public URL getURL() {
        return mURL;
    }
}
