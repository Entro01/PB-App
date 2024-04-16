package com.pb.pb_app.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.pb.pb_app.data.RepositoryImpl
import com.pb.pb_app.data.models.Resource
import com.pb.pb_app.data.models.abstracts.BaseEmployee
import com.pb.pb_app.data.models.employees.Freelancer
import com.pb.pb_app.data.models.inquiries.Inquiry
import com.pb.pb_app.data.models.inquiries.InquiryUpdateAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class CoordinatorViewModel
internal constructor(context: Context) : HomeViewModel() {
    override val repository = RepositoryImpl(context)
    override val self: MutableStateFlow<Resource<BaseEmployee>> = MutableStateFlow(Resource.Loading())
    override var urgentInquiries: MutableStateFlow<Resource<List<Inquiry>>> = MutableStateFlow(Resource.Loading())
    override var miscInquiries: MutableStateFlow<Resource<List<Inquiry>>> = MutableStateFlow(Resource.Loading())

    var freelancers: MutableStateFlow<Resource<List<Freelancer>>> = MutableStateFlow(Resource.Loading())
    private var assignedFreelancer = mutableListOf<String>()


    init {
        with(viewModelScope) {
            launch {
                while (isActive) {
                    self.emit(repository.getSelf())
                    delay(2000) // Sleep for 2000 ms
                }
            }
            launch {
                while (isActive) {
                    freelancers.emit(repository.getFreelancers())
                    delay(2000) // Sleep for 2000 ms
                }
            }
            launch {
                while (isActive) {
                    urgentInquiries.emit(repository.getUrgentInquiries())
                    delay(2000) // Sleep for 2000 ms
                }
            }
            launch {
                while (isActive) {
                    miscInquiries.emit(repository.getMiscInquiries())
                    delay(2000) // Sleep for 2000 ms
                }
            }
        }
    }


    fun appendFreelancer(freelancerId: String) {
        assignedFreelancer += freelancerId
    }

    fun acceptUrgentInquiry(inquiryId: Int) {
        viewModelScope.launch {
            assignedFreelancer.forEach {
                val inquiryUpdateAction = InquiryUpdateAction.RequestFreelancerAsCoordinator(self.value.data!!.employeeId, it, inquiryId, System.currentTimeMillis(), System.currentTimeMillis() + 1800000)
                repository.updateInquiryStatus(inquiryUpdateAction)
            }
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
            repository.setSelfStatus(status)
        }
    }

    fun assignFreelancer(freelancerId: String, inquiryId: Int) {
        val inquiryUpdateAction = InquiryUpdateAction.AssignFreelancerAsCoordinator(self.value.data!!.employeeId, freelancerId, inquiryId)

        viewModelScope.launch {
            repository.updateInquiryStatus(inquiryUpdateAction)
        }
    }
}