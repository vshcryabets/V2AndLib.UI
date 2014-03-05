/*
 * Copyright (C) 2012-2014 V.Shcryabets (vshcryabets@gmail.com)
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

import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.ITaskSimpleListener;
import com.v2soft.AndLib.filecache.FileCache;

import java.net.URI;
import java.security.NoSuchAlgorithmException;

/**
 * Task that will download & encode bitmap file in background thread
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class DownloadAndDecodeImage extends LoadBitmapTask {
    private static final long serialVersionUID = 1L;
    private URI mURL;
	protected FileCache mCache;

    public DownloadAndDecodeImage(URI imageURL, FileCache cache) {
        super("");
        mURL = imageURL;
		mCache = cache;
    }

    @Override
    public LoadBitmapTask execute(ITaskSimpleListener hub) throws AbstractDataRequestException {
        final DownloadTask cache = new DownloadTask(mURL, mCache);
        cache.execute(hub);
        checkCanceled();
		try {
			mFilePath = mCache.getCachePathURI(mURL);
		} catch (NoSuchAlgorithmException e) {
			throw new DummyTaskException(e.toString());
		}
		return super.execute(hub);
    }
    public URI getURL() {
        return mURL;
    }
}
