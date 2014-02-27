package com.v2soft.AndLib.filecache;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.net.URI;
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

		public void useInternalCacheFolder(String type) {
			if ( type != null ) {
				mCacheFolder = new File(mContext.getCacheDir(), type);
			} else {
				mCacheFolder = mContext.getCacheDir();
			}
		}
		public void useExternalCacheFolder(String type) {
			if ( type != null ) {
				mCacheFolder = new File(mContext.getExternalCacheDir(), type);
			} else {
				mCacheFolder = mContext.getExternalCacheDir();
			}
			if ( mCacheFolder == null ) {
				useInternalCacheFolder(type);
			}
		}

		public AndroidFileCache build() {
			if ( mNameFactory == null ) {
				throw new NullPointerException("Name factory wasn't specified");
			}
			return new AndroidFileCache(mNameFactory, mCacheFolder);
		}
	}
}
