package com.pb.pb_app.data.models.inquiries

import com.pb.pb_app.data.Constants.InquiryUpdateActions.ACCEPT_INQUIRY_AS_FREELANCER
import com.pb.pb_app.data.Constants.InquiryUpdateActions.ASSIGN_FREELANCER_AS_COORDINATOR
import com.pb.pb_app.data.Constants.InquiryUpdateActions.CREATE_INQUIRY_AS_ADMIN
import com.pb.pb_app.data.Constants.InquiryUpdateActions.DELETE_INQUIRY_AS_ADMIN
import com.pb.pb_app.data.Constants.InquiryUpdateActions.MARK_RESOLVED_AS_ADMIN
import com.pb.pb_app.data.Constants.InquiryUpdateActions.REJECT_INQUIRY_AS_COORDINATOR
import com.pb.pb_app.data.Constants.InquiryUpdateActions.REJECT_INQUIRY_AS_FREELANCER
import com.pb.pb_app.data.Constants.InquiryUpdateActions.REQUEST_COORDINATOR_AS_ADMIN
import com.pb.pb_app.data.Constants.InquiryUpdateActions.REQUEST_FREELANCER_AS_COORDINATOR
import com.pb.serializer.InquiryUpdateActionSerializer
import kotlinx.serialization.Serializable

@Serializable(InquiryUpdateActionSerializer::class)
abstract class InquiryUpdateAction() {
    abstract val label: String
    abstract val inquiryId: Int


    @Serializable
    data class CreateInquiryAsAdmin(val inquiry: NewInquiry) : InquiryUpdateAction() {
        override val label: String = CREATE_INQUIRY_AS_ADMIN
        override val inquiryId = -1
    }

    @Serializable
    data class RequestCoordinatorAsAdmin(val requestingAdminId: String, val requestedCoordinatorId: String, override val inquiryId: Int, val countDownMillis: Long) :
        InquiryUpdateAction() {
        override val label = REQUEST_COORDINATOR_AS_ADMIN
    }

    @Serializable
    data class RequestFreelancerAsCoordinator(val requestingCoordinatorId: String, val requestedFreelancerId: String, override val inquiryId: Int, val countDownMillis: Long) :
        InquiryUpdateAction() {
        override val label = REQUEST_FREELANCER_AS_COORDINATOR
    }

    @Serializable
    data class AcceptInquiryAsFreelancer(val acceptorFreelancerId: String, override val inquiryId: Int) : InquiryUpdateAction() {
        override val label: String = ACCEPT_INQUIRY_AS_FREELANCER


    }

    @Serializable
    data class AssignFreelancerAsCoordinator(val assignorCoordinatorId: String, val assignedFreelancerId: String, override val inquiryId: Int) : InquiryUpdateAction() {
        override val label: String = ASSIGN_FREELANCER_AS_COORDINATOR
    }

    @Serializable
    data class MarkResolvedAsAdmin(val markingAdminId: String, override val inquiryId: Int) : InquiryUpdateAction() {
        override val label: String = MARK_RESOLVED_AS_ADMIN
    }


    @Serializable
    data class DeleteInquiryAsAdmin(val deletingAdminId: String, override val inquiryId: Int) : InquiryUpdateAction() {
        override val label: String = DELETE_INQUIRY_AS_ADMIN
    }

    @Serializable
    data class RejectInquiryAsCoordinator(val rejectingCoordinatorId: String, override val inquiryId: Int) : InquiryUpdateAction() {
        override val label: String = REJECT_INQUIRY_AS_COORDINATOR
    }

    @Serializable
    data class RejectInquiryAsFreelancer(val rejectingFreelancerId: String, override val inquiryId: Int) : InquiryUpdateAction() {
        override val label = REJECT_INQUIRY_AS_FREELANCER
    }

}