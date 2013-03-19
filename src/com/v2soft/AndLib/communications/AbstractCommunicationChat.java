package com.v2soft.AndLib.communications;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class AbstractCommunicationChat<M extends AbstractCommunicationMessage<?, ID>, ID,
    L extends AbstractCommunicationChat.AbstractCommunicationChatListener> 
    implements Serializable {
    private static final long serialVersionUID = 1L;
    public interface AbstractCommunicationChatListener {
        void onChatChanged();
    }
    protected List<M> mMessages;
    transient protected L mListener;
    
    public AbstractCommunicationChat() {
        mMessages = new ArrayList<M>();
    }

    public List<M> getMessages() {
        return mMessages;
    }

    public void setMessages(List<M> mMessages) {
        this.mMessages = mMessages;
    }
    /**
     * Add message to the chat. If message with same id was already in a list, false will be returned.
     * @param message true if message was added successfully
     */
    public boolean addMessage(M message) {
        if ( mMessages.contains(message)) {
            return false;
        }
        boolean res = mMessages.add(message);
        if ( res && mListener != null ) {
            mListener.onChatChanged();
        }
        return res;
    }

    public boolean removeMessage(M message) {
        boolean res = mMessages.remove(message);
        if ( res && mListener != null ) {
            mListener.onChatChanged();
        }
        return res;
    }
    /**
     * Find and return the index in this chat of the the specified message, or -1 if this chat does not contain this message.
     * @return
     */
    public int indexOf(M message) {
        return mMessages.indexOf(message);
    }
    public L getListener() {return mListener;}
    public void setListener(L listener) {mListener = listener;}
}
