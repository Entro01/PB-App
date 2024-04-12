package com.pb.pb_app.utils.serializers

import com.pb.pb_app.utils.Constants.InquiryStatusLabels.COORDINATOR_REQUESTED
import com.pb.pb_app.utils.Constants.InquiryStatusLabels.FREELANCER_ACCEPTED
import com.pb.pb_app.utils.Constants.InquiryStatusLabels.FREELANCER_ASSIGNED
import com.pb.pb_app.utils.Constants.InquiryStatusLabels.FREELANCER_REQUESTED
import com.pb.pb_app.utils.Constants.InquiryStatusLabels.INQUIRY_RESOLVED
import com.pb.pb_app.utils.Constants.InquiryStatusLabels.UNASSIGNED
import com.pb.pb_app.utils.models.projects.InquiryStatus
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val TAG = "InquiryStatusSerializer"

object InquiryStatusSerializer : JsonContentPolymorphicSerializer<InquiryStatus>(InquiryStatus::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<InquiryStatus> {
        return when (val label = (element as JsonObject)["label"]?.jsonPrimitive?.content) {
            UNASSIGNED -> InquiryStatus.Unassigned.serializer()
            COORDINATOR_REQUESTED -> InquiryStatus.CoordinatorRequested.serializer()
            FREELANCER_REQUESTED -> InquiryStatus.FreelancerRequested.serializer()
            FREELANCER_ACCEPTED -> InquiryStatus.FreelancerAccepted.serializer()
            FREELANCER_ASSIGNED -> InquiryStatus.FreelancerAssigned.serializer()
            INQUIRY_RESOLVED -> InquiryStatus.InquiryResolved.serializer()
            else -> throw IllegalArgumentException("Not a valid status $label")
        }
    }
}
