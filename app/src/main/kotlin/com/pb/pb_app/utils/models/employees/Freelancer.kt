package com.pb.pb_app.utils.models.employees

import androidx.annotation.Keep
import com.pb.pb_app.utils.KtorServerConnector
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.projects.Enquiry
import com.pb.pb_app.utils.models.projects.EnquiryStatus
import com.pb.pb_app.utils.models.projects.EnquiryUpdateAction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
class Freelancer(
    @SerialName("id") override val id: Int,
    @SerialName("employee_id") override val employeeId: String,
    @SerialName("email") override val emailAddress: String,
    @SerialName("name") override var name: String,
    @SerialName("contact_number") override val contactNumber: String,
    @SerialName("status") override val isOnline: Boolean,
) : GenericEmployee() {

    override val role: String = "Freelancer"
    suspend fun getInquiriesByStatus(vararg enquiryStatus: EnquiryStatus): Resource<List<Enquiry>> {
        return KtorServerConnector.getEnquiriesByUsernameAndStatus(employeeId, *enquiryStatus)
    }

    suspend fun setOnlineStatus(status: Boolean): Boolean {
        return KtorServerConnector.setOnlineStatus(employeeId, status)
    }
}