package com.pb.pb_app.utils.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Credentials(
    @SerialName("employee_id")
    val employeeId: String,

    val password: String
)
