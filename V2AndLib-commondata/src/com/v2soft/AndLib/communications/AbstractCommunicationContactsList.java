/*
 * Copyright (C) 2013 V.Shcryabets (vshcryabets@gmail.com)
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
