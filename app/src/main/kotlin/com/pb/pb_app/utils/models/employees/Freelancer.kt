package com.pb.pb_app.utils.models.employees

import androidx.annotation.Keep
import com.pb.pb_app.utils.KtorServerConnector
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.projects.Enquiry
import com.pb.pb_app.utils.models.projects.EnquiryStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.security.InvalidParameterException

@Keep
@Serializable
class Freelancer(
    @SerialName("id")
    override val id: Int,
    @SerialName("employee_id")
    override val username: String,
    @SerialName("email")
    override val emailAddress: String,
    @SerialName("name")
    override val name: String,
    @SerialName("contact_number")
    override val contactNumber: String,
    @SerialName("status")
    override var isUserOnline: Boolean
) : GenericEmployee() {
    companion object {
        const val ROLE_NAME: String = "Freelancer"
    }

    suspend fun acceptProject(projectID: Int) {
        val enquiry = getEnquiries().data.find { it.id == projectID } ?: throw InvalidParameterException("Can't accept project")

        with(enquiry) {
            this@Freelancer.acceptEnquiryAsFR()
        }
    }

    override suspend fun getEnquiries(): Resource.Success<List<Enquiry>> {
        return KtorServerConnector.getProjectsByUsernameAndStatus(username, EnquiryStatus.PCAssignedUnaccepted)
    }

}