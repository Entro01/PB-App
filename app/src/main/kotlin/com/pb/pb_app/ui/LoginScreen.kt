package com.pb.pb_app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pb.pb_app.ui.viewmodels.LoginViewModel
import com.pb.pb_app.ui.viewmodels.LoginViewModel.Companion.AuthenticationState.AUTHENTICATION_FAILURE
import com.pb.pb_app.ui.viewmodels.LoginViewModel.Companion.AuthenticationState.LOGIN_SUCCESS
import com.pb.pb_app.utils.Destination


private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModel.factory)
    val     username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val isAuthenticationError by viewModel.authenticationState.collectAsState()

    if (isAuthenticationError == LOGIN_SUCCESS) {
        navController.navigate(Destination.HOME_SCREEN.route)
    }

    val modifier = Modifier.fillMaxSize()

    Box(modifier) {
        Column(
            modifier,
            Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            Alignment.CenterHorizontally,

            ) {
            val shouldShowPassword by viewModel.shouldShowPassword.collectAsState()

            CredentialsField(
                username,
                { viewModel.updateUserCredentials(username = it) },
                "Username",
                PBUsernameVisualTransformation(),
                pbPasswordKeyboardOptions,
                pbUsernameKeyboardActions,
            )

            CredentialsField(
                password,
                { viewModel.updateUserCredentials(password = it) },
                "Password",
                PBPasswordVisualTransformation(shouldShowPassword),
                pbPasswordKeyboardOptions,
                pbPasswordKeyboardActions,
                { viewModel.toggleState() },
                isAuthenticationError.isError, if (isAuthenticationError == AUTHENTICATION_FAILURE) "Incorrect Credentials" else ""
            )


            LoginButton(!(username.isEmpty() || password.isEmpty())) {
                viewModel.authenticate()
            }


            ForgotPasswordTextButton({})
        }

        if (isAuthenticationError.isError == true) {
            LoginFailureSnackbar()
        }
    }
}

@Composable
fun ForgotPasswordTextButton(onClick: () -> Unit) {
    TextButton(onClick) {
        Text(text = "Forgot password?")
    }
}
