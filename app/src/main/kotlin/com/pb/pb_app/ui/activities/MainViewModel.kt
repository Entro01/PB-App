package com.pb.pb_app

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pb.pb_app.utils.RepositoryImpl
import com.pb.pb_app.utils.interfaces.Repository
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.employees.Admin
import com.pb.pb_app.utils.models.employees.Coordinator
import com.pb.pb_app.utils.models.employees.Freelancer
import com.pb.pb_app.utils.models.employees.GenericEmployee
import com.pb.pb_app.utils.models.projects.Enquiry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"

class MainViewModel(context: Context) : ViewModel() {

    companion object {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return MainViewModel(extras[APPLICATION_KEY]!!.applicationContext) as T
            }
        }
    }

    private val repository: Repository = RepositoryImpl(context)
    private val _userResource: MutableStateFlow<Resource<GenericEmployee>> = MutableStateFlow(Resource.Loading())
    val userResource: StateFlow<Resource<GenericEmployee>>
        get() = _userResource

    private val _enquiries = MutableStateFlow<List<Enquiry>>(emptyList())
    val enquiries: StateFlow<List<Enquiry>>
        get() = _enquiries

    private val _employees = MutableStateFlow<List<GenericEmployee>>(emptyList())
    val employees: StateFlow<List<GenericEmployee>>
        get() = _employees


    init {
        viewModelScope.launch {
            val username = repository.getLoggedInUser()
            if (username == null) {
                _userResource.emit(Resource.Failure("THIS WAS CALLED"))
            } else {
                _userResource.emit(repository.getEmployeeByUsername(username))
            }
        }

        viewModelScope.launch {
            _userResource.collect { employeeResource ->
                employeeResource.data?.getEnquiries()?.let { enquiries -> _enquiries.emit(enquiries.data) }
            }
        }

        viewModelScope.launch {
            _userResource.collect { employeeResource ->
                when (employeeResource.data) {
                    is Admin -> {
                        _employees.emit((employeeResource.data as Admin).getCoordinators().data + (employeeResource.data as Admin).getFreelancers().data)
                    }

                    is Freelancer -> {}

                    is Coordinator -> {
                        _employees.emit((employeeResource.data as Coordinator).getFreelancers().data)
                    }

                    null -> {

                    }
                }
            }
        }
    }

    fun acceptEnquiry(enquiry: Enquiry, coordinatorUsername: String) {
        if (_userResource.value.data !is Admin) return
        viewModelScope.launch {
            (_userResource.value.data as Admin).acceptProjectByID(enquiry.id, coordinatorUsername)
        }
    }

    fun acceptAndAssignPC(enquiry: Enquiry, freelancerUsername: String) {
        if (_userResource.value.data !is Coordinator) return

        viewModelScope.launch {
            (_userResource.value.data as Coordinator).acceptProjectByID(enquiry.id, freelancerUsername)
        }
    }

    fun acceptEnquiryAsFR(enquiry: Enquiry) {
        if (_userResource.value.data !is Freelancer) return

        viewModelScope.launch {
            (_userResource.value.data as Freelancer).acceptProject(enquiry.id)
        }
    }

    fun createNewEnquiry(name: String, description: String) {

        viewModelScope.launch {
            repository.createNewEnquiry(name, description)
        }
    }

    fun setOnlineStatus(status: Boolean) {
        viewModelScope.launch {
            _userResource.value.data?.setOnlineStatus(status)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _userResource.emit(Resource.Failure("THIS WAS NOT CALLED"))
        }
    }

    fun authenticate(username: String, password: String, onAuthenticationFinish: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = repository.authenticateUser(username, password)
            if (response) {
                if (repository.getLoggedInUser() == null) {
                    throw IllegalStateException("how did u manage to log in without saving the user's username?")
                }
                _userResource.emit(repository.getEmployeeByUsername(username))
            }
            onAuthenticationFinish(response)
        }
    }
}