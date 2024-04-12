package com.pb.pb_app.utils.models.projects

import kotlinx.serialization.Serializable

@Serializable
data class NewInquiry(
    val name: String,
    val description: String,
    val assigningMillis: Long,
    val deadlineMillis: Long,
    val service: String,
    val contactNumber: String,
    val deliveryArea: String,
    val reference: Boolean,
)
