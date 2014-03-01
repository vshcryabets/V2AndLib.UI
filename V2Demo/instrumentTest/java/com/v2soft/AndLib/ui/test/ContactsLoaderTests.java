package com.v2soft.AndLib.ui.test;

import android.database.Cursor;
import android.net.Uri;
import android.os.HandlerThread;
import android.provider.ContactsContract;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AndroidDataStreamWrapper;
import com.v2soft.AndLib.dataproviders.DataStreamWrapper;
import com.v2soft.AndLib.filecache.AndroidFileCache;
import com.v2soft.AndLib.filecache.JavaHashFactory;
import com.v2soft.AndLib.filecache.MD5CacheFactory;
import com.v2soft.AndLib.sketches.ContactsData;
import com.v2soft.AndLib.sketches.ContactsLoader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class ContactsLoaderTests extends AndroidTestCase {

	@SmallTest
	public void testContactsLoader() {
		Cursor cursor = getContext().getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI,
				null,
				null,
				null,
				null);
		int contactsCount = cursor.getCount();

		ContactsLoader loader = new ContactsLoader(getContext());
		List<ContactsData> contacts = loader.loadInBackground();
		assertNotNull("Contacts list is null", contacts);
		assertEquals("Contacts list size is wrong", contactsCount, contacts.size());
	}
}
