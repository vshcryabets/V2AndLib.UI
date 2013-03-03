package com.v2soft.AndLib.communications;

import java.io.Serializable;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 * @param <U> communication user data type
 * @param <ID> message ID
 */
public class AbstractCommunicationMessage<U extends AbstractCommunicationUser<?>, ID> 
    implements Serializable {
    private static final long serialVersionUID = 1L;
    protected ID mId;
    protected U mSender;
    protected U mRecepient;
    /**
     * @return the mId
     */
    public ID getId() {
        return mId;
    }
    /**
     * @return the mSender
     */
    public U getSender() {
        return mSender;
    }
    /**
     * @return the mRecepient
     */
    public U getRecepient() {
        return mRecepient;
    }
    /**
     * @param mId the mId to set
     */
    public void setId(ID mId) {
        this.mId = mId;
    }
    /**
     * @param mSender the mSender to set
     */
    public void setSender(U mSender) {
        this.mSender = mSender;
    }
    /**
     * @param mRecepient the mRecepient to set
     */
    public void setRecepient(U mRecepient) {
        this.mRecepient = mRecepient;
    }
}
eturn mId;
    }
    /**
     * @return the mSender
     */
    public U getSender() {
        return mSender;
    }
    /**
     * @return the mRecepient
     */
    public U getRecepient() {
        return mRecepient;
    }
    /**
     * @param mId the mId to set
     */
    public void setId(ID mId) {
        this.mId = mId;
    }
    /**
     * @param mSender the mSender to set
     */
    public void setSender(U mSender) {
        this.mSender = mSender;
    }
    /**
     * @param mRecepient the mRecepient to set
     */
    public void setRecepient(U mRecepient) {
        this.mRecepient = mRecepient;
    }
    
    @Override
    public int hashCode() {
        return mId.hashCode();
    }
    
    public boolean equals(Object o) {
        if ( o == null ) {
            return false;
        }
        if ( o instanceof AbstractCommunicationMessage) {
            AbstractCommunicationMessage<?, ?> msg = (AbstractCommunicationMessage<?, ?>) o;
            return mId.equals(msg.mId);
        }
        return false;
    };
}
