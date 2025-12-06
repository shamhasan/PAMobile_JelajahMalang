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
import ih.pam.pamobile_jelahjahmalang.screen.MissionScreen
import ih.pam.pamobile_jelahjahmalang.ui.theme.PAMobileJelahjahMalangTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PAMobileJelahjahMalangTheme {

                val navController = rememberNavController()


                NavHost(navController = navController, startDestination = "mission_list") {

                    composable("mission_list") {
                        MissionScreen(navController = navController)
                    }

                    composable(
                        "detail_mission/{placeId}/{title}",
                        arguments = listOf(
                            navArgument("placeId") { type = NavType.StringType },
                            navArgument("title") { type = NavType.StringType }

                        )) { backStackEntry ->
                        // Mengambil data ID yang dikirim
                        val placeId = backStackEntry.arguments?.getString("placeId") ?: ""
                        val title = backStackEntry.arguments?.getString("title") ?: ""

                        // Memanggil Screen tujuan dan mengirimkan ID
                        DetailMissionScreen(
                            navController = navController,
                            placeId = placeId,
                            missionTitle = title
                        )
                    }
                }
            }
        }
    }
}