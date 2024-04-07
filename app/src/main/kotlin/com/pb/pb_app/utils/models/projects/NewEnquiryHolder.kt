package com.pb.pb_app.utils.models.projects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewEnquiryHolder(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    var name: String,

    @SerialName("deadline")
    val deadline: Long,

    @SerialName("Service")
    val service: String,

    @SerialName("description")
    var description: String,

    @SerialName("contact_number")
    val contact: String,

    @SerialName("delivery_area")
    val deliveryArea: String,

    @SerialName("reference")
    val reference: Boolean,

    @SerialName("status")
    var statusObject: EnquiryUpdateAction,
)