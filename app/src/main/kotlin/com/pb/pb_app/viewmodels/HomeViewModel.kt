package com.pb.pb_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.pb.pb_app.data.RepositoryImpl
import com.pb.pb_app.data.models.Resource
import com.pb.pb_app.data.models.abstracts.BaseEmployee
import com.pb.pb_app.data.enums.EmployeeRole
import com.pb.pb_app.data.models.inquiries.Inquiry
import kotlinx.coroutines.flow.MutableStateFlow

abstract class HomeViewModel
internal constructor() : ViewModel() {
    abstract val repository: RepositoryImpl
    abstract val self: MutableStateFlow<Resource<BaseEmployee>>
    abstract val urgentInquiries: MutableStateFlow<Resource<List<Inquiry>>>
    abstract val miscInquiries: MutableStateFlow<Resource<List<Inquiry>>>

    companion object {
        fun factory(role: EmployeeRole) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val appContext = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]?.applicationContext ?: throw IllegalStateException("no app running")
                return when (role) {
                    EmployeeRole.ADMIN -> AdminViewModel(appContext)
                    EmployeeRole.COORDINATOR -> CoordinatorViewModel(appContext)
                    EmployeeRole.FREELANCER -> FreelancerViewModel(appContext)
                } as T
            }
        }
    }
}
