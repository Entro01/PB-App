package com.pb.pb_app.utils.models.employees

import com.pb.pb_app.utils.EmployeeSerializer
import kotlinx.serialization.Serializable


@Serializable(EmployeeSerializer::class)
abstract class GenericEmployee {
    abstract val id: Int

    abstract val employeeId: String

    abstract val name: String

    abstract val emailAddress: String

    abstract val contactNumber: String

    abstract val role: String

    abstract val isOnline: Boolean
}