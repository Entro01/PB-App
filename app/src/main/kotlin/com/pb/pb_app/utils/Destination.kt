package com.pb.pb_app.utils

import java.lang.IllegalArgumentException

enum class Destination(val route: String) {
    HOME_SCREEN("Home"),
    LOGIN_SCREEN("Login"),
    NEW_INQUIRY_SCREEN("New Inquiry"),
    NEW_EMPLOYEE_SCREEN("New Employee");

    companion object {

        fun getDestinationByRoute(route: String): Destination {
            return when (route) {
                HOME_SCREEN.route  -> HOME_SCREEN
                LOGIN_SCREEN.route  -> LOGIN_SCREEN
                NEW_INQUIRY_SCREEN.route  -> NEW_INQUIRY_SCREEN
                NEW_EMPLOYEE_SCREEN.route  -> NEW_EMPLOYEE_SCREEN
                else -> throw IllegalArgumentException("No Destination $route lol")
            }
        }
    }
}