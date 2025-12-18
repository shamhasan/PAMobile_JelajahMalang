package ih.pam.pamobile_jelahjahmalang.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ih.pam.pamobile_jelahjahmalang.screen.ChatListScreen
import ih.pam.pamobile_jelahjahmalang.screen.ChatScreen
import ih.pam.pamobile_jelahjahmalang.viewModel.ChatViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    chatViewModel: ChatViewModel
) {

    NavHost(
        navController = navController,
        startDestination = AppRoute.CHAT_LIST
    ) {

        // Halaman list chat
        composable(AppRoute.CHAT_LIST) {
            ChatListScreen(
                onChatClick = {
                    navController.navigate(AppRoute.CHAT)
                }
            )
        }

        // Halaman chat utama
        composable(AppRoute.CHAT) {
            ChatScreen(viewModel = chatViewModel)
        }
    }
}