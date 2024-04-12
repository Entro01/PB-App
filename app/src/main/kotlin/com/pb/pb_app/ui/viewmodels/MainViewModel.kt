package com.pb.pb_app.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pb.pb_app.utils.RepositoryImpl
import com.pb.pb_app.utils.models.employees.EmployeeRole.Companion.fromEmployeeId
import kotlinx.coroutines.launch

class MainViewModel(context: Context) : ViewModel() {
    companion object {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return MainViewModel(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!.applicationContext) as T
            }
        }
    }

    private val repository = RepositoryImpl(context)

    val loginRole = repository.retrieveCredentials()?.elementAt(0)?.fromEmployeeId()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}