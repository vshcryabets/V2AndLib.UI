package com.v2soft.AndLib.communications;

import java.io.Serializable;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class AbstractCommunicationUser<ID> implements Serializable {
    private static final long serialVersionUID = 1L;
    protected ID mId;
    protected String mUserName;

    public ID getId() {
        return mId;
    }
    public String getUserName() {
        return mUserName;
    }


    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public void setId(ID mId) {
        this.mId = mId;
    }
}
