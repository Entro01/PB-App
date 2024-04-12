package com.pb.pb_app.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.pb.pb_app.data.RepositoryImpl
import com.pb.pb_app.data.models.Resource
import com.pb.pb_app.data.models.abstracts.BaseEmployee
import com.pb.pb_app.data.models.inquiries.Inquiry
import com.pb.pb_app.data.models.inquiries.InquiryUpdateAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class FreelancerViewModel
internal constructor(context: Context) : HomeViewModel() {
    override val repository = RepositoryImpl(context)
    override val self: MutableStateFlow<Resource<BaseEmployee>> = MutableStateFlow(Resource.Loading())
    override val urgentInquiries: MutableStateFlow<Resource<List<Inquiry>>> = MutableStateFlow(Resource.Loading())
    override val miscInquiries: MutableStateFlow<Resource<List<Inquiry>>>
        get() = TODO("Not yet implemented")


    init {
        with(viewModelScope) {
            launch {
                while (isActive) {
                    self.emit(repository.getSelf())
                    delay(2000)
                }
            }

            launch {
                while (isActive) {
                    urgentInquiries.emit(repository.getUrgentInquiries())
                    delay(2000)
                }
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
