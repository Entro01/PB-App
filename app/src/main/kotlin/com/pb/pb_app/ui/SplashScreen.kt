package com.pb.pb_app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.pb.pb_app.MainViewModel
import com.pb.pb_app.R
import com.pb.pb_app.utils.NavDestinations.HOME_SCREEN
import com.pb.pb_app.utils.NavDestinations.LOGIN_SCREEN
import com.pb.pb_app.utils.models.Resource

class SplashScreenComposables(private val viewModel: MainViewModel, private val navController: NavController) {
    @Composable
    fun SplashScreen() {
        val userResource by viewModel.userResource.collectAsState()

        when (userResource) {
            is Resource.Success -> {
                navController.popBackStack()
                navController.navigate(HOME_SCREEN)
            }

            is Resource.Failure -> {
                navController.popBackStack()
                navController.navigate(LOGIN_SCREEN)
            }

            else -> SplashScreenAnimation()
        }
    }

    @Composable
    private fun SplashScreenAnimation() {
        Surface(Modifier.fillMaxSize()) {
            val painter = painterResource(id = R.drawable.ic_launcher_foreground)
            Icon(painter, "splash screen", tint = MaterialTheme.colorScheme.primary)
        }
    }

}