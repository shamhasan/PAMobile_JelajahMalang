package ih.pam.pamobile_jelahjahmalang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ih.pam.pamobile_jelahjahmalang.ui.navigation.AppNavGraph
import ih.pam.pamobile_jelahjahmalang.ui.theme.PAMobileJelahjahMalangTheme
import ih.pam.pamobile_jelahjahmalang.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val chatViewModel: ChatViewModel = viewModel()

            PAMobileJelahjahMalangTheme {
                AppNavGraph(
                    navController = navController,
                    chatViewModel = chatViewModel
                )
            }
        }
    }
}
