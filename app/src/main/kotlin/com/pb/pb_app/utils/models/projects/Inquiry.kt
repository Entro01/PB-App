package com.pb.pb_app.utils.models.projects

import kotlinx.serialization.Serializable

@Serializable
data class Inquiry(
    val id: Int,
    var name: String,
    val service: String,
    var description: String,
    val contactNumber: String,
    val assigningMillis: Long,
    val deadlineMillis: Long,
    val deliveryArea: String,
    val reference: Boolean,
    var status: InquiryStatus
)