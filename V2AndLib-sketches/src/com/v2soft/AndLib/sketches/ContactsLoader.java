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
package com.v2soft.AndLib.sketches;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class ContactsLoader extends AsyncTaskLoader<List<ContactsData>> {

	private static final String[] DEFAULT_PROJECTION = new String[]{
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME
	};

	public ContactsLoader(Context context) {
		super(context);
	}

	@Override
	public List<ContactsData> loadInBackground() {
		Cursor cursor = getContext().getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI,
				getProjection(),
				getFilter(),
				getFilterArgs(),
				getSort()
		);
		List<ContactsData> contacts = new ArrayList<ContactsData>();
		if (cursor.moveToFirst()) {
			do {
				ContactsData item = getFromCursor(cursor);
				contacts.add(item);
			} while (cursor.moveToNext());
		}
		return contacts;
	}

	private ContactsData getFromCursor(Cursor cursor) {
		long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		ContactsData item = new ContactsData(id, name);
		return item;
	}

	public String[] getProjection() {
		return DEFAULT_PROJECTION;
	}

	public String getFilter() {
		return null;
	}

	public String[] getFilterArgs() {
		return null;
	}

	public String getSort() {
		return null;
	}
}
