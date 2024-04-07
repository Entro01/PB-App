package com.pb.pb_app.utils.models.employees

import androidx.annotation.Keep
import com.pb.pb_app.utils.KtorServerConnector
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.projects.Enquiry
import com.pb.pb_app.utils.models.projects.EnquiryStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
class Coordinator(
    @SerialName("id") override val id: Int,
    @SerialName("employee_id") override val employeeId: String,
    @SerialName("email") override val emailAddress: String,
    @SerialName("name") override var name: String,
    @SerialName("contact_number") override val contactNumber: String,
    @SerialName("is_online") override val isOnline: Boolean,
    ) : GenericEmployee() {

    override val role: String = "Coordinator"

    suspend fun getFreelancers(): Resource<List<Freelancer>> {
        return KtorServerConnector.getFreelancers()
    }

    suspend fun getEnquiriesByStatus(vararg enquiryStatus: EnquiryStatus): Resource<List<Enquiry>> {
        return KtorServerConnector.getEnquiriesByUsernameAndStatus(employeeId, *enquiryStatus)
    }

    suspend fun setOnlineStatus(status: Boolean): Boolean {
        return KtorServerConnector.setOnlineStatus(employeeId, status)
    }
}