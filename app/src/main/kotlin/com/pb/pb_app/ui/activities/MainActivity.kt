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
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pb.pb_app.ui.LoginScreen
import com.pb.pb_app.ui.NewEmployeeScreen
import com.pb.pb_app.ui.NewInquiryScreen
import com.pb.pb_app.ui.theme.PBAppTheme
import com.pb.pb_app.viewmodels.MainViewModel
import com.pb.pb_app.ui.enums.Destination
import com.pb.pb_app.ui.enums.Destination.ADMIN_SCREEN
import com.pb.pb_app.ui.enums.Destination.COORDINATOR_SCREEN
import com.pb.pb_app.ui.enums.Destination.Companion.valueOf
import com.pb.pb_app.ui.enums.Destination.FREELANCER_SCREEN
import com.pb.pb_app.ui.enums.Destination.LOGIN_SCREEN
import com.pb.pb_app.ui.enums.Destination.NEW_EMPLOYEE_SCREEN
import com.pb.pb_app.ui.enums.Destination.NEW_INQUIRY_SCREEN
import com.pb.pb_app.data.enums.EmployeeRole.Companion.screen
import com.pb.pb_app.ui.enums.Destination.Companion.fromRoute
import com.pb.pb_app.ui.screens.AdminScreen
import com.pb.pb_app.ui.screens.CoordinatorScreen
import com.pb.pb_app.ui.screens.FreelancerScreen

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
                val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route?.fromRoute() ?: "Login".fromRoute()
                val startDestination = if (viewModel.loginRole == null) LOGIN_SCREEN else viewModel.loginRole!!.screen
                val topBar = @Composable {
                    PBAppBar(currentDestination, { }, { }, {
                        viewModel.logout()
                        navController.popBackStack()
                        navController.navigate(LOGIN_SCREEN.route)
                    })
                }
                val floatingActionButton = @Composable {
                    when (currentDestination) {
                        ADMIN_SCREEN -> {
                            ExtendedFloatingActionButton(text = { Text(text = "New Inquiry") },
                                icon = { Icon(Icons.Default.Add, contentDescription = "Add Inquiry Button") },
                                onClick = { navController.navigate(NEW_INQUIRY_SCREEN.route) })
                        }

                        else -> {}
                    }

                }

                Scaffold(Modifier, topBar = topBar, floatingActionButton = floatingActionButton) { paddingValues ->
                    NavHost(navController, startDestination = startDestination.route, modifier = Modifier.padding(paddingValues)) {
                        composable(LOGIN_SCREEN.route) {
                            LoginScreen(navController)
                        }
                        composable(ADMIN_SCREEN.route) { AdminScreen(navController) }
                        composable(COORDINATOR_SCREEN.route) { CoordinatorScreen(navController) }
                        composable(FREELANCER_SCREEN.route) { FreelancerScreen(navController) }
                        composable(NEW_INQUIRY_SCREEN.route) { NewInquiryScreen(navController) }
                        composable(NEW_EMPLOYEE_SCREEN.route) { NewEmployeeScreen(navController)  }
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
        LOGIN_SCREEN -> {
            LargeTopAppBar({ Text("Login") }, actions = {
            })
        }

        else -> {
            if (destination == ADMIN_SCREEN || destination == FREELANCER_SCREEN || destination == COORDINATOR_SCREEN) {
                CenterAlignedTopAppBar(title = { Text("Project Banao") }, Modifier, {
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
        }
    }
}
