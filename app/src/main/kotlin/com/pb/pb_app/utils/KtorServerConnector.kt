package com.pb.pb_app.utils

import android.provider.Telephony.Carriers.PORT
import android.util.Log
import com.pb.pb_app.utils.models.Credentials
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.employees.Coordinator
import com.pb.pb_app.utils.models.employees.Freelancer
import com.pb.pb_app.utils.models.employees.GenericEmployee
import com.pb.pb_app.utils.models.employees.NewUser
import com.pb.pb_app.utils.models.projects.Enquiry
import com.pb.pb_app.utils.models.projects.EnquiryUpdateAction
import com.pb.pb_app.utils.models.projects.EnquiryStatus
import com.pb.pb_app.utils.models.projects.NewEnquiryHolder
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TAG = "KtorServerConnector"

object KtorServerConnector {
    private const val HOST = "174a-103-248-172-84.ngrok-free.app"

    private val ktorClient = HttpClient(Android) {
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
            url {
                protocol = URLProtocol.HTTPS
                host = HOST
            }
        }
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                prettyPrint = true
            })
        }
    }

    // GET REQUESTS
    suspend fun getCoordinators(): Resource<List<Coordinator>> {
        val response = ktorClient.get {
            url.path("print-employee-details")
            url.parameters["role_or_employee_id"] = "Coordinator"
        }

        return if (response.status.isSuccess()) {
            Resource.Success(response.body())
        } else {
            Resource.Failure(response.status.description)
        }
    }

    suspend fun getFreelancers(): Resource<List<Freelancer>> {
        val response = ktorClient.get {
            url.path("print-employee-details")
            url.parameters["role_or_employee_id"] = "Freelancer"
        }

        return if (response.status.isSuccess()) {
            Resource.Success(response.body())
        } else {
            Resource.Failure(response.status.description)
        }
    }

    suspend fun getEnquiriesByUsernameAndStatus(username: String, vararg enquiryStatuses: EnquiryStatus): Resource<List<Enquiry>> {
        val statuses = StringBuilder()

        for (each in enquiryStatuses) {
            statuses.append(each.name)
        }

        val response = ktorClient.get {
            contentType(ContentType.Application.Json)
            url.path("print-enquiry-details")
            url.parameters["username"] = username
            if (statuses.isNotEmpty()) url.parameters["status"] = statuses.toString()
        }

        return if (response.status.isSuccess()) {
            Resource.Success(response.body())
        } else {
            Resource.Failure(response.status.description)
        }
    }

    suspend fun getEmployeeByID(username: String): Resource<GenericEmployee> {
        val response = ktorClient.get {
            url.path("print-employee-details")
            url.parameters["arg"] = username
        }

        return if (response.status.isSuccess()) {
            Resource.Success(response.body())
        } else {
            Resource.Failure(response.status.description)
        }
    }

    // POST REQUESTS

    suspend fun authenticate(credentials: Credentials): Boolean {
        val response = ktorClient.post {
            url.path("login/")
            setBody(credentials)

            Log.e(TAG, "authenticate: ${this.body}", )
        }

        return response.status.isSuccess()
    }

    suspend fun updateEnquiryStatus(inquiryID: String, updateAction: EnquiryUpdateAction): Boolean {


        return ktorClient.post {
            url.path("update-enquiry-status/")
            url.parameters["inquiry_id"] = inquiryID
            when (updateAction) {
                is EnquiryUpdateAction.CoordinatorRejected -> {

                }
                is EnquiryUpdateAction.CoordinatorRequested -> TODO()
                is EnquiryUpdateAction.CoordinatorTimeUp -> TODO()
                is EnquiryUpdateAction.CoordinatorsAccepted -> TODO()
                is EnquiryUpdateAction.FreelancersAccepted -> TODO()
                is EnquiryUpdateAction.FreelancersFinalized -> TODO()
                is EnquiryUpdateAction.FreelancersRejected -> TODO()
                is EnquiryUpdateAction.FreelancersRequested -> TODO()
                is EnquiryUpdateAction.FreelancersTimeUp -> TODO()
            }
        }.status.isSuccess()
    }

    suspend fun createEnquiry(enquiry: NewEnquiryHolder): Boolean {
        return ktorClient.post {
            url.path("status/")
            setBody(enquiry)
        }.status.isSuccess()
    }

    suspend fun createEmployee(newUser: NewUser): Boolean {
        return ktorClient.post {
            url.path("status/")
            setBody(newUser)
        }.status.isSuccess()
    }


    suspend fun setOnlineStatus(username: String, status: Boolean): Boolean {
        return ktorClient.post {
            contentType(ContentType.Text.Plain)
            url.path("employee-status-update")
            url.parameters["employee_id"] = username
            setBody(status)
        }.status.isSuccess()
    }
}

