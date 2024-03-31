package com.pb.pb_app.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pb.pb_app.MainViewModel
import com.pb.pb_app.ui.FormScreen
import com.pb.pb_app.ui.HomeScreenComposables
import com.pb.pb_app.ui.LoginScreenComposables
import com.pb.pb_app.ui.PBFab
import com.pb.pb_app.ui.SplashScreenComposables
import com.pb.pb_app.ui.theme.PBAppTheme
import com.pb.pb_app.utils.NavDestinations.FORM_SCREEN
import com.pb.pb_app.utils.NavDestinations.HOME_SCREEN
import com.pb.pb_app.utils.NavDestinations.LOADING_SCREEN
import com.pb.pb_app.utils.NavDestinations.LOGIN_SCREEN
import com.pb.pb_app.utils.models.employees.Admin

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {


    private val viewModel: MainViewModel by viewModels {
        MainViewModel.factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PBAppTheme {
                val navController = rememberNavController()
                val currentDestination = navController.currentBackStackEntryAsState()
                val userResource = viewModel.userResource.collectAsState()
                Scaffold(Modifier, { PBAppBar(currentDestination.value, {}, {}) { viewModel.logout() } }, floatingActionButton = {
                    if (currentDestination.value?.destination?.route == HOME_SCREEN && userResource.value.data is Admin) PBFab {
                        navController.navigate(
                            FORM_SCREEN
                        )
                    }
                }) { paddingValues ->
                    Column {
                        Spacer(Modifier.height(paddingValues.calculateTopPadding()))
                        NavHost(navController, startDestination = LOADING_SCREEN) {
                            composable(HOME_SCREEN) {
                                HomeScreenComposables(viewModel, navController).HomeScreen()
                            }
                            composable(LOGIN_SCREEN) {
                                LoginScreenComposables(viewModel, navController).LoginScreen()
                            }
                            composable(LOADING_SCREEN) {
                                SplashScreenComposables(viewModel, navController).SplashScreen()
                            }
                            composable(FORM_SCREEN) {
                                FormScreen(viewModel, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PBAppBar(
    destination: NavBackStackEntry?, onNotificationIconClicked: () -> Unit = {}, onDrawerIconClicked: () -> Unit = {}, onLogout: () -> Unit = {}
) {

    when (destination?.destination?.route) {
        HOME_SCREEN -> {
            CenterAlignedTopAppBar(title = { Text("Home Screen") }, Modifier, {
                IconButton(onClick = onDrawerIconClicked) {
                    Icon(Icons.Default.Menu, "Sidebar Button")
                }
            }, {
                IconButton(onClick = onNotificationIconClicked) {
                    Icon(Icons.Default.Notifications, "Notifications Button")
                }
                IconButton(onClick = onLogout) {
                    Icon(Icons.AutoMirrored.Default.Logout, "Notifications Button")
                }
            })
        }

        LOGIN_SCREEN -> {
            LargeTopAppBar({ Text("Login") })
        }
    }

}
