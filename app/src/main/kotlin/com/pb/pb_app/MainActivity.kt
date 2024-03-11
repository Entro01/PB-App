package com.pb.pb_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pb.pb_app.ui.HomeScreen
import com.pb.pb_app.ui.LoginScreen
import com.pb.pb_app.ui.theme.PBAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PBAppTheme {
                val navController = rememberNavController()
                val navDestination = viewModel.navDestination.collectAsState()
                LaunchedEffect(Unit) {
                    viewModel.navDestination.collect {
                        navController.navigate(it)
                    }
                }

                Scaffold(topBar = { PBAppBar(navDestination = navDestination.value) }) {
                    Column {
                        Spacer(Modifier.height(it.calculateTopPadding()))
                        NavHost(navController, NavDestinations.LOGIN_SCREEN) {
                            composable(NavDestinations.LOGIN_SCREEN) {
                                LoginScreen(onSignUp = { username, password ->
                                    lifecycleScope.launch {
                                        viewModel.signUp(
                                            username, password
                                        )
                                    }
                                })
                            }

                            composable(NavDestinations.HOME_SCREEN) {
                                HomeScreen {

                                }
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
    navDestination: String,
    onNotificationIconClicked: () -> Unit = {},
    onDrawerIconClicked: () -> Unit = {}
) {
    when (navDestination) {
        NavDestinations.HOME_SCREEN -> {
            CenterAlignedTopAppBar(title = { Text("Home Screen") }, Modifier, {
                IconButton(onClick = onDrawerIconClicked) {
                    Icon(
                        Icons.Default.Menu, "Sidebar Button"
                    )
                }
            }, {
                IconButton(
                    onClick = onNotificationIconClicked
                ) {
                    Icon(
                        Icons.Default.Notifications, contentDescription = "Notifications Button"
                    )
                }
            })
        }

        NavDestinations.LOGIN_SCREEN -> {
            LargeTopAppBar({ Text("Login") })
        }
    }
}
