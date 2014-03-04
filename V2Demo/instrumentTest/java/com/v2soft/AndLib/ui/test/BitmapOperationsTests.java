package com.v2soft.AndLib.ui.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.ImageView;

import com.v2soft.AndLib.media.BitmapOperations;

import java.io.IOException;
import java.net.URI;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class BitmapOperationsTests extends AndroidTestCase {
	public static final String ASSET_FILE_PATH = "file:///android_asset/data.jpg";
	public static final String ASSET_LARGE_FILE_PATH = "file:///android_asset/large.jpg";
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

	@SmallTest
	public void testScaleFactorCalculations() throws IOException {
		Rect sourceBitmap = new Rect(0,0,4096,2048);
		Rect screenRect = new Rect(0, 0, 480, 800);
		BitmapOperations helper = new BitmapOperations(getContext());
		float scaleFactor = helper.getScaleFactor(BitmapOperations.ScaleType.FIT_CENTER, screenRect, sourceBitmap);
		assertTrue("Wrong fit center scale factor", Math.abs(scaleFactor-0.11718) < 0.001);
		scaleFactor = helper.getScaleFactor(BitmapOperations.ScaleType.CENTER_CROP, screenRect, sourceBitmap);
		assertTrue("Wrong fit center scale factor", Math.abs(scaleFactor-0.39062) < 0.001);

		URI uri = URI.create(ASSET_LARGE_FILE_PATH);
		BitmapFactory.Options options = helper.getBitmapScaleOptions(uri, screenRect, BitmapOperations.ScaleType.FIT_CENTER);
		assertNotNull("No bitmap options", options);
		assertEquals("Wrong inSample size", options.inSampleSize, 4);

		Bitmap bitmap = helper.loadBitmap(uri, screenRect, BitmapOperations.ScaleType.CENTER_CROP, false);
		assertNotNull("No bitmap", bitmap);
		assertTrue("Wrong bitmap size", bitmap.getWidth() <= screenRect.width()
				|| bitmap.getHeight() <= screenRect.height() );
		bitmap.recycle();
		bitmap = helper.loadBitmap(uri, screenRect, BitmapOperations.ScaleType.FIT_CENTER, false);
		assertNotNull("No bitmap", bitmap);
		assertTrue("Wrong bitmap size", bitmap.getWidth() <= screenRect.width()
				&& bitmap.getHeight() <= screenRect.height());
	}

	@SmallTest
	public void testScale() throws IOException {
		URI uri = URI.create(ASSET_FILE_PATH);
		int width = 384;
		Rect margins = new Rect(0, 0, width, width);
		BitmapOperations helper = new BitmapOperations(getContext());
		Bitmap loadedImage = helper.loadBitmap(uri);
		Bitmap scaledImage = helper.scaleBitmap(loadedImage, margins, BitmapOperations.ScaleType.FIT_CENTER, true);
		assertNotNull("No bitmap", scaledImage);
		assertEquals("Wrong bitmap width", 384, scaledImage.getWidth());
		assertEquals("Wrong bitmap height", 320, scaledImage.getHeight());
	}
}
