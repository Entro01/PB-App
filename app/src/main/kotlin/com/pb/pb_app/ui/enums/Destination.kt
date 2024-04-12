package com.pb.pb_app.ui.enums

enum class Destination(val route: String) {
    ADMIN_SCREEN("Admin"),
    COORDINATOR_SCREEN("Coordinator"),
    FREELANCER_SCREEN("Freelancer"),
    LOGIN_SCREEN("Login"),
    NEW_INQUIRY_SCREEN("New Inquiry"),
    NEW_EMPLOYEE_SCREEN("New Employee");

    companion object {
        fun String.valueOf(): Destination {
            return Destination.valueOf(this)
        }

        fun String.fromRoute(): Destination {
            return when (this) {
                ADMIN_SCREEN.route -> ADMIN_SCREEN
                COORDINATOR_SCREEN.route -> COORDINATOR_SCREEN
                FREELANCER_SCREEN.route -> FREELANCER_SCREEN
                LOGIN_SCREEN.route -> LOGIN_SCREEN
                NEW_INQUIRY_SCREEN.route -> NEW_INQUIRY_SCREEN
                NEW_EMPLOYEE_SCREEN.route -> NEW_EMPLOYEE_SCREEN
                else -> throw IllegalArgumentException("No such route")
            }
        }
    }
}