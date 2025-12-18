package ih.pam.pamobile_jelahjahmalang.data.repository


import ih.pam.pamobile_jelahjahmalang.data.remote.ApiServiceGemini
import ih.pam.pamobile_jelahjahmalang.models.ChatMessage
import ih.pam.pamobile_jelahjahmalang.models.ChatSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChatRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {


    private fun sessionsRef(userId: String) =
        firestore.collection("chats").document(userId).collection("sessions")

    private fun messagesRef(userId: String, sessionId: String) =
        sessionsRef(userId).document(sessionId).collection("messages")


    private suspend fun ensureUserId(): String {
        val current = auth.currentUser
        return if (current != null) {
            current.uid
        } else {
            val result = auth.signInAnonymously().await()
            result.user?.uid ?: throw Exception("Failed to sign in anonymously")
        }
    }


    suspend fun createSession(title: String = "New Chat"): String {
        val uid = ensureUserId()
        val sessionId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val session = ChatSession(
            sessionId = sessionId,
            title = title,
            createdAt = now,
            updatedAt = now,
            lastMessage = ""
        )

        sessionsRef(uid).document(sessionId).set(session).await()
        return sessionId
    }

    fun listenSessions(): Flow<List<ChatSession>> = callbackFlow {
        val uid = try {
            kotlinx.coroutines.runBlocking { ensureUserId() }
        } catch (e: Exception) {
            close(e); return@callbackFlow
        }

        val listener = sessionsRef(uid)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val list = snapshot?.toObjects<ChatSession>() ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }


    fun listenMessages(sessionId: String): Flow<List<ChatMessage>> = callbackFlow {
        val uid = try {
            kotlinx.coroutines.runBlocking { ensureUserId() }
        } catch (e: Exception) {
            close(e); return@callbackFlow
        }

        val listener = messagesRef(uid, sessionId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val list = snapshot?.toObjects<ChatMessage>() ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }


    suspend fun sendUserMessage(sessionId: String, text: String) {
        val uid = ensureUserId()
        val msgId = UUID.randomUUID().toString()

        val msg = ChatMessage(
            id = msgId,
            text = text,
            sender = "user",
            timestamp = System.currentTimeMillis()
        )

        messagesRef(uid, sessionId).document(msgId).set(msg).await()

        sessionsRef(uid).document(sessionId).update(
            mapOf(
                "lastMessage" to text,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }


    private suspend fun sendBotMessage(sessionId: String, text: String) {
        val uid = ensureUserId()
        val msgId = UUID.randomUUID().toString()

        val msg = ChatMessage(
            id = msgId,
            text = text,
            sender = "bot",
            timestamp = System.currentTimeMillis()
        )

        messagesRef(uid, sessionId).document(msgId).set(msg).await()

        sessionsRef(uid).document(sessionId).update(
            mapOf(
                "lastMessage" to text,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun generateReplyAndSave(sessionId: String, prompt: String): String {

        val reply = try {
            ApiServiceGemini.generateReply(prompt)     // FIXED HERE
        } catch (e: Exception) {
            e.printStackTrace()
            "Maaf, saya kesulitan memahami pertanyaan. Coba lagi ya!"
        }

        sendBotMessage(sessionId, reply)
        return reply
    }

    suspend fun sendUserAndGetReply(sessionId: String, userText: String): String {

        sendUserMessage(sessionId, userText)

        return generateReplyAndSave(sessionId, userText)
    }
}
