package com.pb.pb_app.utils

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import com.pb.pb_app.R
import com.pb.pb_app.utils.interfaces.Repository
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.employees.GenericEmployee

private const val TAG = "Repository"

class RepositoryImpl(context: Context) : Repository {

    private val sharedPreferencesName = context.getString(R.string.shared_preferences_name)

    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Activity.MODE_PRIVATE)

    private val usernameTag = context.getString(R.string.username)


    override fun getLoggedInUser(): String? {
        return sharedPreferences.getString(usernameTag, null)
    }

    override suspend fun getEmployeeByUsername(username: String): Resource.Success<GenericEmployee> {
        return KtorServerConnector.getEmployee(username)
    }

    override fun logout() {
        sharedPreferences.edit {
            putString(usernameTag, null)
        }
    }

    override suspend fun authenticateUser(username: String, password: String): Boolean {
        val authResponse = KtorServerConnector.authenticate(username, password)
        if (authResponse) {
            sharedPreferences.edit {
                putString(usernameTag, username)
            }
        }
        return authResponse
    }

    override suspend fun createNewEnquiry(name: String, description: String) {
        KtorServerConnector.createEnquiry(name, description)
    }
}