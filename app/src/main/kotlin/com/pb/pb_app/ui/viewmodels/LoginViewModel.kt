package com.pb.pb_app.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pb.pb_app.utils.RepositoryImpl
import com.pb.pb_app.utils.models.AuthenticationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(context: Context) : ViewModel() {

    companion object {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return LoginViewModel(extras[APPLICATION_KEY]!!.applicationContext) as T
            }
        }
    }

    private val repository = RepositoryImpl(context)

    private val _shouldShowPassword = MutableStateFlow(false)
    val shouldShowPassword get() = _shouldShowPassword as StateFlow<Boolean>
    private val _username = MutableStateFlow("")
    val username get() = _username as StateFlow<String>
    private val _password = MutableStateFlow("")
    val password get() = _password as StateFlow<String>

    private val _authenticationState = MutableStateFlow(AuthenticationState.LOGIN_UNDONE)
    val authenticationState get() = _authenticationState as StateFlow<AuthenticationState>

    fun authenticate() {
        viewModelScope.launch {
            if (repository.authenticateUser(_username.value, _password.value)) {
                _authenticationState.emit(AuthenticationState.LOGIN_SUCCESS)
            } else {
                _authenticationState.emit(AuthenticationState.AUTHENTICATION_FAILURE)
            }
        }
    }

    fun updateUserCredentials(username: String = _username.value, password: String = _password.value) {
        viewModelScope.launch {
            _username.emit(username)
            _password.emit(password)
        }
    }

    fun toggleState() {
        viewModelScope.launch {
            _shouldShowPassword.emit(!_shouldShowPassword.value)
        }
    }
}