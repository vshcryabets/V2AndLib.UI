package com.v2soft.AndLib.demotest;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.sketches.ContactsData;
import com.v2soft.AndLib.sketches.ContactsLoader;

import java.util.List;

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
