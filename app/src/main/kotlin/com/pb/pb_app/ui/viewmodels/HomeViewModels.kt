package com.pb.pb_app.ui.viewmodels

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
import com.pb.pb_app.utils.models.projects.EnquiryStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


object HomeViewModels {
    fun getHomeViewModelInstance(user: GenericEmployee) = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val appContext = extras[APPLICATION_KEY]!!.applicationContext
            return when (user) {
                is Admin -> AdminViewModel(user, appContext)
                is Coordinator -> CoordinatorViewModel(user, appContext)
                is Freelancer -> FreelancerViewModel(user, appContext)
                else -> {
                    throw IllegalArgumentException("No viewmodel for this user ${user.role}")
                }
            } as T
        }
    }

    abstract class HomeViewModel
    internal constructor(open val user: GenericEmployee) : ViewModel() {
        abstract val repository: Repository
    }

    class AdminViewModel
    internal constructor(override val user: Admin, context: Context) : HomeViewModel(user) {
        override val repository: Repository = RepositoryImpl(context)

        var freelancers: MutableStateFlow<Resource<List<Freelancer>>> = MutableStateFlow(Resource.Loading())
        var coordinators: MutableStateFlow<Resource<List<Coordinator>>> = MutableStateFlow(Resource.Loading())
        var newEnquiries: MutableStateFlow<Resource<List<Enquiry>>> = MutableStateFlow(Resource.Loading())
        var otherEnquiries: MutableStateFlow<Resource<List<Enquiry>>> = MutableStateFlow(Resource.Loading())

        init {
            with(viewModelScope) {
                launch {
                    freelancers.emit(user.getFreelancers())
                }

                launch {
                    coordinators.emit(user.getCoordinators())
                }

                launch {
                    newEnquiries.emit(user.getEnquiriesByStatus(EnquiryStatus.NEW_ENQUIRY))
                }

                launch {
                    otherEnquiries.emit(user.getEnquiriesByStatus())
                }
            }
        }
    }

    class CoordinatorViewModel
    internal constructor(override val user: Coordinator, context: Context) : HomeViewModel(user) {
        override val repository: Repository = RepositoryImpl(context)

        var freelancers: MutableStateFlow<Resource<List<Freelancer>>> = MutableStateFlow(Resource.Loading())
        var newEnquiries: MutableStateFlow<Resource<List<Enquiry>>> = MutableStateFlow(Resource.Loading())
        var otherEnquiries: MutableStateFlow<Resource<List<Enquiry>>> = MutableStateFlow(Resource.Loading())

        init {
            with(viewModelScope) {
                launch {
                    freelancers.emit(user.getFreelancers())
                }

                launch {
                    newEnquiries.emit(user.getEnquiriesByStatus(EnquiryStatus.COORDINATORS_REQUESTED))
                }

                launch {
                    otherEnquiries.emit(user.getEnquiriesByStatus())
                }
            }
        }

        fun setOnlineStatus(status: Boolean) {
            viewModelScope.launch {
                user.setOnlineStatus(status)
            }
        }
    }

    class FreelancerViewModel
    internal constructor(override val user: Freelancer, context: Context) : HomeViewModel(user) {
        override val repository: Repository = RepositoryImpl(context)

        var newInquiries: MutableStateFlow<Resource<List<Enquiry>>> = MutableStateFlow(Resource.Loading())

        init {
            with(viewModelScope) {
                launch {
                    newInquiries.emit(user.getInquiriesByStatus())
                }
            }
        }

        fun setOnlineStatus(status: Boolean) {
            viewModelScope.launch {
                user.setOnlineStatus(status)
            }
        }
    }
}