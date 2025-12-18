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
// (Karena file-nya sudah kamu pindah ke folder ih.pam, package-nya ikut berubah)
import ih.pam.pamobile_jelahjahmalang.screen.FeedScreen
import ih.pam.pamobile_jelahjahmalang.screen.DetailScreen
import ih.pam.pamobile_jelahjahmalang.utils.PlaceRepository
import ih.pam.pamobile_jelahjahmalang.network.RetrofitInstance
import ih.pam.pamobile_jelahjahmalang.viewmodel.PlaceViewModel
// Pastikan file Factory ini package-nya sudah benar (cek file aslinya)
import pg.mobile.projectpampricil.ui.PlaceViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. SETUP LOGIKA PRICIL (Repository)
        // Disiapkan di sini supaya data wisata siap dimuat
        val repository = PlaceRepository(RetrofitInstance.apiService)

        setContent {
            PAMobileJelahjahMalangTheme {

                // 2. SETUP VIEWMODEL (Pricil)
                // ViewModel ini akan dipakai di FeedScreen & DetailScreen
                val placeViewModel: PlaceViewModel = viewModel(
                    factory = PlaceViewModelFactory(repository)
                )

                val navController = rememberNavController()

                // 3. NAVIGASI UTAMA
                // Start Destination kita set ke 'login' dulu
                NavHost(navController = navController, startDestination = "login") {

                    // === AREA LOGIN & REGISTER (Isham) ===
                    composable("login") {
                        // Nanti di LoginScreen, tombol login harus: navController.navigate("feed")
                        LoginScreen(navController = navController)
                    }

                    composable("register") {
                        RegisterScreen(navController = navController)
                    }

                    // === AREA FITUR WISATA (Pricil) ===
                    composable("feed") {
                        // Ini halaman List Wisata
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
                    // Bisa diakses lewat tombol menu nanti
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
                }
            }
        }
    }
}