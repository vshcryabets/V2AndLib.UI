package com.v2soft.AndLib.communications;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class AbstractCommunicationUser<ID> {
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
