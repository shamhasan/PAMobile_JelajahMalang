package ih.pam.pamobile_jelahjahmalang.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatListScreen(
    onChatClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Chat History",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        // di sini nanti kamu bisa buat list chat jika multi-chat
        // sementara ini dummy satu tombol
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onChatClick() }
                .padding(16.dp)
        ) {
            Text("Chatbot Jelajah Malang")
        }
    }
}