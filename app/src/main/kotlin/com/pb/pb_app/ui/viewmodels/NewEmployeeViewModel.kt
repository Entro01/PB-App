package com.pb.pb_app.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pb.pb_app.utils.RepositoryImpl
import com.pb.pb_app.utils.models.employees.EmployeeRole
import com.pb.pb_app.utils.models.employees.NewEmployee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NewEmployeeViewModel(context: Context) : ViewModel() {
    companion object {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return NewEmployeeViewModel(extras[APPLICATION_KEY]!!.applicationContext) as T
            }
        }
    }

    val shouldEnableSaveButton = MutableStateFlow(false)

    private val newEmployee = MutableStateFlow(NewEmployee("", "", "", "", false, EmployeeRole.FREELANCER))

    private val repository = RepositoryImpl(context)

    init {
        viewModelScope.launch {
            newEmployee.collect {
                shouldEnableSaveButton.emit(
                    it.employeeId.isNotEmpty() && it.name.isNotEmpty() && it.emailAddress.isNotEmpty() && it.contactNumber.isNotEmpty()
                )
            }
        }
    }

    fun createNewEmployee() {
        viewModelScope.launch {
            repository.createNewEmployee(newEmployee.value)
        }
    }

    fun updateNewEmployee(
        employeeId: String = newEmployee.value.employeeId,
        name: String = newEmployee.value.name,
        emailAddress: String = newEmployee.value.emailAddress,
        contactNumber: String = newEmployee.value.contactNumber,
        role: EmployeeRole = newEmployee.value.role
    ) {
        viewModelScope.launch {
            newEmployee.emit(NewEmployee(employeeId, name, emailAddress, contactNumber, false, role))
        }
    }

}