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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	public FileOutputStream getFileOutputStream(URI name) throws NoSuchAlgorithmException, FileNotFoundException {
		File file = getFileByURI(name);
		return new FileOutputStream(file);
	}
	public FileInputStream getFileInputStream(URI name) throws NoSuchAlgorithmException, FileNotFoundException {
		File file = getFileByURI(name);
		return new FileInputStream(file);
	}

	/**
	 * Get absolute cached file path.
	 * @param uri
	 * @return absolute file path in cache.
	 */
	public String getCachePathURI(URI uri) throws NoSuchAlgorithmException {
		return new File(mCacheFolder, mNameFactory.getLocalName(uri.toString())).getAbsolutePath();
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
            preBuildCheck();
			return new FileCache(mNameFactory, mCacheFolder);
		}

        protected void preBuildCheck() {
            if ( mNameFactory == null ) {
                mNameFactory = new JavaHashFactory();
            }
            if ( mCacheFolder == null ) {
                throw new NullPointerException("Cache folder wasn't specified");
            }
        }
    }
}
