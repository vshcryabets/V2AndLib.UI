package com.v2soft.AndLib.filecache;

import java.io.File;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class FileCache {
	public interface NameFactory {
		/**
		 * Return apropriate local name for specified resource.
		 * @param resource
		 * @return
		 */
		String getLocalName(String resource) throws NoSuchAlgorithmException;
	}

	protected NameFactory mNameFactory;
	protected File mCacheFolder;

	public FileCache(NameFactory nameFactory, File cacheFolder) {
		mNameFactory = nameFactory;
		mCacheFolder = cacheFolder;
		if ( !mCacheFolder.exists() ) {
			mCacheFolder.mkdirs();
		}
	}

	/**
	 * Remove all files from cache.
	 */
	public void clear() {
		File[] allFiles = mCacheFolder.listFiles();
		for (File file : allFiles ) {
			file.delete();
		}
	}


	public boolean isInCache(URI name) throws NoSuchAlgorithmException {
		File file = getFileByURI(name);
		return file.exists();
	}
	public boolean isInCache(String name) throws NoSuchAlgorithmException {
		File file = getFileByName(name);
		return file.exists();
	}

	private File getFileByName(String name) throws NoSuchAlgorithmException {
		return new File(mCacheFolder, mNameFactory.getLocalName(name));
	}

	public File getFileByURI(URI file) throws NoSuchAlgorithmException {
		return new File(mCacheFolder, mNameFactory.getLocalName(file.toString()));
	}

	public boolean remove(URI fileName) throws NoSuchAlgorithmException {
		File file = getFileByURI(fileName);
		return file.delete();
	}
	public static class Builder {
		protected NameFactory mNameFactory;
		protected File mCacheFolder;

		public Builder() {

		}

		public void setNamesFactory(NameFactory nameFactory) {
			mNameFactory = nameFactory;
		}

		public void setOutdate(int calendarType, int value) {

		}

		public void setCacheFolder(File folder) {
			mCacheFolder = folder;
		}

		public FileCache build() {
			return new FileCache(mNameFactory, mCacheFolder);
		}
	}
}
