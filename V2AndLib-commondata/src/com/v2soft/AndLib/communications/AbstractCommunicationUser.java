package com.v2soft.AndLib.communications;

import com.v2soft.AndLib.dao.AbstractProfile;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class AbstractCommunicationUser<ID> extends AbstractProfile<ID> {
    private static final long serialVersionUID = 1L;
    public AbstractCommunicationUser() {
        super();
    }
    public AbstractCommunicationUser(ID id, String name) {
        super(id, name);
    }
}
