package ih.pam.pamobile_jelahjahmalang.models

data class ChatMessage(
    val id: String = "",
    val text: String = "",
    val sender: String = "", // "user" or "bot"
    val timestamp: Long = System.currentTimeMillis()
)