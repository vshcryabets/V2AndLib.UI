package com.v2soft.AndLib.communications;

import java.util.HashMap;
import java.util.Map;

/**
 * Contacts list
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 * @param <U> contact data type
 * @param <ID> contact id data type
 */
public class AbstractCommunicationContactsList<U extends AbstractCommunicationUser<ID>, ID> {
    protected Map<ID, U> mContactsMap;
    
    public AbstractCommunicationContactsList() {
        mContactsMap = new HashMap<ID, U>();
    }
    
    public void addContact(U contact) {
        mContactsMap.put(contact.getId(), contact);
    }
    
    public U findContactById(ID id) {
        return mContactsMap.get(id);
    }

}
