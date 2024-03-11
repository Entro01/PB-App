package com.pb.pb_app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val _navDestination = MutableStateFlow(NavDestinations.LOGIN_SCREEN)
    val navDestination: StateFlow<String>
        get() = _navDestination

    suspend fun signUp(username: String, password: String) {
        return _navDestination.emit(NavDestinations.HOME_SCREEN)

    }
}