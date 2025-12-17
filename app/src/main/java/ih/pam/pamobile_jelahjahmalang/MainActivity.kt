package ih.pam.pamobile_jelahjahmalang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ih.pam.pamobile_jelahjahmalang.screen.DetailMissionScreen
import ih.pam.pamobile_jelahjahmalang.screen.LoginScreen
import ih.pam.pamobile_jelahjahmalang.screen.MissionScreen
import ih.pam.pamobile_jelahjahmalang.screen.RegisterScreen
import ih.pam.pamobile_jelahjahmalang.ui.theme.PAMobileJelahjahMalangTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PAMobileJelahjahMalangTheme {


                val navController = rememberNavController()


                NavHost(navController = navController, startDestination = "login") {

                    composable("mission_list") {
                        MissionScreen(navController = navController)
                    }

                    composable(route = "login") {
                        LoginScreen(navController = navController)
                    }


                    composable(route = "register") {
                        RegisterScreen(navController = navController)
                    }


                    composable(
                        route = "detail_mission/{missionId}", // Tambahkan /{missionId}
                        arguments = listOf(
                            navArgument("missionId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        // Ambil ID dari URL navigasi
                        val id = backStackEntry.arguments?.getString("missionId")

                        // Kirim ID ke DetailScreen
                        DetailMissionScreen(navController = navController, missionId = id ?: "")
                    }
                }
            }
        }
    }
}