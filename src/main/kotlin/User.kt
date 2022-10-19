data class User(val id: Int) {
    var chats = mutableMapOf<Int, Chat>()
    var allMessages = mutableListOf<Message>()
    val deletedMessages = mutableListOf<Message>()
}

fun main() {

}