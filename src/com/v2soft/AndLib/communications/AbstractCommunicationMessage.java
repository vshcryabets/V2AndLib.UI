package com.v2soft.AndLib.communications;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 * @param <U> communication user data type
 * @param <ID> message ID
 */
public class AbstractCommunicationMessage<U extends AbstractCommunicationUser<?>, ID> {
    protected ID mId;
    protected U mSender;
    protected U mRecepient;
    /**
     * @return the mId
     */
    public ID getmId() {
        return mId;
    }
    /**
     * @return the mSender
     */
    public U getmSender() {
        return mSender;
    }
    /**
     * @return the mRecepient
     */
    public U getmRecepient() {
        return mRecepient;
    }
    /**
     * @param mId the mId to set
     */
    public void setmId(ID mId) {
        this.mId = mId;
    }
    /**
     * @param mSender the mSender to set
     */
    public void setmSender(U mSender) {
        this.mSender = mSender;
    }
    /**
     * @param mRecepient the mRecepient to set
     */
    public void setmRecepient(U mRecepient) {
        this.mRecepient = mRecepient;
    }
}
