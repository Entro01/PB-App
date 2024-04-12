package com.pb.pb_app.utils.models


enum class AuthenticationState(val isError: Boolean?) {
    LOGIN_SUCCESS(false),
    AUTHENTICATION_FAILURE(true),
    LOGIN_UNDONE(null)
}