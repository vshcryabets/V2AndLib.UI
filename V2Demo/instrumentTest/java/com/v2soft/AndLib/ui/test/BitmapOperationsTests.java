package com.v2soft.AndLib.ui.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.media.BitmapOperations;

import java.io.IOException;
import java.net.URI;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class BitmapOperationsTests extends AndroidTestCase {
	public static final String ASSET_FILE_PATH = "file:///android_asset/data.jpg";
	public static final String HTTPS_FILE_PATH = "https://dl.dropboxusercontent.com/u/18391781/bookpureandroid.png";


	@SmallTest
	public void testLoadBitmapOptions() throws IOException {
		URI uri = URI.create(ASSET_FILE_PATH);
		BitmapOperations helper = new BitmapOperations(getContext());
		BitmapFactory.Options options = helper.getBitmapOptions(uri);
		assertNotNull("No bitmap options", options);
		assertEquals("Wrong bitmap width", 480, options.outWidth);
		assertEquals("Wrong bitmap height", 400, options.outHeight);
		assertEquals("Wrong bitmap mime", "image/jpeg", options.outMimeType);

		Bitmap bitmap = helper.loadBitmap(uri);
		assertNotNull("No bitmap", bitmap);
		bitmap.recycle();
	}

	@SmallTest
	public void testLoadBitmapOptionsFromHTTPS() throws IOException {
		URI uri = URI.create(HTTPS_FILE_PATH);
		BitmapOperations helper = new BitmapOperations(getContext());
		BitmapFactory.Options options = helper.getBitmapOptions(uri);
		assertNotNull("No bitmap options", options);
		assertEquals("Wrong bitmap width", 480, options.outWidth);
		assertEquals("Wrong bitmap height", 800, options.outHeight);
		assertEquals("Wrong bitmap mime", "image/png", options.outMimeType);

		Bitmap bitmap = helper.loadBitmap(uri);
		assertNotNull("No bitmap", bitmap);
		bitmap.recycle();
	}
}
