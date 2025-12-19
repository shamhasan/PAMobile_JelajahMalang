package ih.pam.pamobile_jelahjahmalang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ih.pam.pamobile_jelahjahmalang.network.ApiServicePricil
import ih.pam.pamobile_jelahjahmalang.screen.*
import ih.pam.pamobile_jelahjahmalang.ui.theme.PAMobileJelahjahMalangTheme
import ih.pam.pamobile_jelahjahmalang.utils.PlaceRepository
import ih.pam.pamobile_jelahjahmalang.viewmodel.ChatViewModel
import ih.pam.pamobile_jelahjahmalang.viewmodel.PlaceViewModel
import ih.pam.pamobile_jelahjahmalang.viewmodel.UserViewmodel
import pg.mobile.projectpampricil.ui.PlaceViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PAMobileJelahjahMalangTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val chatViewModel: ChatViewModel = viewModel()
    val userViewModel: UserViewmodel = viewModel()

    // Setup untuk PlaceViewModel
    val retrofit = Retrofit.Builder()
        .baseUrl("https://pam-jelajah-malang-default-rtdb.firebaseio.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiServicePricil::class.java)
    val repository = PlaceRepository(apiService)
    val placeViewModel: PlaceViewModel = viewModel(
        factory = PlaceViewModelFactory(repository)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Daftar route yang menampilkan bottom bar
    val bottomBarRoutes = listOf("feed", "profile")
    val showBottomBar = currentDestination?.route in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Feed") },
                        label = { Text("Feed") },
                        selected = currentDestination?.hierarchy?.any { it.route == "feed" } == true,
                        onClick = {
                            navController.navigate("feed") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
                        onClick = {
                            navController.navigate("profile") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(padding)
        ) {
            // Auth screens
            composable("login") {
                LoginScreen(navController = navController, viewmodel = userViewModel)
            }
            composable("register") {
                RegisterScreen(navController = navController, viewmodel = userViewModel)
            }

            // Main screens
            composable("feed") {
                FeedScreen(navController = navController, vm = placeViewModel)
            }
            composable("profile") {
                // ProfileScreen(navController = navController)
                Text("Profile Screen - Coming Soon")
            }

            // Detail screens
            composable("detail/{placeId}") { backStackEntry ->
                val placeId = backStackEntry.arguments?.getString("placeId") ?: ""
                DetailScreen(
                    placeId = placeId,
                    navController = navController,
                    vm = placeViewModel
                )
            }

            // Mission screens
            composable("mission_list") {
                MissionScreen(navController = navController)
            }
            composable("detail_mission/{missionId}") { backStackEntry ->
                val missionId = backStackEntry.arguments?.getString("missionId") ?: ""
                DetailMissionScreen(
                    navController = navController,
                    missionId = missionId
                )
            }

            // Chat screens
            composable("chat_list") {
                ChatListScreen(
                    navController = navController,
                    onChatClick = {
                        navController.navigate("chat")
                    }
                )
            }
            composable("chat") {
                ChatScreen(
                    navController = navController,
                    viewModel = chatViewModel
                )
            }
        }
    }
}