package com.pb.pb_app.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.pb.pb_app.data.RepositoryImpl
import com.pb.pb_app.data.models.Resource
import com.pb.pb_app.data.models.abstracts.BaseEmployee
import com.pb.pb_app.data.models.employees.Coordinator
import com.pb.pb_app.data.models.employees.Freelancer
import com.pb.pb_app.data.models.inquiries.Inquiry
import com.pb.pb_app.data.models.inquiries.InquiryUpdateAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AdminViewModel
internal constructor(context: Context) : HomeViewModel() {
    override val repository = RepositoryImpl(context)
    override val self: MutableStateFlow<Resource<BaseEmployee>> = MutableStateFlow(Resource.Loading())
    override var urgentInquiries: MutableStateFlow<Resource<List<Inquiry>>> = MutableStateFlow(Resource.Loading())
    override var miscInquiries: MutableStateFlow<Resource<List<Inquiry>>> = MutableStateFlow(Resource.Loading())

    var freelancers: MutableStateFlow<Resource<List<Freelancer>>> = MutableStateFlow(Resource.Loading())
    var coordinators: MutableStateFlow<Resource<List<Coordinator>>> = MutableStateFlow(Resource.Loading())
    var selectedCoordinators = mutableListOf<String>()

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
                    coordinators.emit(repository.getCoordinators())
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