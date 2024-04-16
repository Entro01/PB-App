package com.pb.pb_app.data

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.pb.pb_app.R
import com.pb.pb_app.data.enums.EmployeeRole
import com.pb.pb_app.data.enums.EmployeeRole.Companion.parseEmployeeId
import com.pb.pb_app.data.models.Credentials
import com.pb.pb_app.data.models.Resource
import com.pb.pb_app.data.models.Token
import com.pb.pb_app.data.models.abstracts.BaseEmployee
import com.pb.pb_app.data.models.employees.Coordinator
import com.pb.pb_app.data.models.employees.Freelancer
import com.pb.pb_app.data.models.employees.NewEmployee
import com.pb.pb_app.data.models.inquiries.Inquiry
import com.pb.pb_app.data.models.inquiries.InquiryUpdateAction

private const val TAG = "Repository"


class RepositoryImpl(context: Context) {

    private val sharedPreferencesName = context.getString(R.string.shared_preferences_name)

    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Activity.MODE_PRIVATE)

    private val secretKey = context.getString(R.string.secret_key)
    private val usernameKey = context.getString(R.string.username_key)

    init {
        val tokenSecret = retrieveCredentials()?.second
        if (tokenSecret != null) {
            KtorServerConnector.loadToken { Token(tokenSecret) }
        }
    }

    suspend fun login(username: String, password: String): Boolean {
        val authResponse = KtorServerConnector.login(Credentials(username, password))
        if (authResponse is Resource.Success) {
            putCredentials(username, authResponse.data.secret)
            return true
        }

        return false
    }

    suspend fun getSelf(): Resource<BaseEmployee> {
        return KtorServerConnector.getSelf()
    }

    suspend fun getEmployeeByUsername(username: String? = null): Resource<BaseEmployee> {
        return KtorServerConnector.getEmployeeById(username)
    }

    private fun putCredentials(username: String? = null, secret: String? = null) {
        if (username == null || secret == null) {
            sharedPreferences.edit().putStringSet(secretKey, null).apply()
            return
        }

        sharedPreferences.edit {
            putString(usernameKey, username)
            putString(secretKey, secret)
        }
    }

    fun retrieveCredentials(): Pair<String, String>? {
        val username = sharedPreferences.getString(usernameKey, null)
        val secret = sharedPreferences.getString(secretKey, null)

        if (username == null || secret == null) return null

        Log.e(TAG, "retrieveCredentials: $secret")

        return Pair(username, secret)
    }

    fun logout() {
        putCredentials()
    }

    suspend fun getFreelancers(): Resource<List<Freelancer>> {
        val loggedInUserRole = getLoggedInUserRole()
        if (loggedInUserRole != EmployeeRole.ADMIN && loggedInUserRole != EmployeeRole.COORDINATOR) {
            return Resource.Failure("Not logged in or freelancer is trying to attempt getting other freelancer data")
        }
        return KtorServerConnector.getFreelancers()
    }

    private fun getLoggedInUserRole(): EmployeeRole? {
        return runCatching {
            retrieveCredentials()?.first?.parseEmployeeId()
        }.getOrElse {
            return null
        }
    }

    suspend fun getCoordinators(): Resource<List<Coordinator>> {
        val loggedInUserRole = getLoggedInUserRole()
        if (loggedInUserRole != EmployeeRole.ADMIN) {
            Log.e(TAG, "getCoordinators: $loggedInUserRole")
            return Resource.Failure("User not logged in or not the correct role")
        }
        return KtorServerConnector.getCoordinators()
    }

    suspend fun getUrgentInquiries(): Resource<List<Inquiry>> {
        return KtorServerConnector.getUrgentInquiries()
    }

    suspend fun getMiscInquiries(): Resource<List<Inquiry>> {
        return KtorServerConnector.getMiscInquiries()
    }


    suspend fun createNewEmployee(newEmployee: NewEmployee): Boolean {
        val loggedInUserRole = getLoggedInUserRole()
        if (loggedInUserRole != EmployeeRole.ADMIN) throw IllegalStateException("Only admins can create new employees")
        return KtorServerConnector.createEmployee(newEmployee)
    }

    suspend fun updateInquiryStatus(updateAction: InquiryUpdateAction) {
        KtorServerConnector.performAction(updateAction)

    }

    suspend fun setSelfStatus(status: Boolean) {
        KtorServerConnector.setSelfStatus(status)
    }
}