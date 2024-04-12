package com.pb.pb_app.utils.models.projects

import com.pb.pb_app.utils.Constants.InquiryStatusLabels
import com.pb.pb_app.utils.serializers.InquiryStatusSerializer
import kotlinx.serialization.Serializable

@Serializable(InquiryStatusSerializer::class)
sealed class InquiryStatus {
    abstract val inquiryId: Int
    abstract val label: String

    @Serializable
    data class Unassigned(
        override val inquiryId: Int, override val label: String = InquiryStatusLabels.UNASSIGNED
    ) : InquiryStatus()

    @Serializable
    data class CoordinatorRequested(
        override val inquiryId: Int,
        val requestedCoordinator: String,
        val countDownMillis: Long,
        override val label: String = InquiryStatusLabels.COORDINATOR_REQUESTED
    ) : InquiryStatus()

    @Serializable
    data class FreelancerRequested(
        override val inquiryId: Int,
        val coordinator: String,
        val freelancerFirst: String,
        val freelancerSecond: String?,
        val freelancerThird: String?,
        val firstCountDownMillis: Long,
        val secondCountDownMillis: Long?,
        val thirdCountDownMillis: Long?,
        override val label: String = InquiryStatusLabels.FREELANCER_REQUESTED
    ) : InquiryStatus()

    @Serializable
    data class FreelancerAccepted(
        override val inquiryId: Int,
        val coordinator: String,
        val freelancerFirst: String,
        val freelancerSecond: String?,
        val freelancerThird: String?,
        override val label: String = InquiryStatusLabels.FREELANCER_ACCEPTED
    ) : InquiryStatus()

    @Serializable
    data class FreelancerAssigned(
        override val inquiryId: Int, val coordinator: String, val freelancer: String, override val label: String = InquiryStatusLabels.FREELANCER_ASSIGNED
    ) : InquiryStatus()

    @Serializable
    data class InquiryResolved(
        override val inquiryId: Int, override val label: String = InquiryStatusLabels.INQUIRY_RESOLVED, val tags: String? = null
    ) : InquiryStatus()
}