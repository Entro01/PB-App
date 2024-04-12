package com.pb.pb_app.utils.models.projects

import com.pb.pb_app.utils.Constants.NegativeInquiryActionLabels.DELETE_INQUIRY_AS_ADMIN
import com.pb.pb_app.utils.Constants.NegativeInquiryActionLabels.REJECT_INQUIRY_AS_COORDINATOR
import com.pb.pb_app.utils.Constants.NegativeInquiryActionLabels.REJECT_INQUIRY_AS_FREELANCER
import com.pb.pb_app.utils.Constants.PositiveInquiryUpdateActionLabels.ACCEPT_INQUIRY_AS_FREELANCER
import com.pb.pb_app.utils.Constants.PositiveInquiryUpdateActionLabels.ASSIGN_FREELANCER_AS_COORDINATOR
import com.pb.pb_app.utils.Constants.PositiveInquiryUpdateActionLabels.CREATE_INQUIRY_AS_ADMIN
import com.pb.pb_app.utils.Constants.PositiveInquiryUpdateActionLabels.MARK_RESOLVED_AS_ADMIN
import com.pb.pb_app.utils.Constants.PositiveInquiryUpdateActionLabels.REQUEST_COORDINATOR_AS_ADMIN
import com.pb.pb_app.utils.Constants.PositiveInquiryUpdateActionLabels.REQUEST_FREELANCER_AS_COORDINATOR
import com.pb.serializer.InquiryUpdateActionSerializer
import kotlinx.serialization.Serializable

@Serializable(InquiryUpdateActionSerializer::class)
abstract class InquiryUpdateAction() {
    abstract val label: String
    abstract val inquiryId: Int


    @Serializable
    class CreateInquiryAsAdmin(val inquiry: NewInquiry) : InquiryUpdateAction() {
        override val label: String = CREATE_INQUIRY_AS_ADMIN
        override val inquiryId = -1
    }

    @Serializable
    class RequestCoordinatorAsAdmin(val requestingAdminId: String, val requestedCoordinatorId: String, override val inquiryId: Int, val countDownMillis: Long) :
        InquiryUpdateAction() {
        override val label = REQUEST_COORDINATOR_AS_ADMIN
    }

    @Serializable
    class RequestFreelancerAsCoordinator(val requestingCoordinatorId: String, val requestedFreelancerId: String, override val inquiryId: Int, val countDownMillis: Long) :
        InquiryUpdateAction() {
        override val label = REQUEST_FREELANCER_AS_COORDINATOR
    }

    @Serializable
    class AcceptInquiryAsFreelancer(val acceptorFreelancerId: String, override val inquiryId: Int) : InquiryUpdateAction() {
        override val label: String = ACCEPT_INQUIRY_AS_FREELANCER


    }

    @Serializable
    class AssignFreelancerAsCoordinator(val assignorCoordinatorId: String, val assignedFreelancerId: String, override val inquiryId: Int) : InquiryUpdateAction() {
        override val label: String = ASSIGN_FREELANCER_AS_COORDINATOR
    }

    @Serializable
    class MarkResolvedAsAdmin(val markingAdminId: String, override val inquiryId: Int) : InquiryUpdateAction() {
        override val label: String = MARK_RESOLVED_AS_ADMIN
    }


    @Serializable
    class DeleteInquiryAsAdmin(val deletingAdminId: String, override val inquiryId: Int) : InquiryUpdateAction() {
        override val label: String = DELETE_INQUIRY_AS_ADMIN
    }

    @Serializable
    class RejectInquiryAsCoordinator(val rejectingCoordinatorId: String, override val inquiryId: Int) : InquiryUpdateAction() {
        override val label: String = REJECT_INQUIRY_AS_COORDINATOR
    }

    @Serializable
    class RejectInquiryAsFreelancer(val rejectingFreelancerId: String, override val inquiryId: Int) : InquiryUpdateAction() {
        override val label = REJECT_INQUIRY_AS_FREELANCER
    }

}