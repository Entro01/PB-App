package com.pb.pb_app.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pb.pb_app.MainViewModel
import com.pb.pb_app.utils.NavDestinations.HOME_SCREEN

private const val TAG = "LoginScreen"

class LoginScreenComposables(private val viewModel: MainViewModel, private val navController: NavController) {
    @Composable
    fun LoginScreen() {
        var passwordToggleState by remember { mutableStateOf(false) }
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isAuthenticationError: Boolean? by remember { mutableStateOf(null) }
        val modifier = Modifier.fillMaxSize()

        Box(modifier) {
            Column(
                modifier,
                Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                Alignment.CenterHorizontally,

                ) {

                CredentialsField({ username = it })
                CredentialsField(
                    { password = it },
                    "Password",
                    PBPasswordVisualTransformation(passwordToggleState),
                    pbPasswordKeyboardOptions,
                    pbPasswordKeyboardActions,
                    { passwordToggleState = !passwordToggleState },
                    isAuthenticationError,
                )

                LoginButton {
                    viewModel.authenticate(username, password) {
                        Log.e(TAG, "LoginScreen: CALLED ONCE")
                        isAuthenticationError = !it
                        if (it) {
                            navController.popBackStack()
                            navController.navigate(HOME_SCREEN)
                        }
                    }
                }

                ForgotPasswordTextButton()
            }

            if (isAuthenticationError == true) {
                LoginFailureSnackbar()
            }
        }
    }


}