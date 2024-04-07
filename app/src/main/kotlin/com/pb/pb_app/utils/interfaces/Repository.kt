package com.pb.pb_app.utils.interfaces

import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.employees.GenericEmployee
import com.pb.pb_app.utils.models.employees.NewUser
import com.pb.pb_app.utils.models.projects.NewEnquiryHolder

interface Repository {
    fun getLoggedInUserName(): String?

    suspend fun getEmployeeByUsername(username: String): Resource<GenericEmployee>

    fun logout()

    suspend fun authenticateUser(username: String, password: String): Boolean

    suspend fun createNewEnquiry(enquiry: NewEnquiryHolder)

    suspend fun createNewEmployee(newUser: NewUser)
}