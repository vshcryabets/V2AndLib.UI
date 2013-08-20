package com.v2soft.AndLib.communications;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author vshcryabets@gmail.com
 * @param <M> message type type
 * @param <MID> message id type
 * @param <CID> chat id type
 *
 */
public class AbstractCommunicationChat<M extends AbstractCommunicationMessage<?, MID>, MID, CID> 
implements Serializable {
    private static final long serialVersionUID = 1L;
    public interface AbstractCommunicationChatListener {
        void onChatChanged();
    }
    protected List<M> mMessages;
    protected CID mChatId;
    transient protected AbstractCommunicationChatListener mListener;
    protected int mUnreadCount;

    public AbstractCommunicationChat() {
        mMessages = new ArrayList<M>();
        mUnreadCount = 0;
    }

    public List<M> getMessages() {
        return mMessages;
    }

    /**
     * Set messages list for this chat
     * @param messages
     */
    public void setMessages(List<M> messages) {
        if (messages == null ) {
            throw new NullPointerException();
        }
        synchronized (mMessages) {
            mMessages = messages;
            synchronized (mMessages) {
                mUnreadCount = 0;
                for (M m : mMessages) {
                    if ( !m.isRead()) {
                        mUnreadCount++;
                    }
                }
            }
        }
    }
    /**
     * Add message to the chat. If message with same id was already in a list, false will be returned.
     * @param message true if message was added successfully
     */
    public boolean addMessage(M message) {
        synchronized (mMessages) {
            if ( mMessages.contains(message)) {
                return false;
            }
            boolean res = mMessages.add(message);
            if ( res && !message.isRead() ) {
                mUnreadCount++;
            }
            if ( res && mListener != null ) {
                mListener.onChatChanged();
            }
            return res;
        }
    }

    public boolean removeMessage(M message) {
        synchronized (mMessages) {
            int idx = mMessages.indexOf(message);
            if ( idx < 0 ) {
                return false;
            }
            message = mMessages.get(idx);
            if ( !message.isRead() ) {
                mUnreadCount--;
            }
            mMessages.remove(idx);
            if ( mListener != null ) {
                mListener.onChatChanged();
            }
            return true;
        }
    }
    /**
     * Find and return the index in this chat of the the specified message, or -1 if this chat does not contain this message.
     * @return
     */
    public int indexOf(M message) {
        return mMessages.indexOf(message);
    }
    public AbstractCommunicationChatListener getListener() {return mListener;}
    public void setListener(AbstractCommunicationChatListener listener) {mListener = listener;}
    /**
     * Get this chat id
     */
    public CID getId() {
        return mChatId;
    }
    /**
     * Set chat id
     */
    public void setId(CID id) {
        mChatId = id;
    }
    /**
     * @return number of unread messages
     */
    public int getUnreadMessageCount() {
        return mUnreadCount;
    }
    /**
     * Mark specified message as read
     * @param message
     * @return true is message marked
     */
    public boolean setMessageRead(M message) {
        synchronized (mMessages) {
            int idx = mMessages.indexOf(message);
            if ( idx < 0 ) {
                return false;
            }
            message = mMessages.get(idx);
            message.setRead(true);
            mUnreadCount--;
            return true;
        }
    }
}
