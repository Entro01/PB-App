package com.pb.serializer

import com.pb.pb_app.data.Constants
import com.pb.pb_app.data.models.inquiries.InquiryUpdateAction
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive


object InquiryUpdateActionSerializer : JsonContentPolymorphicSerializer<InquiryUpdateAction>(InquiryUpdateAction::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<InquiryUpdateAction> {
        println("printed stuff: $element")
        return when (val label = (element as JsonObject)["label"]?.jsonPrimitive?.content) {
            Constants.InquiryUpdateActions.CREATE_INQUIRY_AS_ADMIN -> {
                InquiryUpdateAction.CreateInquiryAsAdmin.serializer()
            }

            Constants.InquiryUpdateActions.REQUEST_COORDINATOR_AS_ADMIN -> {
                InquiryUpdateAction.RequestCoordinatorAsAdmin.serializer()
            }

            Constants.InquiryUpdateActions.REQUEST_FREELANCER_AS_COORDINATOR -> {
                InquiryUpdateAction.RequestFreelancerAsCoordinator.serializer()
            }

            Constants.InquiryUpdateActions.ACCEPT_INQUIRY_AS_FREELANCER -> {
                InquiryUpdateAction.AcceptInquiryAsFreelancer.serializer()
            }

            Constants.InquiryUpdateActions.ASSIGN_FREELANCER_AS_COORDINATOR -> {
                InquiryUpdateAction.AssignFreelancerAsCoordinator.serializer()
            }

            Constants.InquiryUpdateActions.MARK_RESOLVED_AS_ADMIN -> {
                InquiryUpdateAction.MarkResolvedAsAdmin.serializer()
            }

            Constants.InquiryUpdateActions.DELETE_INQUIRY_AS_ADMIN -> {
                InquiryUpdateAction.DeleteInquiryAsAdmin.serializer()
            }

            Constants.InquiryUpdateActions.REJECT_INQUIRY_AS_COORDINATOR -> {
                InquiryUpdateAction.RejectInquiryAsCoordinator.serializer()
            }

            Constants.InquiryUpdateActions.REJECT_INQUIRY_AS_FREELANCER -> {
                InquiryUpdateAction.RejectInquiryAsFreelancer.serializer()
            }

            else -> {
                throw IllegalArgumentException("IDK WHAT HAPPENED")
            }
        }
    }
}
