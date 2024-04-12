package com.pb.pb_app.utils.models.employees

import com.pb.pb_app.utils.models.Destination
import kotlinx.serialization.Serializable

@Serializable
enum class EmployeeRole() {
    ADMIN, COORDINATOR, FREELANCER;

    companion object {
        fun String.fromEmployeeId(): EmployeeRole {
            return if (this.startsWith("PB-AM")) ADMIN
            else if (this.startsWith("PB-PC")) COORDINATOR
            else if (this.startsWith("PB-FR")) FREELANCER
            else throw IllegalArgumentException("Not a valid username")
        }

        val EmployeeRole.screen: Destination
            get() = when (this) {
                ADMIN -> Destination.ADMIN_SCREEN
                COORDINATOR -> Destination.COORDINATOR_SCREEN
                FREELANCER -> Destination.FREELANCER_SCREEN
            }
    }
}