package com.v2soft.AndLib.dataproviders;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 */
public class DataStreamWrapper implements Closeable {
	private static final String FILE_SCHEME_URI = "file";
	private static final String CONTENT_SCHEME_URI = "content";
	private static final String HTTP_SCHEME_URI = "http";
	private static final String HTTPS_SCHEME_URI = "https";

	private static final String ANDROID_ASSETS = "/android_asset/";

	private InputStream mStream;
	private long mAvaiableDataSize;

	private DataStreamWrapper() {}

	public static DataStreamWrapper getStream(Context context, URI uri) throws IOException {
		DataStreamWrapper result = new DataStreamWrapper();
		String path = uri.getPath();

		if ( uri.getScheme().equalsIgnoreCase(FILE_SCHEME_URI)) {
			if ( path.startsWith(ANDROID_ASSETS)) {
				try {
					AssetFileDescriptor fd = context.getAssets().openFd(path.replace(ANDROID_ASSETS, ""));
					result.mAvaiableDataSize = fd.getLength();
					result.mStream = fd.createInputStream();
				} catch (FileNotFoundException e) {
					result.mStream = context.getAssets().open(path.replace(ANDROID_ASSETS, ""));
					result.mAvaiableDataSize = Long.MIN_VALUE;
				}
			} else {
				File file = new File(path);
				result.mStream = new FileInputStream(path);
				result.mAvaiableDataSize = file.length();
			}
		} else if ( uri.getScheme().equalsIgnoreCase(CONTENT_SCHEME_URI)) {
			context.getContentResolver().openInputStream(Uri.parse(uri.toString()));
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
}
