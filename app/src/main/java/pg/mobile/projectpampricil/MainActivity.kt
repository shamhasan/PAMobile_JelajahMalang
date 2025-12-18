package pg.mobile.projectpampricil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pg.mobile.projectpampricil.data.PlaceRepository
import pg.mobile.projectpampricil.data.RetrofitInstance
import pg.mobile.projectpampricil.ui.DetailScreen
import pg.mobile.projectpampricil.ui.FeedScreen
import pg.mobile.projectpampricil.ui.PlaceViewModel
import pg.mobile.projectpampricil.ui.PlaceViewModelFactory
import pg.mobile.projectpampricil.ui.theme.ProjectPAMPricilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ Menyuntikkan PlaceRepository dengan RetrofitInstance.apiService
        val repository = PlaceRepository(RetrofitInstance.apiService)

        setContent {
            ProjectPAMPricilTheme {
                // Menyuntikkan ViewModel dengan repository yang sudah ada
                val placeViewModel: PlaceViewModel = viewModel(
                    factory = PlaceViewModelFactory(repository)
                )
                AppRoot(placeViewModel)
            }
        }
    }
}

@Composable
fun AppRoot(placeViewModel: PlaceViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "feed") {
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
    }
}
