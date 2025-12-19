package ih.pam.pamobile_jelahjahmalang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// --- IMPORT DARI FITUR ISHAM (Login, Register, Mission) ---
import ih.pam.pamobile_jelahjahmalang.screen.DetailMissionScreen
import ih.pam.pamobile_jelahjahmalang.screen.LoginScreen
import ih.pam.pamobile_jelahjahmalang.screen.MissionScreen
import ih.pam.pamobile_jelahjahmalang.screen.RegisterScreen
import ih.pam.pamobile_jelahjahmalang.ui.theme.PAMobileJelahjahMalangTheme

// --- IMPORT DARI FITUR PRICIL (Feed, Detail, ViewModel) ---
import ih.pam.pamobile_jelahjahmalang.screen.FeedScreen
import ih.pam.pamobile_jelahjahmalang.screen.DetailScreen
import ih.pam.pamobile_jelahjahmalang.utils.PlaceRepository
import ih.pam.pamobile_jelahjahmalang.network.RetrofitInstance
import ih.pam.pamobile_jelahjahmalang.viewmodel.PlaceViewModel
import pg.mobile.projectpampricil.ui.PlaceViewModelFactory

// --- IMPORT DARI FITUR CHATBOT ---
import ih.pam.pamobile_jelahjahmalang.screen.ChatListScreen
import ih.pam.pamobile_jelahjahmalang.screen.ChatScreen
import ih.pam.pamobile_jelahjahmalang.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. SETUP LOGIKA PRICIL (Repository)
        val repository = PlaceRepository(RetrofitInstance.apiService)

        setContent {
            PAMobileJelahjahMalangTheme {

                // 2. SETUP VIEWMODEL
                val placeViewModel: PlaceViewModel = viewModel(
                    factory = PlaceViewModelFactory(repository)
                )

                // 3. SETUP CHATBOT VIEWMODEL (Shared untuk semua screen chat)
                val chatViewModel: ChatViewModel = viewModel()

                val navController = rememberNavController()

                // 4. NAVIGASI UTAMA
                NavHost(navController = navController, startDestination = "login") {

                    // === AREA LOGIN & REGISTER (Isham) ===
                    composable("login") {
                        LoginScreen(navController = navController)
                    }

                    composable("register") {
                        RegisterScreen(navController = navController)
                    }

                    // === AREA FITUR WISATA (Pricil) ===
                    composable("feed") {
                        FeedScreen(navController = navController, vm = placeViewModel)
                    }

                    composable(
                        route = "detail/{placeId}",
                        arguments = listOf(navArgument("placeId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val placeId = backStackEntry.arguments?.getString("placeId") ?: ""
                        DetailScreen(
                            placeId = placeId,
                            navController = navController,
                            vm = placeViewModel
                        )
                    }

                    // === AREA FITUR MISI (Isham) ===
                    composable("mission_list") {
                        MissionScreen(navController = navController)
                    }

                    composable(
                        route = "detail_mission/{missionId}",
                        arguments = listOf(navArgument("missionId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("missionId")
                        DetailMissionScreen(navController = navController, missionId = id ?: "")
                    }

                    // === AREA FITUR CHATBOT ===
                    // Chat List - Halaman daftar chat history
                    composable("chat_list") {
                        ChatListScreen(
                            navController = navController,
                            onChatClick = {
                                // Navigasi ke halaman chat
                                navController.navigate("chat")
                            }
                        )
                    }

                    // Chat Screen - Halaman chat utama dengan bot
                    composable("chat") {
                        ChatScreen(
                            navController = navController,
                            viewModel = chatViewModel
                        )
                    }
                }
            }
        }
    }
}