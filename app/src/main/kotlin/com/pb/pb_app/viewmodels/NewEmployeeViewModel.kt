package com.pb.pb_app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pb.pb_app.data.RepositoryImpl
import com.pb.pb_app.data.enums.EmployeeRole
import com.pb.pb_app.data.models.employees.NewEmployee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _newEmployee = MutableStateFlow(NewEmployee("", "", "", "", false, EmployeeRole.FREELANCER))
    val newEmployee get() = _newEmployee as StateFlow<NewEmployee>

    private val repository = RepositoryImpl(context)

    init {
        viewModelScope.launch {
            _newEmployee.collect {
                shouldEnableSaveButton.emit(
                    it.employeeId.isNotEmpty() && it.name.isNotEmpty() && it.emailAddress.isNotEmpty() && it.contactNumber.isNotEmpty()
                )
            }
        }
    }

    fun createNewEmployee() {
        viewModelScope.launch {
            repository.createNewEmployee(_newEmployee.value)
        }
    }

    fun updateNewEmployee(
        employeeId: String = _newEmployee.value.employeeId,
        name: String = _newEmployee.value.name,
        emailAddress: String = _newEmployee.value.emailAddress,
        contactNumber: String = _newEmployee.value.contactNumber,
        role: EmployeeRole = _newEmployee.value.role
    ) {
        viewModelScope.launch {
            _newEmployee.emit(NewEmployee(employeeId, name, emailAddress, contactNumber, false, role))
        }
    }

}