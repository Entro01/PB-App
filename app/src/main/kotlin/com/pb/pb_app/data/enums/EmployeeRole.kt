package com.pb.pb_app.data.enums

import com.pb.pb_app.ui.enums.Destination
import kotlinx.serialization.Serializable

@Serializable
enum class EmployeeRole() {
    ADMIN, COORDINATOR, FREELANCER;

    companion object {
        fun String.parseEmployeeId(): EmployeeRole {
            return when {
                this.startsWith("PB-AM") -> ADMIN
                this.startsWith("PB-PC") -> COORDINATOR
                this.startsWith("PB-FR") -> FREELANCER
                else -> throw IllegalArgumentException("Not a valid username")
            }
        }

        fun String.fromString() {
            val string = this.lowercase()
            when (string) {
                "admin" -> ADMIN
                "coordinator" -> COORDINATOR
                "freelancer" -> FREELANCER
            }
        }

        val EmployeeRole.screen: Destination
            get() = when (this) {
                ADMIN -> Destination.ADMIN_SCREEN
                COORDINATOR -> Destination.COORDINATOR_SCREEN
                FREELANCER -> Destination.FREELANCER_SCREEN
            }
    }
}