package com.pb.pb_app.data.enums


enum class AuthenticationState(val isError: Boolean?) {
    LOGIN_SUCCESS(false),
    AUTHENTICATION_FAILURE(true),
    LOGIN_UNDONE(null)
}