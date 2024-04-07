package com.pb.pb_app.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pb.pb_app.ui.HomeScreen
import com.pb.pb_app.ui.LoginScreen
import com.pb.pb_app.ui.theme.PBAppTheme
import com.pb.pb_app.ui.viewmodels.MainViewModel
import com.pb.pb_app.utils.Destination
import com.pb.pb_app.utils.Destination.HOME_SCREEN
import com.pb.pb_app.utils.Destination.LOGIN_SCREEN
import com.pb.pb_app.utils.Destination.NEW_EMPLOYEE_SCREEN
import com.pb.pb_app.utils.Destination.NEW_INQUIRY_SCREEN
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
                val currentDestination = Destination.getDestinationByRoute(navController.currentBackStackEntryAsState().value?.destination?.route ?: "Home")
                val startDestination = remember { if (viewModel.loggedInEmployeeUsername.isNullOrBlank()) LOGIN_SCREEN else HOME_SCREEN }
                val userResource by viewModel.loggedInEmployee.collectAsState()
                val topBar = @Composable {
                    PBAppBar(currentDestination, { }, { }, { viewModel.logout() })
                }
                val floatingActionButton = @Composable {
                    if (userResource.data is Admin)
                        ExtendedFloatingActionButton(text = { Text(text = "New Inquiry") },
                            icon = { Icon(Icons.Default.Add, contentDescription = "Add Inquiry Button") },
                            onClick = { navController.navigate(NEW_INQUIRY_SCREEN.route) })
                }

                Scaffold(Modifier, topBar = topBar, floatingActionButton = floatingActionButton) { paddingValues ->
                    NavHost(navController, startDestination = startDestination.route, modifier = Modifier.padding(paddingValues)) {
                        composable(LOGIN_SCREEN.route) {
                            LoginScreen(navController)
                        }
                        composable(HOME_SCREEN.route) {
                            HomeScreen(userResource, navController)
                        }
                        composable(NEW_INQUIRY_SCREEN.route) {
                            //NewEnquiryScreen()
                        }
                        composable(NEW_EMPLOYEE_SCREEN.route) {
                            //NewEmployeeScreen()
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
    destination: Destination,
    onDrawerIconClicked: () -> Unit,
    onNotificationIconClicked: () -> Unit,
    onLogout: () -> Unit,
) {


    when (destination) {
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
            LargeTopAppBar({ Text("Login") }, actions = {
                IconButton(onClick = { }) {

                }
            })
        }

        else -> {

        }
    }
}
