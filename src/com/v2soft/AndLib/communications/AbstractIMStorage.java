package com.v2soft.AndLib.communications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Instant messenger storage template.
 * @author V.Shcryabets<vshcryabets@gmail.com>
 * @param UID user id class
 * @param U user class
 * @param MID message id class
 * @param M message class
 * @param L chat listener class
 * @param C chat class
 */
public class AbstractIMStorage<
    UID,
    U extends AbstractCommunicationUser<UID>,
    MID,
    M extends AbstractCommunicationMessage<U, MID>,
    L extends AbstractCommunicationChat.AbstractCommunicationChatListener,
    C extends AbstractCommunicationChat<M, MID, L>> {
    
    public interface ChatFactory<U, C> {
        public C newInstance(U user);
    }
    // ==================================================================
    // CLass fields
    // ==================================================================
    private ChatFactory<U, C> mChatFactory;
    private Map<U, C> mChatsMap;
    private Map<MID, M> mMessagesMap;
    private AbstractCommunicationContactsList<U, UID> mContacts;
    
    public AbstractIMStorage(ChatFactory<U, C> chatFactory) {
        mContacts = new AbstractCommunicationContactsList<U, UID>();
        mMessagesMap = new HashMap<MID, M>();
        mChatsMap = new HashMap<U, C>();
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
    public C getChatByUser(U user) {
        if ( mChatsMap.containsKey(user)) {
            return mChatsMap.get(user);
        } else {
            if ( mChatFactory == null ) {
                throw new NullPointerException("Chat factory is null");
            }
            final C chat = mChatFactory.newInstance(user);
            mChatsMap.put(user, chat);
            return chat;
        }
    }
    /**
     * Adds message to chat with spefied user
     */
    public boolean addMessage(U user, M message) {
        final C chat = getChatByUser(user);
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
        final C senderChat = mChatsMap.get(message.getRecepient());
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
    public void setChatFactory(ChatFactory<U, C> factory) {
        mChatFactory = factory;
    }
    /**
     * Return chat list
     */
    public List<C> getChats() {
        return new ArrayList<C>(mChatsMap.values());
    }
}
