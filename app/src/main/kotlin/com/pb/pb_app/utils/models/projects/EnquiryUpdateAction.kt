package com.pb.pb_app.utils.models.projects

import com.pb.pb_app.utils.models.employees.Freelancer
import kotlinx.serialization.Serializable

@Serializable
enum class EnquiryStatus() {
    NEW_ENQUIRY,
    COORDINATORS_REQUESTED,
    COORDINATOR_FINALIZED,
    FREELANCERS_REQUESTED,
    FREELANCERS_ACCEPTED,
    FREELANCER_FINALIZED,
    ENQUIRY_RESOLVED,
}

@Serializable
sealed class EnquiryUpdateAction(val name: String) {
    class CoordinatorRequested(coordinatorId: String, time: Long) : EnquiryUpdateAction("COORDINATOR_REQUESTED")
    class CoordinatorsAccepted(coordinatorId: String) : EnquiryUpdateAction("COORDINATORS_ACCEPTED")
    class CoordinatorRejected(coordinatorId: String) : EnquiryUpdateAction("COORDINATOR_REJECTED")
    class CoordinatorTimeUp(freelancerId: String) : EnquiryUpdateAction("COORDINATOR_TIME_UP")
    class FreelancersRequested(freelancerId: String, time: Long) : EnquiryUpdateAction("FREELANCERS_REQUESTED")
    class FreelancersAccepted(freelancerId: String) : EnquiryUpdateAction("FREELANCERS_ACCEPTED")
    class FreelancersRejected(freelancerId: String) : EnquiryUpdateAction("FREELANCERS_REJECTED")
    class FreelancersTimeUp(freelancer: Freelancer) : EnquiryUpdateAction("FREELANCERS_TIME_UP")
    class FreelancersFinalized(freelancer: Freelancer) : EnquiryUpdateAction("FREELANCERS_FINALIZED")
}
