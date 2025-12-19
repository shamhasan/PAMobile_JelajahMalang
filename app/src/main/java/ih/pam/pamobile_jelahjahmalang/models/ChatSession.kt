package ih.pam.pamobile_jelahjahmalang.models

data class ChatSession(
    val sessionId: String = "",
    val title: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastMessage: String = ""
)