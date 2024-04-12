package com.pb.pb_app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(
    val employeeId: String,
    val password: String
)
