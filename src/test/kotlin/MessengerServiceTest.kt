import MessengerService.allUnreadMessages
import MessengerService.deleteChat
import MessengerService.deleteMessage
import MessengerService.message
import MessengerService.messagesCounter
import MessengerService.newChat
import MessengerService.read
import MessengerService.unreadMessagesInChat
import MessengerService.users
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import java.lang.RuntimeException

class MessengerServiceTest {

    @Before
    fun setUp() {
        messagesCounter = 0
        for (i in 0..7) {
            MessengerService.users.add(i, User(i))
        }

    }

    @Test
    fun deleteMessageFull() {
        for (i in 0..3) {
            MessengerService.createMessage(users[0].id, users[1].id, "hello $i")
            deleteMessage(0, 1, 0)
        }
        assertEquals(users[0].chats[1], deleteChat(1, 0))
    }

    @Test
    fun deleteMessagePartly() {
        for (i in 0..3) {
            MessengerService.createMessage(users[0].id, users[1].id, "hello $i")
        }
        val expectation = users[0].chats[1]!!.messages[0]
        val result = deleteMessage(0, 1, 0)

        assertEquals(result, expectation)
    }

    @Test
    fun createMessage() {
        for (i in 0..3) {
            MessengerService.createMessage(users[0].id, users[1].id, "hello $i")
        }
        val expected = "hello 3"
        assertEquals(users[1].chats[0]!!.messages.last().text, expected)
    }

    @Test(expected = RuntimeException::class)
    fun shouldThrow() {
        MessengerService.createMessage(8, 0, "hi")
    }


    @Test
    fun editMessage() {
        for (i in 0..3) {
            MessengerService.createMessage(users[0].id, users[1].id, "hello $i")
        }
        users[1].chats[0]!!.messages[2] = MessengerService.editMessage(0, 1, 2)
        val expected = message(3, "hello 2 (изменено)")
        assertEquals(users[1].chats[0]!!.messages[2], expected)
    }

    @Test(expected = UserNotFoundException::class)
    fun shouldThrowUserNotFound() {
        for (i in 0..3) {
            MessengerService.createMessage(users[i].id, users[5].id, "hello $i")
        }
        MessengerService.editMessage(9, 1, 0)
    }

    @Test(expected = ChatNotFoundException::class)
    fun shouldThrowChatNotFound() {
        for (i in 0..3) {
            MessengerService.createMessage(users[i + 1].id, users[0].id, "hello ")
        }
        MessengerService.editMessage(1, 2, 0)
    }

    @Test(expected = MessageNotFoundException::class)
    fun shouldThrowMessageNotFound() {
        for (i in 0..3) {
            MessengerService.createMessage(users[0].id, users[1].id, "hello $i")
        }
        MessengerService.editMessage(0, 1, 5)
    }

    @Test
    fun read_lessThanSize() {
        for (i in 0..4) {
            MessengerService.createMessage(i, 5, "hello + $i")
        }
        val result = read(5, 2, 2)
        val expectation = users[5].allMessages.slice(2..4)
        assertEquals(result, expectation)
    }

    @Test
    fun read_moreThanSize() {
        for (i in 0..3) {
            MessengerService.createMessage(i, 5, "hello + $i")
        }
        val result = read(5, 2, 5)
        val expectation = users[5].allMessages.slice(2..3)
        assertEquals(result, expectation)
    }

    @Test
    fun setRead() {
    }

    @Test
    fun getAllUnreadMessages() {
        for (i in 0..3) {
            MessengerService.createMessage(i, 5, "hello")
        }
        val result = allUnreadMessages(5)
        val expectation = users[5].allMessages
        assertEquals(result, expectation)
    }

    @Test
    fun getUnreadChatsCount() {
        for (i in 0..3) {
            MessengerService.createMessage(i, 5, "hello")
        }
        val expected = MessengerService.getUnreadChatsCount(5)
        val result = 4
        assertEquals(expected, result)
    }

    @Test
    fun getUnreadMessagesInChat() {
        val userId = 0
        val anotherUserId = 1
        MessengerService.createMessage(userId, anotherUserId, "hello")
        val result = unreadMessagesInChat(userId, anotherUserId)
        val expected = mutableListOf(Message(1, "hello"))
        assertEquals(expected, result)
    }

    @Test
    fun getAllChats() {
        for (i in 0..3) {
            MessengerService.createMessage(i, 5, "hello")
        }
        val chatsCounter = MessengerService.getAllChats(5).count()
        val expected = 4
        assertEquals(expected, chatsCounter)
    }

    @Test
    fun getListOfMessages() {
    }
}