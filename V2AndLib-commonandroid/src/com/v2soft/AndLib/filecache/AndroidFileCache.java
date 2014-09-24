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
package com.v2soft.AndLib.filecache;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.security.NoSuchAlgorithmException;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class AndroidFileCache extends FileCache {
	public AndroidFileCache(NameFactory nameFactory, File cacheFolder) {
		super(nameFactory, cacheFolder);
	}

	public boolean isInCache(Uri name) throws NoSuchAlgorithmException {
		File file = getFileByUri(name);
		return file.exists();
	}

	public File getFileByUri(Uri name) throws NoSuchAlgorithmException {
		return new File(mCacheFolder, mNameFactory.getLocalName(name.toString()));
	}

	public static class Builder extends FileCache.Builder {
		private Context mContext;

		public Builder(Context context) {
			mContext = context;
		}

		public Builder useInternalCacheFolder(String type) {
			if ( type != null ) {
				mCacheFolder = new File(mContext.getCacheDir(), type);
			} else {
				mCacheFolder = mContext.getCacheDir();
			}
            return this;
		}
		public Builder useExternalCacheFolder(String type) {
			if ( type != null ) {
				mCacheFolder = new File(mContext.getExternalCacheDir(), type);
			} else {
				mCacheFolder = mContext.getExternalCacheDir();
			}
			if ( mCacheFolder == null ) {
				useInternalCacheFolder(type);
			}
            return this;
		}

		public AndroidFileCache build() {
			preBuildCheck();
			return new AndroidFileCache(mNameFactory, mCacheFolder);
		}

        @Override
        protected void preBuildCheck() {
            if ( mCacheFolder == null ) {
                useExternalCacheFolder(null);
            }
            super.preBuildCheck();
        }

        public void useExternalFilesFolder(String type) {
            mCacheFolder = mContext.getExternalFilesDir(type);
        }

        public void useInternalFilesFolder(String type) {
            if ( type != null ) {
                mCacheFolder = new File(mContext.getFilesDir(), type);
            } else {
                mCacheFolder = mContext.getFilesDir();
            }
        }
    }
}
