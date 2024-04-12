package com.pb.pb_app.utils.models.employees

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
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