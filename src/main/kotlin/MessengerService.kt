import java.lang.RuntimeException

object MessengerService {
    var messagesCounter = 0
    var users = mutableListOf<User>()

    var message = { id: Int, text: String -> Message(id, text) }

    var newChat = { id1: Int, id2: Int -> Chat(id1, id2) }

    val deleteChat = { idChat: Int, idUser: Int -> users[idUser].chats.remove(idChat) }

//    val deleteMessage = { idChat: Int, idUser: Int, idMessage: Int ->
//        users[idUser].chats[idChat]!!.messages.removeAt(idMessage)
//    }

    fun deleteMessage(idUser: Int, idChat: Int, idMessage: Int): Message {
        if (users[idUser].chats[idChat]!!.messages.isEmpty()) {
            deleteChat(idChat, idUser)

        }
        val currentMessage = users[idUser].chats[idChat]!!.messages[idMessage]
        users[idUser].deletedMessages.add(currentMessage)
        return users[idUser].chats[idChat]!!.messages.removeAt(idMessage)
    }


    fun createMessage(id1: Int, id2: Int, text: String): Message {

        if (!users.any() { it.id == id1 }) {
            throw RuntimeException("Ошибка доступа! Пользователь $id1 не найден")
        }
        if (!users[id1].chats.any() { it.key == id2 }) {
            users[id1].chats[id2] = newChat(id1, id2)
            users[id2].chats[id1] = newChat(id2, id1)
        }
        messagesCounter++
        users[id1].chats[id2]?.messages?.add(message(messagesCounter, text))
        users[id1].allMessages.add(message(messagesCounter, text))
        users[id2].chats[id1]!!.messages.add(message(messagesCounter, text))
        users[id2].allMessages.add(message(messagesCounter, text))

        return users[id2].allMessages.last()
    }


    fun editMessage(userId: Int, chatId: Int, messageId: Int): Message {
        if (!users.any { it.id == userId }) {
            throw  UserNotFoundException("Пользователь с id $userId не найден")
        }
        if (!users[userId].chats.any { it.key == chatId }) {
            throw ChatNotFoundException("Чат с id $chatId не найден")
        }
        if (!users[userId].chats[chatId]?.messages!!.any { it.id == messageId }) {
            throw MessageNotFoundException("Сообщение с id $messageId не найдено")
        }
        val newText = users[userId].chats[chatId]!!.messages[messageId].text + " (изменено)"
        users[userId].chats[chatId]!!.messages[messageId].text = newText
        return users[userId].chats[chatId]!!.messages[messageId]
    }


    //Буду признателен за совет, как можно сократить эту функцию
    fun read(userId: Int, lastMessageId: Int, messagesCount: Int): List<Message> {
        val readMessages = mutableListOf<Message>()
        if (lastMessageId + messagesCount >= users[userId].allMessages.size) {
            val reallyLastMessageId = users[userId].allMessages.count { it.id > 0 }
            for (i in lastMessageId until reallyLastMessageId) {
                users[userId].allMessages[i].isRead = true
                readMessages.add(users[userId].allMessages[i])
            }
        } else {
            val reallyLastMessageId = lastMessageId + messagesCount
            for (i in lastMessageId..reallyLastMessageId) {
                users[userId].allMessages[i].isRead = true
                readMessages.add(users[userId].allMessages[i])
            }
        }
        return readMessages
    }

    val allUnreadMessages = { id: Int -> users[id].allMessages.filter { !it.isRead } }
    val unreadMessagesInChat =
        { userId: Int, chatId: Int ->
            users[userId].chats[chatId]!!.messages.filter { !it.isRead }
        }

    val getAllChats = { id: Int -> users[id].chats }
    val getUnreadChatsCount =
        { id: Int -> users[id].chats.count { chats -> chats.value.messages.any { !it.isRead } } }

}


class Chat(id1: Int, id2: Int) {
    val id = id2
    var messages = mutableListOf<Message>()
}

data class Message(val id: Int, var text: String) {
    var isRead = false
}

class UserNotFoundException(message: String) : RuntimeException()
class ChatNotFoundException(message: String) : RuntimeException()
class MessageNotFoundException(message: String) : RuntimeException()
