package com.pb.pb_app.utils.models.projects

import com.pb.pb_app.utils.KtorServerConnector
import com.pb.pb_app.utils.models.employees.Admin
import com.pb.pb_app.utils.models.employees.Coordinator
import com.pb.pb_app.utils.models.employees.Freelancer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Enquiry(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("repo_link")
    val repositoryLink: String?,
    @SerialName("coordinator")
    var coordinatorUsername: String?,
    @SerialName("status")
    var statusCode: Int,
    @SerialName("freelancer")
    var freelancerUsernames: String?
) {

    suspend fun Admin.reject() {
        statusCode = -1
    }

    suspend fun Admin.acceptAndAssignPC(coordinatorUsername: String) {
        KtorServerConnector.changeProjectStatus(id, coordinatorUsername, EnquiryStatus.PCAssignedUnaccepted)
    }

    suspend fun Coordinator.acceptAndAssignFR(freelancerUsernames: String) {
        KtorServerConnector.changeProjectStatus(id, freelancerUsernames, EnquiryStatus.FRAssignedUnaccepted)
    }

    suspend fun Freelancer.acceptEnquiryAsFR() {
        KtorServerConnector.changeProjectStatus(id, this.username, EnquiryStatus.FRAssignedAccepted)
    }
}