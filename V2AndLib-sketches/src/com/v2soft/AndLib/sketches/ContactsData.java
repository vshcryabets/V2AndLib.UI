package com.v2soft.AndLib.sketches;

import com.v2soft.AndLib.communications.AbstractCommunicationUser;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class ContactsData extends AbstractCommunicationUser<Long> {
	public ContactsData(long id, String name) {
		super(id, name);
	}
}
