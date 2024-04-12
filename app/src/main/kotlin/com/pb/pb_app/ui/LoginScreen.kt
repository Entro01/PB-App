package com.pb.pb_app.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pb.pb_app.ui.viewmodels.LoginViewModel
import com.pb.pb_app.utils.models.AuthenticationState
import com.pb.pb_app.utils.models.Destination
import com.pb.pb_app.utils.models.employees.EmployeeRole
import com.pb.pb_app.utils.models.employees.EmployeeRole.Companion.fromEmployeeId
import com.pb.pb_app.utils.reusables.PBPasswordVisualTransformation
import com.pb.pb_app.utils.reusables.PBUsernameVisualTransformation
import com.pb.pb_app.utils.reusables.pbPasswordKeyboardActions
import com.pb.pb_app.utils.reusables.pbPasswordKeyboardOptions
import com.pb.pb_app.utils.reusables.pbUsernameKeyboardActions
import com.pb.pb_app.utils.reusables.pbUsernameKeyboardOptions


private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModel.factory)
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val authenticationState by viewModel.authenticationState.collectAsState()

    if (authenticationState == AuthenticationState.LOGIN_SUCCESS) {
        when (username.fromEmployeeId()) {
            EmployeeRole.ADMIN -> navController.navigate(Destination.ADMIN_SCREEN.route)
            EmployeeRole.COORDINATOR -> navController.navigate(Destination.COORDINATOR_SCREEN.route)
            EmployeeRole.FREELANCER -> navController.navigate(Destination.FREELANCER_SCREEN.route)
        }
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
                authenticationState.isError, if (authenticationState == AuthenticationState.AUTHENTICATION_FAILURE) "Incorrect Credentials" else ""
            )


            LoginButton(!(username.isEmpty() || password.isEmpty())) {
                viewModel.authenticate()
            }


            ForgotPasswordTextButton({})
        }

        if (authenticationState.isError == true) {
            Toast.makeText(LocalContext.current, "Login credentials are invalid", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun ForgotPasswordTextButton(onClick: () -> Unit) {
    TextButton(onClick) {
        Text(text = "Forgot password?")
    }
}

@Composable
fun LoginButton(isEnabled: Boolean, onClick: () -> Unit) {
    Button(
        modifier = Modifier.widthIn(TextFieldDefaults.MinWidth, TextFieldDefaults.MinWidth), onClick = onClick, enabled = isEnabled
    ) {
        Text(text = "Log in", modifier = Modifier)
    }
}


@Composable
fun CredentialsField(
    text: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Username",
    visualTransformation: VisualTransformation = PBUsernameVisualTransformation(),
    keyboardOptions: KeyboardOptions = pbUsernameKeyboardOptions,
    keyboardActions: KeyboardActions = pbUsernameKeyboardActions,
    onShowPasswordToggle: (() -> Unit)? = null,
    isError: Boolean? = null,
    supportingText: String = ""
) {

    TextField(
        value = text,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.widthIn(TextFieldDefaults.MinWidth, TextFieldDefaults.MinWidth),
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        placeholder = { Text(placeholder) },
        supportingText = { if (supportingText.isNotEmpty()) Text(supportingText) },
        label = { Text(placeholder) },
        trailingIcon = {
            if (onShowPasswordToggle != null) ShowPasswordIconToggle(onShowPasswordToggle)
        },
        isError = isError ?: false
    )
}

@Composable
fun ShowPasswordIconToggle(onClick: () -> Unit) {
    var toggle by remember { mutableStateOf(false) }

    IconButton({
        onClick()
        toggle = !toggle
    }) {
        Icon(if (toggle) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Show password toggle")
    }
}