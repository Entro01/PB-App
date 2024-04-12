package com.pb.pb_app.data.models.abstracts

import com.pb.pb_app.data.enums.EmployeeRole
import com.pb.pb_app.data.serializers.EmployeeSerializer
import kotlinx.serialization.Serializable


@Serializable(EmployeeSerializer::class)
abstract class BaseEmployee {
    abstract val employeeId: String
    abstract val name: String
    abstract val emailAddress: String
    abstract val contactNumber: String
    abstract val availabilityStatus: Boolean
    abstract val role: EmployeeRole
}