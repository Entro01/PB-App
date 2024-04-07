package com.pb.pb_app.utils.models.employees

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewUser(
    @SerialName("name") override var name: String,
    @SerialName("employee_id") override val employeeId: String,
    @SerialName("email") override val emailAddress: String,
    @SerialName("contact_number") override val contactNumber: String,
    @SerialName("status") override val isOnline: Boolean,
) : GenericEmployee() {
    override val id: Int
        get() = throw IllegalAccessError("NewUser doesn't have id")
    override val role: String
        get() = throw IllegalAccessError("NewUser doesn't have username")
}

