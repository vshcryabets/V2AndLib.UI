import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.v2soft.AndLib.communications.AbstractCommunicationChat;
import com.v2soft.AndLib.communications.AbstractCommunicationMessage;
import com.v2soft.AndLib.communications.AbstractCommunicationUser;
import com.v2soft.AndLib.communications.AbstractIMStorage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for communication classes
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public class CommunicationlogicTests {
    class TestUser extends AbstractCommunicationUser<UUID> {
        private static final long serialVersionUID = 1L;
    }
    class TestMessage extends AbstractCommunicationMessage<TestUser, UUID> {
        private static final long serialVersionUID = 1L;
        public TestMessage(UUID id) {
            mId = id;
        }
    }
    class TestChat extends AbstractCommunicationChat<TestMessage, UUID, UUID> {
        private static final long serialVersionUID = 1L;
        public TestChat(UUID id) {
            mChatId = id;
        }
    }
    class TestStorage extends AbstractIMStorage<UUID, TestUser, UUID, TestMessage, UUID, TestChat> {
        public TestStorage() {
            super(new ChatFactory<UUID, TestChat>() {
                @Override
                public TestChat newInstance(UUID id) {
                    return new TestChat(id);
                }
            });
        }
    }

    @Test
    public void testUnreadmessageCounterInChat() {
        TestChat chat = new TestChat(UUID.randomUUID());
        List<TestMessage> msg = new ArrayList<TestMessage>();
        TestMessage l1 = new TestMessage(UUID.randomUUID());
        TestMessage l2 = new TestMessage(UUID.randomUUID());
        TestMessage l3 = new TestMessage(UUID.randomUUID());
        msg.add(l1);
        msg.add(l2);
        msg.add(l3);
        chat.setMessages(msg);
        assertEquals(3, chat.getUnreadMessageCount());
        TestMessage m1 = new TestMessage(UUID.randomUUID());
        TestMessage m2 = new TestMessage(UUID.randomUUID());
        TestMessage m3 = new TestMessage(UUID.randomUUID());
        TestMessage m4 = new TestMessage(UUID.randomUUID());
        chat.removeMessage(l1);
        chat.addMessage(m1);
        chat.addMessage(m2);
        chat.addMessage(m3);
        assertEquals(5, chat.getUnreadMessageCount());
        chat.removeMessage(m2);
        assertEquals(4, chat.getUnreadMessageCount());
        chat.removeMessage(m1);
        chat.removeMessage(m2);
        chat.addMessage(m4);
        assertEquals(4, chat.getUnreadMessageCount());
        chat.removeMessage(m3);
        chat.removeMessage(m4);
        assertEquals(2, chat.getUnreadMessageCount());
    }

    @Test
    public void testUnreadmessageCounterInIMStorage() {
        TestStorage storage = new TestStorage();
        int unreadMessageCount = 0;
        Random rnd = new Random();
        for (int i = 0; i < 5 + rnd.nextInt(5); i++ ) {
            UUID chatId = UUID.randomUUID();
//            TestChat chat = storage.getChatById(chatId);
            for ( int j = 0; j < 5+rnd.nextInt(20); j++) {
                TestMessage msg = new TestMessage(UUID.randomUUID());
                storage.addMessage(chatId, msg);
                unreadMessageCount++;
            }
        }
        assertEquals(unreadMessageCount, storage.getUnreadMessageCount());
    }
}
