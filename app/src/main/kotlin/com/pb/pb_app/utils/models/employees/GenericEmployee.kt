package com.pb.pb_app.utils.models.employees

import com.pb.pb_app.utils.EmployeeSerializer
import com.pb.pb_app.utils.KtorServerConnector
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.projects.Enquiry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(EmployeeSerializer::class)
sealed class GenericEmployee {

    @SerialName("id")
    abstract val id: Int

    @SerialName("employee_id")
    abstract val username: String

    @SerialName("email")
    abstract val emailAddress: String

    @SerialName("name")
    abstract val name: String

    @SerialName("contact_number")
    abstract val contactNumber: String

    @SerialName("status")
    abstract var isUserOnline: Boolean

    suspend fun setOnlineStatus(status: Boolean) {
        KtorServerConnector.setOnlineStatus(username, status)
    }

    abstract suspend fun getEnquiries(): Resource.Success<List<Enquiry>>
}