package com.pb.pb_app.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pb.pb_app.utils.RepositoryImpl
import com.pb.pb_app.utils.interfaces.Repository
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.employees.GenericEmployee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(context: Context) : ViewModel() {
    companion object {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return MainViewModel(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!.applicationContext) as T
            }
        }
    }

    private val repository: Repository = RepositoryImpl(context)

    val loggedInEmployeeUsername: String?
        get() = repository.getLoggedInUserName()

    private val _loggedInEmployee = MutableStateFlow<Resource<GenericEmployee>>(Resource.Loading())
    val loggedInEmployee: StateFlow<Resource<GenericEmployee>>
        get() = _loggedInEmployee

    init {
        viewModelScope.launch {
            val username= loggedInEmployeeUsername
            if (!username.isNullOrBlank()) {
                viewModelScope.launch {
                    _loggedInEmployee.emit(repository.getEmployeeByUsername(username))
                }
            } else {
                _loggedInEmployee.emit(Resource.Failure("User Not Logged In"))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _loggedInEmployee.emit(Resource.Failure("User not logged in"))
        }
    }
}