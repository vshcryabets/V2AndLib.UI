package com.v2soft.AndLib.communications;

import java.util.List;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class AbstractCommunicationChat<M extends AbstractCommunicationMessage<?, ?>> {
    protected List<M> mMessages;

    public List<M> getMessages() {
        return mMessages;
    }

    public void setMessages(List<M> mMessages) {
        this.mMessages = mMessages;
    }

}
