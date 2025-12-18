package ih.pam.pamobile_jelahjahmalang.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatBubble(
    message: String,
    isUser: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isUser) Color(0xFF4A90E2) else Color(0xFFE8E8E8),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp,
            modifier = Modifier
                // FIX: Mengganti widthIn(max = 280.dp) dengan fillMaxWidth(0.8f).
                // Ini memungkinkan bubble melebar hingga 80% dari layar,
                // sehingga teks panjang tidak terpotong atau terlalu cepat pindah baris.
                .fillMaxWidth(0.8f)
                .padding(8.dp)
        ) {
            Text(
                text = message,
                color = if (isUser) Color.White else Color.Black,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}