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
import com.v2soft.AndLib.streams.SpeedControlOutputStream;
import com.v2soft.AndLib.streams.StreamHelper;
import com.v2soft.AndLib.dataproviders.ITaskSimpleListener;
import com.v2soft.AndLib.filecache.FileCache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

/**
 * Task that will download file from specified URL to local cache
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class DownloadTask extends DummyTask<Boolean> {
	private static final long serialVersionUID = 1L;
	protected URI mURI;
	protected boolean mSuccess = false;
	protected File mCachedFile;
	protected FileCache mCache;
	protected StreamHelper.StreamPositionListener mListener;
    protected int mSpeedLimit = Integer.MIN_VALUE; // negative means unlimited
    public static final int BYTES_IN_KB = 1024;

	/**
	 *
	 * @param source source resource path
	 * @param localFile cached file object.
	 */
	public DownloadTask(URI source, File localFile) {
		mURI = source;
		mCachedFile = localFile;
	}
	/**
	 *
	 * @param source source resource path
	 * @param cache cache object.
	 */
	public DownloadTask(URI source, FileCache cache) {
		mURI = source;
		mCache = cache;
	}

    /**
     * Set download speed limit. Negative value means unlimited speed.
     * @param limitInBytesPerSecond speed limit in bytes per second.
     */
    public void setSpeedLimit(int limitInBytesPerSecond) {
        mSpeedLimit = limitInBytesPerSecond;
    }
	@Override
	public Boolean getResult() {
		return mSuccess;
	}

	@Override
	public DownloadTask execute(ITaskSimpleListener handler)
			throws AbstractDataRequestException {
		mSuccess = false;
		try {
            mCachedFile = getLocalFilePath();
			if ( mCachedFile.exists() ) {
				mSuccess = true;
				return this;
			}
			StreamHelper wrapper = getStream(mURI);
			OutputStream output = new FileOutputStream(mCachedFile);
            if ( mSpeedLimit > 0 ) {
                output = new SpeedControlOutputStream(output, mSpeedLimit);
            }
            try {
                wrapper.copyToOutputStream(output, mListener, this);
                mSuccess = true;
            } catch (InterruptedException e) {
                mSuccess = false;
                // task was canceled, we should remove file
                mCachedFile.delete();
            }
            wrapper.close();
			output.close();
			return this;
		} catch (UnsupportedEncodingException e) {
			throw new DummyTaskException(e.toString());
		} catch (NoSuchAlgorithmException e) {
			throw new DummyTaskException(e.toString());
		} catch (FileNotFoundException e) {
			throw new DummyTaskException(e.toString());
		} catch (IOException e) {
			throw new DummyTaskException(e.toString());
		}
	}

    protected StreamHelper getStream(URI uri) throws IOException {
        return StreamHelper.getStream(uri);
    }

    public File getLocalFilePath() throws NoSuchAlgorithmException {
        if ( mCachedFile == null ) {
            mCachedFile = mCache.getFileByURI(mURI);
        }
		return mCachedFile;
	}
}
