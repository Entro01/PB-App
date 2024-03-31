package com.pb.pb_app.utils.interfaces

import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.employees.GenericEmployee

interface Repository {
    fun getLoggedInUser(): String?

    suspend fun getEmployeeByUsername(username: String): Resource.Success<GenericEmployee>

    fun logout()

    suspend fun authenticateUser(username: String, password: String): Boolean

    suspend fun createNewEnquiry(name: String, description: String)
}