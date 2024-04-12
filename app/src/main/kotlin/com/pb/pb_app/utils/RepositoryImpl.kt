package com.pb.pb_app.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.pb.pb_app.R
import com.pb.pb_app.utils.Constants.InquiryStatusLabels.COORDINATOR_REQUESTED
import com.pb.pb_app.utils.Constants.InquiryStatusLabels.FREELANCER_ACCEPTED
import com.pb.pb_app.utils.Constants.InquiryStatusLabels.FREELANCER_ASSIGNED
import com.pb.pb_app.utils.Constants.InquiryStatusLabels.FREELANCER_REQUESTED
import com.pb.pb_app.utils.Constants.InquiryStatusLabels.UNASSIGNED
import com.pb.pb_app.utils.models.Credentials
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.Token
import com.pb.pb_app.utils.models.employees.BaseEmployee
import com.pb.pb_app.utils.models.employees.Coordinator
import com.pb.pb_app.utils.models.employees.EmployeeRole
import com.pb.pb_app.utils.models.employees.EmployeeRole.Companion.fromEmployeeId
import com.pb.pb_app.utils.models.employees.Freelancer
import com.pb.pb_app.utils.models.employees.NewEmployee
import com.pb.pb_app.utils.models.projects.Inquiry
import com.pb.pb_app.utils.models.projects.InquiryUpdateAction
import com.pb.pb_app.utils.models.projects.NewInquiry

private const val TAG = "Repository"


class RepositoryImpl(context: Context) {

    private val sharedPreferencesName = context.getString(R.string.shared_preferences_name)

    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Activity.MODE_PRIVATE)

    private val credentialsKey = context.getString(R.string.credentials)

    private lateinit var ktorServerConnector: KtorServerConnector

    init {
        val tokenSecret = retrieveCredentials()?.elementAt(1)
        if (tokenSecret != null) {
            ktorServerConnector = KtorServerConnector(Token(tokenSecret))
        }
    }

    suspend fun authenticateUser(username: String, password: String): Boolean {
        val authResponse = ktorLogin(Credentials(username, password))
        if (authResponse is Resource.Success) {
            ktorServerConnector = KtorServerConnector(authResponse.data)
            putCredentials(username, authResponse.data.secret)
        }
        return authResponse is Resource.Success
    }

    suspend fun getSelf(): Resource<BaseEmployee> {
        return ktorServerConnector.getSelf()
    }

    suspend fun getEmployeeByUsername(username: String? = null): Resource<BaseEmployee> {
        return ktorServerConnector.getEmployeeById(username)
    }

    private fun putCredentials(username: String? = null, secret: String? = null) {
        if (username == null || secret == null) {
            sharedPreferences.edit().putStringSet(credentialsKey, null).apply()
            return
        }
        sharedPreferences.edit().putStringSet(credentialsKey, setOf(username, secret)).apply()
    }

    fun retrieveCredentials(): MutableSet<String>? {
        return sharedPreferences.getStringSet(credentialsKey, null)
    }

    fun logout() {
        putCredentials()
    }


    suspend fun getFreelancers(): Resource<List<Freelancer>> {
        val loggedInUserRole = getLoggedInUserRole()
        if (loggedInUserRole != EmployeeRole.ADMIN && loggedInUserRole != EmployeeRole.COORDINATOR) {
            throw IllegalStateException("Freelancers cannot retrieve freelancer data")
        }
        return ktorServerConnector.getFreelancers()
    }

    private fun getLoggedInUserRole(): EmployeeRole {
        return retrieveCredentials()?.elementAt(0)?.fromEmployeeId() ?: throw IllegalStateException("User not logged in")
    }

    suspend fun getCoordinators(): Resource<List<Coordinator>> {
        val loggedInUserRole = getLoggedInUserRole()
        if (loggedInUserRole != EmployeeRole.ADMIN) {
            throw IllegalStateException("Only Admins can retrieve freelancer data")
        }
        return ktorServerConnector.getCoordinators()
    }

    suspend fun getUrgentInquiries(): Resource<List<Inquiry>> {
        return when (retrieveCredentials()?.elementAt(0)?.fromEmployeeId()) {
            EmployeeRole.ADMIN -> ktorServerConnector.getInquiriesByStatus(UNASSIGNED)
            EmployeeRole.COORDINATOR -> ktorServerConnector.getInquiriesByStatus(COORDINATOR_REQUESTED)
            EmployeeRole.FREELANCER -> ktorServerConnector.getInquiriesByStatus(FREELANCER_REQUESTED)
            null -> throw IllegalStateException("User not logged in")
        }
    }

    suspend fun getMiscInquiries(): Resource<List<Inquiry>> {
        return when (retrieveCredentials()?.elementAt(0)?.fromEmployeeId()) {
            EmployeeRole.ADMIN -> ktorServerConnector.getInquiriesByStatus()
            EmployeeRole.COORDINATOR -> ktorServerConnector.getInquiriesByStatus(FREELANCER_REQUESTED, FREELANCER_ACCEPTED, FREELANCER_ASSIGNED)
            EmployeeRole.FREELANCER -> ktorServerConnector.getInquiriesByStatus(FREELANCER_ASSIGNED)
            null -> throw IllegalStateException("User not logged in")
        }
    }

    suspend fun createNewInquiry(enquiry: NewInquiry): Boolean {
        val loggedInUserRole = getLoggedInUserRole()
        if (loggedInUserRole != EmployeeRole.ADMIN) throw IllegalStateException("Only admins can create new inquiries")
        val result = ktorServerConnector.createEnquiry(enquiry)
        Log.e(TAG, "createNewInquiry: $result")
        return result
    }

    suspend fun createNewEmployee(newEmployee: NewEmployee): Boolean {
        val loggedInUserRole = getLoggedInUserRole()
        if (loggedInUserRole != EmployeeRole.ADMIN) throw IllegalStateException("Only admins can create new employees")
        return ktorServerConnector.createEmployee(newEmployee)
    }

    suspend fun updateInquiryStatus(updateAction: InquiryUpdateAction) {
        ktorServerConnector.performInquiryUpdateAction(updateAction)

    }

    suspend fun setEmployeeStatus(employeeId: String, status: Boolean) {
        ktorServerConnector.setEmployeeStatus(employeeId = employeeId, status)
    }
}