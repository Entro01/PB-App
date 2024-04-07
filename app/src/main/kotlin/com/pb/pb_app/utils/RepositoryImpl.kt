package com.pb.pb_app.utils

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import com.pb.pb_app.R
import com.pb.pb_app.utils.interfaces.Repository
import com.pb.pb_app.utils.models.Credentials
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.employees.GenericEmployee
import com.pb.pb_app.utils.models.employees.NewUser
import com.pb.pb_app.utils.models.projects.NewEnquiryHolder

private const val TAG = "Repository"

class RepositoryImpl(context: Context) : Repository {

    private val sharedPreferencesName = context.getString(R.string.shared_preferences_name)

    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Activity.MODE_PRIVATE)

    private val usernameTag = context.getString(R.string.employee_id)


    override fun getLoggedInUserName(): String? {
        return sharedPreferences.getString(usernameTag, null)
    }

    override suspend fun getEmployeeByUsername(username: String): Resource<GenericEmployee> {
        return KtorServerConnector.getEmployeeByID(username)
    }

    override fun logout() {
        sharedPreferences.edit {
            putString(usernameTag, null)
        }
    }

    override suspend fun authenticateUser(username: String, password: String): Boolean {
        val authResponse = KtorServerConnector.authenticate(Credentials(username, password))
        if (authResponse) {
            sharedPreferences.edit {
                putString(usernameTag, username)
            }
        }
        return authResponse
    }

    override suspend fun createNewEnquiry(enquiry: NewEnquiryHolder) {
        KtorServerConnector.createEnquiry(enquiry)
    }

    override suspend fun createNewEmployee(newUser: NewUser) {
        KtorServerConnector.createEmployee(newUser)
    }
}