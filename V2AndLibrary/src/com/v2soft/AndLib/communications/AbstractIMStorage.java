package com.v2soft.AndLib.communications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Instant messenger storage template.
 * @author V.Shcryabets<vshcryabets@gmail.com>
 * @param <UID> user id class
 * @param <U> user class
 * @param <MID> message id class
 * @param <M> message class
 * @param <CID> chat id class
 * @param <C> chat class
 */
public class AbstractIMStorage<
    UID,
    U extends AbstractCommunicationUser<UID>,
    MID,
    M extends AbstractCommunicationMessage<U, MID>,
    CID,
    C extends AbstractCommunicationChat<M, MID, CID>> {
    
    public interface ChatFactory<CID, C> {
        public C newInstance(CID id);
    }
    // ==================================================================
    // CLass fields
    // ==================================================================
    private ChatFactory<CID, C> mChatFactory;
    private Map<CID, C> mChatsMap;
    private Map<MID, M> mMessagesMap;
    private AbstractCommunicationContactsList<U, UID> mContacts;
    
    public AbstractIMStorage(ChatFactory<CID, C> chatFactory) {
        mContacts = new AbstractCommunicationContactsList<U, UID>();
        mMessagesMap = new HashMap<MID, M>();
        mChatsMap = new HashMap<CID, C>();
        setChatFactory(chatFactory);
    }
    /**
     * 
     * @return contacts list
     */
    public AbstractCommunicationContactsList<U, UID> getContacts() {
        return mContacts;
    }
    /**
     * Delete all chats & messages
     */
    public void removeAllChats() {
        mChatsMap.clear();
    }
    /**
     * Return chat for specified user. If chat doesn't exist, the new one will be created.
     */
    public C getChatById(CID id) {
        if ( mChatsMap.containsKey(id)) {
            return mChatsMap.get(id);
        } else {
            if ( mChatFactory == null ) {
                throw new NullPointerException("Chat factory is null");
            }
            final C chat = mChatFactory.newInstance(id);
            mChatsMap.put(id, chat);
            return chat;
        }
    }
    /**
     * Adds message to chat with spefied user
     */
    public boolean addMessage(CID chatId, M message) {
        final C chat = getChatById(chatId);
        mMessagesMap.put(message.getId(), message);
        return chat.addMessage(message);
    }
    /**
     * Returns message by specified message id
     */
    public M getMessageById(MID id) {
        return mMessagesMap.get(id);
    }
    /**
     * Delete message
     * @return true if message was found and deleted
     */
    public boolean deleteMessage(M message) {
        boolean res = false;
        final C recipientChat = mChatsMap.get(message.getRecepient());
        final C senderChat = mChatsMap.get(message.getSender());
        if ( recipientChat != null ) {
            recipientChat.removeMessage(message);
            res = true;
        }
        if ( senderChat != null ) {
            senderChat.removeMessage(message);
            res = true;
        }
        if ( mMessagesMap.containsKey(message.getId())) {
            mMessagesMap.remove(message.getId());
        }
        return res;
    }
    /**
     * Delete message by specified id
     * @return true if message was found and deleted
     */
    public boolean deleteMessageById(MID mid) {
        if ( !mMessagesMap.containsKey(mid)) {
            return false;
        }
        final M message = mMessagesMap.get(mid);
        return deleteMessage(message);
    }
    /**
     * Set chat factory
     */
    public void setChatFactory(ChatFactory<CID, C> factory) {
        mChatFactory = factory;
    }
    /**
     * Return chat list
     */
    public List<C> getChats() {
        return new ArrayList<C>(mChatsMap.values());
    }
    /**
     * @return number of unread messages
     */
    public int getUnreadMessageCount() {
        int res = 0;
        for (C chat : mChatsMap.values()) {
            res += chat.getUnreadMessageCount();
        }
        return res;
    }
}
