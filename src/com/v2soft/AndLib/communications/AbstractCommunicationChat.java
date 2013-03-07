package com.v2soft.AndLib.communications;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class AbstractCommunicationChat<M extends AbstractCommunicationMessage<?, ?>,
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
    public void addMessage(M message) {
        mMessages.add(message);
        if ( mListener != null ) {
            mListener.onChatChanged();
        }
    }

    public boolean removeMessage(M message) {
        boolean res = mMessages.remove(message);
        if ( mListener != null ) {
            mListener.onChatChanged();
        }
        return res;
    }
    public L getListener() {return mListener;}
    public void setListener(L listener) {mListener = listener;}
}
