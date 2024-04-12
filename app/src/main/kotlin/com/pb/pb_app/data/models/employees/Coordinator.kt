package com.pb.pb_app.data.models.employees

import com.pb.pb_app.data.enums.EmployeeRole
import com.pb.pb_app.data.models.abstracts.BaseEmployee
import kotlinx.serialization.Serializable

@Serializable
data class Coordinator(
    override val employeeId: String,
    override var name: String,
    override val emailAddress: String,
    override val contactNumber: String,
    override val availabilityStatus: Boolean,
) : BaseEmployee() {
    override val role: EmployeeRole = EmployeeRole.COORDINATOR
}