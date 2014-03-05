package com.v2soft.AndLib.dataproviders;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 */
public class AndroidDataStreamWrapper extends DataStreamWrapper {
	private static final String FILE_SCHEME_URI = "file";
	private static final String ANDROID_ASSETS = "/android_asset/";

	protected AndroidDataStreamWrapper() {}

	public AndroidDataStreamWrapper(InputStream in, long dataSize) {
		super(in, dataSize);
	}

	public static DataStreamWrapper getStream(Context context, URI uri) throws IOException {
		DataStreamWrapper result;
		String path = uri.getPath();
		if ( uri.getScheme().equalsIgnoreCase(ContentResolver.SCHEME_FILE) && path.startsWith(ANDROID_ASSETS)) {
			result = new AndroidDataStreamWrapper();
			try {
				AssetFileDescriptor fd = context.getAssets().openFd(path.replace(ANDROID_ASSETS, ""));
				result.mAvaiableDataSize = fd.getLength();
				result.mStream = fd.createInputStream();
			} catch (FileNotFoundException e) {
				result.mStream = context.getAssets().open(path.replace(ANDROID_ASSETS, ""));
				result.mAvaiableDataSize = Long.MIN_VALUE;
			}
		} else if ( uri.getScheme().equalsIgnoreCase(ContentResolver.SCHEME_CONTENT)) {
			result = new AndroidDataStreamWrapper();
			ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(Uri.parse(uri.toString()), "r");
			result.mStream = new ParcelFileDescriptor.AutoCloseInputStream(fd);
//					context.getContentResolver().openInputStream(Uri.parse(uri.toString()));
			result.mAvaiableDataSize = fd.getStatSize();
		} else {
			result = DataStreamWrapper.getStream(uri);
		}
		return result;
	}
}
