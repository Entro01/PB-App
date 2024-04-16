package com.pb.pb_app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pb.pb_app.data.RepositoryImpl
import com.pb.pb_app.data.enums.EmployeeRole.Companion.parseEmployeeId
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

    val loginRole = repository.retrieveCredentials()?.first?.parseEmployeeId()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}