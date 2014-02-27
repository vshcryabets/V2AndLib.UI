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
package com.v2soft.AndLib.dataproviders;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 */
public class DataStreamWrapper implements Closeable {
	private static final String FILE_SCHEME_URI = "file";
	private static final String HTTP_SCHEME_URI = "http";
	private static final String HTTPS_SCHEME_URI = "https";
	private static final int BUFFER_SIZE = 8192;
	private static final long UPDATE_MEASURE = 10 * 1024; // update every 10kB

	public interface StreamPositionListener {
		public void onPositionChanged(long position, long maxPosition);
	}

	protected InputStream mStream;
	protected long mAvaiableDataSize;
	protected long mUpdateMeasure = UPDATE_MEASURE;

	protected DataStreamWrapper() {}

	public DataStreamWrapper(InputStream in, long dataSize) {
		mStream = in;
		mAvaiableDataSize = dataSize;
	}

	public static DataStreamWrapper getStream(URI uri) throws IOException {
		DataStreamWrapper result = new DataStreamWrapper();
		String path = uri.getPath();

		if ( uri.getScheme().equalsIgnoreCase(FILE_SCHEME_URI)) {
			File file = new File(path);
			result.mStream = new FileInputStream(path);
			result.mAvaiableDataSize = file.length();
		} else if ( uri.getScheme().equalsIgnoreCase(HTTP_SCHEME_URI) ||
				uri.getScheme().equalsIgnoreCase(HTTPS_SCHEME_URI)) {
			URL url = new URL(uri.toString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.getDoInput();
			connection.connect();
			result.mStream = connection.getInputStream();
			result.mAvaiableDataSize = connection.getContentLength();
		}
		return result;
	}

	@Override
	public void close() throws IOException {
		if ( mStream != null ) {
			mStream.close();
			mStream = null;
		}
	}

	public InputStream getInputStream() {
		return mStream;
	}

	public long getAvaiableDataSize() {
		return mAvaiableDataSize;
	}

	/**
	 * Copy stream to specified output stream.
	 * @param out
	 * @param listener
	 * @return
	 * @throws java.io.IOException
	 */
	public DataStreamWrapper copyToOutputStream(OutputStream out, StreamPositionListener listener)
			throws IOException {
		byte buffer[] = new byte[BUFFER_SIZE];
		int read;
		long total = 0, prevtotal = 0;
		while ( (read = mStream.read(buffer)) > 0 ) {
			out.write(buffer, 0, read);
			total += read;
			if ( listener != null && total - mUpdateMeasure > prevtotal ) {
				listener.onPositionChanged(total, mAvaiableDataSize);
			}
		}
		return this;
	}

	public DataStreamWrapper copyToOutputStream(OutputStream out)
			throws IOException {
		return copyToOutputStream(out, null);
	}

	public void setUpdateMeasure(long mUpdateMeasure) {
		this.mUpdateMeasure = mUpdateMeasure;
	}

}
