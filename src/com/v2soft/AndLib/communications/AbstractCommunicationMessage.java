package com.v2soft.AndLib.communications;

import java.io.Serializable;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 * @param <U> communication user data type
 * @param <ID> message ID
 */
public class AbstractCommunicationMessage<U extends AbstractCommunicationUser<?>, MID>
    extends AbstractCommunicationComment<U, MID>
    implements Serializable {
    private static final long serialVersionUID = 2L;
    protected U mRecepient;
    
    public AbstractCommunicationMessage() {
        isRead = false;
    }
    /**
     * Return message recipient.
     * @return message recipient.
     */
    public U getRecepient() {
        return mRecepient;
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
    }
}
