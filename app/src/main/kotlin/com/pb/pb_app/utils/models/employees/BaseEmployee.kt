package com.pb.pb_app.utils.models.employees

import com.pb.pb_app.utils.serializers.EmployeeSerializer
import kotlinx.serialization.Serializable


@Serializable(EmployeeSerializer::class)
sealed class BaseEmployee {
    abstract val employeeId: String
    abstract val name: String
    abstract val emailAddress: String
    abstract val contactNumber: String
    abstract val availabilityStatus: Boolean
    abstract val role: EmployeeRole
}