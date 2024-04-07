package com.pb.pb_app.utils.models.employees

import com.pb.pb_app.utils.KtorServerConnector
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.projects.Enquiry
import com.pb.pb_app.utils.models.projects.EnquiryStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
class Admin(
    @SerialName("id") override val id: Int,
    @SerialName("employee_id") override val employeeId: String,
    @SerialName("email") override val emailAddress: String,
    @SerialName("name") override var name: String,
    @SerialName("contact_number") override val contactNumber: String,
    @SerialName("status") override val isOnline: Boolean,
) : GenericEmployee() {


    @SerialName("role")
    override val role = "Admin"


    suspend fun getEnquiriesByStatus(vararg enquiryStatus: EnquiryStatus): Resource<List<Enquiry>> {
        return KtorServerConnector.getEnquiriesByUsernameAndStatus(employeeId, *enquiryStatus)
    }

    suspend fun getFreelancers(): Resource<List<Freelancer>> {
        return KtorServerConnector.getFreelancers()
    }

    suspend fun getCoordinators(): Resource<List<Coordinator>> {
        return KtorServerConnector.getCoordinators()
    }
}