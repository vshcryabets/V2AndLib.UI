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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Message;

import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.ITask;
import com.v2soft.AndLib.dataproviders.ITaskSimpleListener;

/**
 * Task that will download file from specified URL to local cache
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class CacheHTTPFile extends DummyTask<Boolean> {
	private static final long serialVersionUID = 1L;
	public static final int MSG_CONTENT_LENGTH = 1;
	public static final int MSG_RECEIVED_LENGTH = 2;
	private static final long UPDATE_MEASURE = 1024 * 10;
	private URL mURL;
	private File mLocalCacheDir;
	private String mCustomHashString;
	private HttpClient mClient;
	private boolean mSucccess = false;

	/**
	 *
	 * @param source source resource path
	 * @param localCacheDir
	 */
	public CacheHTTPFile(URL source, File localCacheDir) {
		mURL = source;
		mLocalCacheDir = localCacheDir;
		mClient = new DefaultHttpClient();
	}

	public CacheHTTPFile(URL filePath, File localCacheDir, String customHashString) {
		mURL = filePath;
		mLocalCacheDir = localCacheDir;
		mCustomHashString = customHashString;
		mClient = new DefaultHttpClient();
	}
	public void setHttpClient(HttpClient client) {
		mClient = client;
	}

	public String getLocalPath() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		if ( mCustomHashString != null ) {
			return mCustomHashString;
		} else {
			return getURLHash(mURL);
		}
	}

	/**
	 * Get hashed of specified URL.
	 * @param path
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getURLHash(URL path) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// get file path hash
		final MessageDigest md = MessageDigest.getInstance("MD5");
		final byte [] pathBytes = path.toExternalForm().getBytes("utf-8");
		md.update(pathBytes, 0, pathBytes.length);
		final String filename = new BigInteger( 1, md.digest() ).toString( 16 );
		return filename;
	}

	public static boolean isFileInCache(URL path, File localCacheDir)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		final String filename = getURLHash(path);
		final File file = new File(localCacheDir, filename);
		return file.exists();
	}

	@Override
	public Boolean getResult() {
		return mSucccess;
	}

	@Override
	public CacheHTTPFile execute(ITaskSimpleListener handler)
			throws AbstractDataRequestException {
		mSucccess = false;
		try {
			if ( isFileInCache(mURL, mLocalCacheDir)) {
				mSucccess= true;
				return this;
			}
			final String filename = getURLHash(mURL);
			final File file = new File(mLocalCacheDir, filename);
			checkCanceled();
			// download this file
			// Open a connection to that URL.
			final HttpGet request = new HttpGet(mURL.toString());
			final HttpResponse response = mClient.execute(request);
			checkCanceled();
			int statusCode = response.getStatusLine().getStatusCode();
			if ( statusCode != 200 ) {
				throw new IOException("Status code "+statusCode);
			}
			long length = response.getEntity().getContentLength();
			final Message msg = new Message();
			if ( handler != null ) {
				msg.what = MSG_CONTENT_LENGTH;
				msg.obj = length;
				handler.onMessageFromTask(this, msg);
			}
			final InputStream is = response.getEntity().getContent();
			final FileOutputStream fos = new FileOutputStream(file);
			final byte [] buffer = new byte[4096];
			int read = 0;
			long total = 0, prevTotal = 0;
			while ( (read = is.read(buffer)) > 0 ) {
				checkCanceled();
				fos.write(buffer, 0, read);
				checkCanceled();
				total += read;
				if ( handler != null && total - prevTotal > UPDATE_MEASURE ) {
					prevTotal = total;
					msg.what = MSG_RECEIVED_LENGTH;
					msg.obj = total;
					handler.onMessageFromTask(this, msg);
				}
			}
			is.close();
			fos.close();
			mSucccess = true;
			return this;
		} catch (UnsupportedEncodingException e) {
			throw new DummyTaskException(e.toString());
		} catch (NoSuchAlgorithmException e) {
			throw new DummyTaskException(e.toString());
		} catch (FileNotFoundException e) {
			throw new DummyTaskException(e.toString());
		} catch (ClientProtocolException e) {
			throw new DummyTaskException(e.toString());
		} catch (IOException e) {
			throw new DummyTaskException(e.toString());
		}
	}

	public String getFullLocalPath() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		return new File(mLocalCacheDir, getLocalPath()).getAbsolutePath();
	}
}
