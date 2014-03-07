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

import java.io.File;
import java.io.IOException;
import java.net.URI;

import android.content.Context;
import android.os.Message;

import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.AndroidStreamHelper;
import com.v2soft.AndLib.streams.StreamHelper;
import com.v2soft.AndLib.dataproviders.ITaskSimpleListener;
import com.v2soft.AndLib.filecache.FileCache;

/**
 * Task that will download file from specified URL to local cache
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class CacheHTTPFile extends DownloadTask {
	private static final long serialVersionUID = 1L;
	public static final int MSG_CONTENT_LENGTH = 1;
	public static final int MSG_RECEIVED_LENGTH = 2;
    protected Context mContext;
	/**
	 *
	 * @param source source resource path
	 */
	public CacheHTTPFile(Context context, URI source, File localFile) {
		super(source, localFile);
        mContext = context;
	}

	public CacheHTTPFile(Context context, URI filePath, FileCache cache) {
		super(filePath, cache);
        mContext = context;
	}

	@Override
	public DownloadTask execute(final ITaskSimpleListener handler)
			throws AbstractDataRequestException {
		if ( handler != null ) {
			mListener = new StreamHelper.StreamPositionListener() {
				@Override
				public void onPositionChanged(long position, long maxPosition) {
					final Message msg = new Message();
					if ( position == 0 ) {
						msg.what = MSG_CONTENT_LENGTH;
						msg.obj = maxPosition;
					} else {
						msg.what = MSG_RECEIVED_LENGTH;
						msg.obj = position;
					}
					handler.onMessageFromTask(CacheHTTPFile.this, msg);
				}
			};
		}
		return super.execute(handler);
	}

    @Override
    protected StreamHelper getStream(URI uri) throws IOException {
        return AndroidStreamHelper.getStream(mContext, uri);
    }

}
