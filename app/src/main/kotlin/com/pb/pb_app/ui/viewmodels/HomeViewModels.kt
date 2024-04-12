package com.pb.pb_app.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.pb.pb_app.utils.RepositoryImpl
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.employees.Admin
import com.pb.pb_app.utils.models.employees.BaseEmployee
import com.pb.pb_app.utils.models.employees.Coordinator
import com.pb.pb_app.utils.models.employees.EmployeeRole
import com.pb.pb_app.utils.models.employees.Freelancer
import com.pb.pb_app.utils.models.projects.Inquiry
import com.pb.pb_app.utils.models.projects.InquiryUpdateAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


abstract class HomeViewModel
internal constructor() : ViewModel() {
    abstract val repository: RepositoryImpl

    companion object {
        fun factory(role: EmployeeRole) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val appContext = extras[APPLICATION_KEY]?.applicationContext ?: throw IllegalStateException("no app running")
                return when (role) {
                    EmployeeRole.ADMIN -> AdminViewModel(appContext)
                    EmployeeRole.COORDINATOR -> CoordinatorViewModel(appContext)
                    EmployeeRole.FREELANCER -> FreelancerViewModel(appContext)
                } as T
            }
        }
    }
}

class AdminViewModel
internal constructor(context: Context) : HomeViewModel() {
    override val repository = RepositoryImpl(context)
    val self: MutableStateFlow<Resource<Admin>> = MutableStateFlow(Resource.Loading())

    var freelancers: MutableStateFlow<Resource<List<Freelancer>>> = MutableStateFlow(Resource.Loading())
    var coordinators: MutableStateFlow<Resource<List<Coordinator>>> = MutableStateFlow(Resource.Loading())
    var urgentInquiries: MutableStateFlow<Resource<List<Inquiry>>> = MutableStateFlow(Resource.Loading())
    var miscInquiries: MutableStateFlow<Resource<List<Inquiry>>> = MutableStateFlow(Resource.Loading())
    var selectedCoordinators = mutableListOf<String>()

    init {
        with(viewModelScope) {
            launch {
                self.emit(repository.getSelf() as Resource<Admin>)
            }
            launch {
                freelancers.emit(repository.getFreelancers())
            }

            launch {
                coordinators.emit(repository.getCoordinators())
            }

            launch {
                urgentInquiries.emit(repository.getUrgentInquiries())
            }

            launch {
                miscInquiries.emit(repository.getMiscInquiries())
            }
        }
    }

    fun appendCoordinator(coordinatorId: String) {
        selectedCoordinators.add(coordinatorId)
    }

    fun assignCoordinator(inquiryId: Int, countDownMillis: Long) {
        viewModelScope.launch {
            for (coordinatorId in selectedCoordinators) {
                val action = InquiryUpdateAction.RequestCoordinatorAsAdmin(self.value.data!!.employeeId, coordinatorId, inquiryId, countDownMillis)

                repository.updateInquiryStatus(action)
            }
        }
    }

    fun deleteInquiry(inquiryId: Int) {
        val action = InquiryUpdateAction.DeleteInquiryAsAdmin(self.value.data!!.employeeId, inquiryId)
        viewModelScope.launch {
            repository.updateInquiryStatus(action)
        }
    }
}

class CoordinatorViewModel
internal constructor(context: Context) : HomeViewModel() {
    override val repository = RepositoryImpl(context)
    val self: MutableStateFlow<Resource<Coordinator>> = MutableStateFlow(Resource.Loading())

    var freelancers: MutableStateFlow<Resource<List<Freelancer>>> = MutableStateFlow(Resource.Loading())
    var urgentInquiries: MutableStateFlow<Resource<List<Inquiry>>> = MutableStateFlow(Resource.Loading())
    var miscInquiries: MutableStateFlow<Resource<List<Inquiry>>> = MutableStateFlow(Resource.Loading())
    var assignedFreelancer = ""


    init {
        with(viewModelScope) {
            launch {
                self.emit(repository.getSelf() as Resource<Coordinator>)
            }
            launch {
                freelancers.emit(repository.getFreelancers())
            }

            launch {
                urgentInquiries.emit(repository.getUrgentInquiries())
            }

            launch {
                miscInquiries.emit(repository.getMiscInquiries())
            }
        }
    }

    fun setSelectedEmployee(freelancerId: String) {
        assignedFreelancer = freelancerId
    }

    fun acceptUrgentInquiry(inquiryId: Int) {
        val inquiryUpdateAction = InquiryUpdateAction.AssignFreelancerAsCoordinator(self.value.data!!.employeeId, assignedFreelancer, inquiryId)
        viewModelScope.launch {
            repository.updateInquiryStatus(inquiryUpdateAction)
        }
    }

    fun rejectUrgentInquiry(inquiryId: Int) {
        val inquiryUpdateAction = InquiryUpdateAction.RejectInquiryAsCoordinator(self.value.data!!.employeeId, inquiryId)
        viewModelScope.launch {
            repository.updateInquiryStatus(inquiryUpdateAction)
        }
    }

    fun setOnlineStatus(status: Boolean) {
        viewModelScope.launch {
            repository.setEmployeeStatus(self.value.data!!.employeeId, status)
        }
    }
}

class FreelancerViewModel
internal constructor(context: Context) : HomeViewModel() {
    override val repository = RepositoryImpl(context)
    val self: MutableStateFlow<Resource<Freelancer>> = MutableStateFlow(Resource.Loading())

    val urgentInquiries: MutableStateFlow<Resource<List<Inquiry>>> = MutableStateFlow(Resource.Loading())
    val user = MutableStateFlow<Resource<BaseEmployee>>(Resource.Loading())

    init {
        with(viewModelScope) {
            launch {
                self.emit(repository.getSelf() as Resource<Freelancer>)
            }
            launch {
                user.emit(repository.getSelf())
                urgentInquiries.emit(repository.getUrgentInquiries())
            }
        }
    }

    fun acceptUrgentInquiry(inquiryId: Int) {
        val inquiryUpdateAction = InquiryUpdateAction.AcceptInquiryAsFreelancer(self.value.data!!.employeeId, inquiryId)
        viewModelScope.launch {
            repository.updateInquiryStatus(inquiryUpdateAction)
        }
    }

    fun rejectUrgentInquiry(inquiryId: Int) {
        val inquiryUpdateAction = InquiryUpdateAction.RejectInquiryAsFreelancer(self.value.data!!.employeeId, inquiryId)
        viewModelScope.launch {
            repository.updateInquiryStatus(inquiryUpdateAction)
        }
    }

    fun setOnlineStatus(status: Boolean) {
        viewModelScope.launch {
            repository.setEmployeeStatus(self.value.data!!.employeeId, status)
        }
    }
}
