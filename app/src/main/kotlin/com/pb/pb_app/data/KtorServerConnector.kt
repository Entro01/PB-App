package com.pb.pb_app.data


import android.util.Log
import com.pb.pb_app.data.Constants.HOST
import com.pb.pb_app.data.Constants.PORT
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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
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

    private lateinit var ktorClient: HttpClient

    private fun implementKtorClient(token: Token) {
        ktorClient = HttpClient(Android) {
            Auth {
                bearer {
                    loadTokens {
                        BearerTokens(token.secret, token.secret)
                    }
                }
            }

            install(DefaultRequest) {
                contentType(ContentType.Application.Json)
                url {
                    protocol = URLProtocol.HTTP
                    host = HOST
                    port = PORT
                }
            }

            install(ContentNegotiation) {
                json()
            }
        }
    }

    fun loadToken(tokenRetriever: () -> Token) {
        implementKtorClient(tokenRetriever())
    }

    suspend fun login(credentials: Credentials): Resource<Token> {

        val ktorClient = HttpClient(Android) {
            install(DefaultRequest) {
                contentType(ContentType.Application.Json)
                url {
                    protocol = URLProtocol.HTTP
                    host = HOST
                    port = PORT
                    path("login")
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    prettyPrint = true
                })
            }
        }

        val response = ktorClient.post {
            setBody(credentials)
        }

        val token = response.body<Token>()
        implementKtorClient(token)

        return if (response.status.isSuccess()) Resource.Success(token) else Resource.Failure("Authentication failed")
    }

    // GET REQUESTS
    suspend fun getSelf(): Resource<BaseEmployee> {
        val response = ktorClient.get {
            url {
                path("employees", "self")
            }
        }

        return Resource.Success(response.body())
    }

    suspend fun getCoordinators(): Resource<List<Coordinator>> {
        val response = ktorClient.get {
            url.path("employees")
            url.parameters["role"] = "Coordinator"
        }

        return if (response.status.isSuccess()) {
            Resource.Success(response.body())
        } else {
            Resource.Failure(response.status.description)
        }
    }

    suspend fun getFreelancers(): Resource<List<Freelancer>> {
        val response = ktorClient.get {
            url.path("employees")
            url.parameters["role"] = "Freelancer"
        }
        Log.e(TAG, "getFreelancers: ${response.status.value}")

        return if (response.status.isSuccess()) {
            Resource.Success(response.body())
        } else {
            Resource.Failure(response.status.description)
        }
    }

    suspend fun getInquiriesByStatus(vararg inquiryStatuses: String): Resource<List<Inquiry>> {
        val statuses = inquiryStatuses.joinToString(",") { it }

        val response = ktorClient.get {
            contentType(ContentType.Application.Json)
            url.path("inquiries")
            if (statuses.isNotEmpty()) url.parameters["status"] = statuses
        }

        return if (response.status.isSuccess()) {
            Resource.Success(response.body())
        } else {
            Resource.Failure(response.status.description)
        }
    }

    suspend fun getEmployeeById(employeeId: String?): Resource<BaseEmployee> {
        val response = ktorClient.get {
            url.path("employees")
            if (employeeId != null) url.parameters["employee_id"] = employeeId
        }

        return if (response.status.isSuccess()) {
            Resource.Success(response.body())
        } else {
            Resource.Failure(response.status.description)
        }
    }

    // POST REQUESTS
    suspend fun createEnquiry(enquiry: NewInquiry): Boolean {
        val result = ktorClient.post {
            url.path("inquiries", "create")
            setBody(enquiry)
        }
        Log.e(TAG, "createEnquiry: ${result.status}")
        return result.status.isSuccess()
    }

    suspend fun createEmployee(newEmployee: NewEmployee): Boolean {
        return ktorClient.post {
            url.path("employees", "create")
            setBody(newEmployee)
        }.status.isSuccess()
    }

    suspend fun performInquiryUpdateAction(updateAction: InquiryUpdateAction): Boolean {
        val response = ktorClient.post {
            url.path("inquiries", "update-inquiry")
            setBody(updateAction)
        }

        return response.status.isSuccess()
    }


    suspend fun setEmployeeStatus(employeeId: String, status: Boolean): Boolean {
        return ktorClient.post {
            contentType(ContentType.Text.Plain)
            url.path("employee-status", "update")
            url.parameters["employee_id"] = employeeId
            setBody(status)
        }.status.isSuccess()
    }
}