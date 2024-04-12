package com.pb.pb_app.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.pb.pb_app.R
import com.pb.pb_app.data.Constants.InquiryStatusLabels.COORDINATOR_REQUESTED
import com.pb.pb_app.data.Constants.InquiryStatusLabels.FREELANCER_ACCEPTED
import com.pb.pb_app.data.Constants.InquiryStatusLabels.FREELANCER_ASSIGNED
import com.pb.pb_app.data.Constants.InquiryStatusLabels.FREELANCER_REQUESTED
import com.pb.pb_app.data.Constants.InquiryStatusLabels.UNASSIGNED
import com.pb.pb_app.data.enums.EmployeeRole
import com.pb.pb_app.data.enums.EmployeeRole.Companion.fromEmployeeId
import com.pb.pb_app.data.models.Credentials
import com.pb.pb_app.data.models.Resource
import com.pb.pb_app.data.models.Token
import com.pb.pb_app.data.models.abstracts.BaseEmployee
import com.pb.pb_app.data.models.employees.Coordinator
import com.pb.pb_app.data.models.employees.Freelancer
import com.pb.pb_app.data.models.employees.NewEmployee
import com.pb.pb_app.data.models.inquiries.Inquiry
import com.pb.pb_app.data.models.inquiries.InquiryUpdateAction
import com.pb.pb_app.data.models.inquiries.NewInquiry

private const val TAG = "Repository"


class RepositoryImpl(context: Context) {

    private val sharedPreferencesName = context.getString(R.string.shared_preferences_name)

    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Activity.MODE_PRIVATE)

    private val credentialsKey = context.getString(R.string.credentials)
    
    init {
        val tokenSecret = retrieveCredentials()?.elementAt(1)
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
        return KtorServerConnector.getFreelancers()
    }

    private fun getLoggedInUserRole(): EmployeeRole {
        return retrieveCredentials()?.elementAt(0)?.fromEmployeeId() ?: throw IllegalStateException("User not logged in")
    }

    suspend fun getCoordinators(): Resource<List<Coordinator>> {
        val loggedInUserRole = getLoggedInUserRole()
        if (loggedInUserRole != EmployeeRole.ADMIN) {
            throw IllegalStateException("Only Admins can retrieve freelancer data")
        }
        return KtorServerConnector.getCoordinators()
    }

    suspend fun getUrgentInquiries(): Resource<List<Inquiry>> {
        return when (retrieveCredentials()?.elementAt(0)?.fromEmployeeId()) {
            EmployeeRole.ADMIN -> KtorServerConnector.getInquiriesByStatus(UNASSIGNED)
            EmployeeRole.COORDINATOR -> KtorServerConnector.getInquiriesByStatus(COORDINATOR_REQUESTED)
            EmployeeRole.FREELANCER -> KtorServerConnector.getInquiriesByStatus(FREELANCER_REQUESTED)
            null -> throw IllegalStateException("User not logged in")
        }
    }

    suspend fun getMiscInquiries(): Resource<List<Inquiry>> {
        return when (retrieveCredentials()?.elementAt(0)?.fromEmployeeId()) {
            EmployeeRole.ADMIN -> KtorServerConnector.getInquiriesByStatus()
            EmployeeRole.COORDINATOR -> KtorServerConnector.getInquiriesByStatus(FREELANCER_REQUESTED, FREELANCER_ACCEPTED, FREELANCER_ASSIGNED)
            EmployeeRole.FREELANCER -> KtorServerConnector.getInquiriesByStatus(FREELANCER_ASSIGNED)
            null -> throw IllegalStateException("User not logged in")
        }
    }

    suspend fun createNewInquiry(enquiry: NewInquiry): Boolean {
        val loggedInUserRole = getLoggedInUserRole()
        if (loggedInUserRole != EmployeeRole.ADMIN) throw IllegalStateException("Only admins can create new inquiries")
        val result = KtorServerConnector.createEnquiry(enquiry)
        Log.e(TAG, "createNewInquiry: $result")
        return result
    }

    suspend fun createNewEmployee(newEmployee: NewEmployee): Boolean {
        val loggedInUserRole = getLoggedInUserRole()
        if (loggedInUserRole != EmployeeRole.ADMIN) throw IllegalStateException("Only admins can create new employees")
        return KtorServerConnector.createEmployee(newEmployee)
    }

    suspend fun updateInquiryStatus(updateAction: InquiryUpdateAction) {
        KtorServerConnector.performInquiryUpdateAction(updateAction)

    }

    suspend fun setEmployeeStatus(employeeId: String, status: Boolean) {
        KtorServerConnector.setEmployeeStatus(employeeId = employeeId, status)
    }
}