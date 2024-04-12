package com.pb.pb_app.utils.models.employees

import kotlinx.serialization.Serializable

@Serializable
data class Freelancer(
    override val employeeId: String,
    override var name: String,
    override val emailAddress: String,
    override val contactNumber: String,
    override val availabilityStatus: Boolean,
) : BaseEmployee() {
    override val role: EmployeeRole = EmployeeRole.FREELANCER
}