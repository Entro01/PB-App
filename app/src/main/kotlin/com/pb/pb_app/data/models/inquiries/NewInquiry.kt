package com.pb.pb_app.data.models.inquiries

import com.pb.pb_app.data.models.abstracts.BaseInquiry
import kotlinx.serialization.Serializable

@Serializable
data class NewInquiry(
    override val name: String,
    override val description: String,
    override val creationTime: Long,
    override val deadlineMillis: Long,
    override val service: String,
    override val contactNumber: String,
    override val deliveryArea: String,
    override val reference: Boolean,
) : BaseInquiry()
