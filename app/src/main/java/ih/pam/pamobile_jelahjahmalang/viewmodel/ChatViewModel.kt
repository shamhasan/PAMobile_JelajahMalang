package ih.pam.pamobile_jelahjahmalang.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import ih.pam.pamobile_jelahjahmalang.data.remote.ApiServiceGemini
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ChatMessage(
    val id: String = "",
    val sender: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val chatCollection = firestore.collection("chats")

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadChatHistory()
    }

    fun loadChatHistory() {
        viewModelScope.launch {
            try {
                val snapshot = chatCollection
                    .orderBy("timestamp")
                    .get()
                    .await()

                val data = snapshot.documents.map { doc ->
                    ChatMessage(
                        id = doc.id,
                        sender = doc.getString("sender") ?: "",
                        content = doc.getString("content") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0
                    )
                }

                _messages.value = data

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true

            // 1. Tambah pesan user ke UI & Firestore
            val userMsg = ChatMessage(
                sender = "user",
                content = userText
            )
            saveMessage(userMsg)

            // 2. Panggil API Gemini
            val botReplyText: String = try {
                ApiServiceGemini.generateReply(userText)
            } catch (e: Exception) {
                e.printStackTrace()
                "Maaf, terjadi kesalahan saat meminta jawaban. Coba lagi beberapa saat."
            }

            // 3. Tambah pesan bot
            val botMsg = ChatMessage(
                sender = "bot",
                content = botReplyText
            )
            saveMessage(botMsg)

            _isLoading.value = false
        }
    }


    private suspend fun saveMessage(message: ChatMessage) {
        val data = mapOf(
            "sender" to message.sender,
            "content" to message.content,
            "timestamp" to message.timestamp
        )

        chatCollection.document().set(data).await()

        // Update UI state
        _messages.value = _messages.value + message
    }

    fun clearChat() {
        viewModelScope.launch {
            try {
                val snapshot = chatCollection.get().await()
                snapshot.documents.forEach { it.reference.delete() }
                _messages.value = emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
