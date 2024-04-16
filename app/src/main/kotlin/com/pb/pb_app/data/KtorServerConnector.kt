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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json

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

            defaultRequest {
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

        val internalKtorClient = HttpClient(Android) {
            defaultRequest {
                contentType(ContentType.Application.Json)
                url {
                    protocol = URLProtocol.HTTP
                    host = HOST
                    port = PORT
                    path("login")
                }
            }
            install(ContentNegotiation) {
                json()
            }
        }

        val response = internalKtorClient.post {
            setBody(credentials)
        }

        if (response.status.isSuccess()) {
            val token = response.body<Token>()
            implementKtorClient(token)
            return Resource.Success(token)
        }

        return Resource.Failure("Authentication failed")
    }

    // GET REQUESTS
    suspend fun getSelf(): Resource<BaseEmployee> {
        val response = ktorClient.get {
            url {
                path("employees", "self")
            }
        }

        return if (response.status.isSuccess()) {
            Resource.Success(response.body())
        } else {
            Resource.Failure(response.status.description)
        }
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

        return if (response.status.isSuccess()) {
            Resource.Success(response.body())
        } else {
            Resource.Failure(response.status.description)
        }
    }

    suspend fun getUrgentInquiries(): Resource<List<Inquiry>> {
        val response = ktorClient.get {
            contentType(ContentType.Application.Json)
            url.path("inquiries", "urgent")
        }
        Log.e(TAG, "getUrgentInquiries: ${response.status}")

        return if (response.status.isSuccess()) {
            Resource.Success(response.body<List<Inquiry>>())
        } else {
            Resource.Failure(response.status.description)
        }
    }

    suspend fun getMiscInquiries(): Resource<List<Inquiry>> {
        val response = ktorClient.get {
            contentType(ContentType.Application.Json)
            url.path("inquiries", "misc")
        }

        return if (response.status.isSuccess()) {
            Resource.Success(response.body<List<Inquiry>>())
        } else {
            Resource.Failure(response.status.description)
        }
    }

    suspend fun getInquiriesByStatus(status: String): Resource<List<Inquiry>> {
        val response = ktorClient.get {
            contentType(ContentType.Application.Json)
            url.path("inquiries")
            if (status.isNotEmpty()) url.parameters["status"] = status
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
            if (employeeId != null) url.parameters["employeeId"] = employeeId
        }

        return if (response.status.isSuccess()) {
            Resource.Success(response.body())
        } else {
            Resource.Failure(response.status.description)
        }
    }

    // POST REQUESTS
    suspend fun createEmployee(newEmployee: NewEmployee): Boolean {
        return ktorClient.post {
            url.path("employees", "create")
            setBody(newEmployee)
        }.status.isSuccess()
    }

    suspend fun performAction(updateAction: InquiryUpdateAction): Boolean {
        val response = ktorClient.post {
            url.path("inquiries", "action")
            setBody(updateAction)
        }

        return response.status.isSuccess()
    }


    suspend fun setSelfStatus(status: Boolean): Boolean {
        return ktorClient.post {
            contentType(ContentType.Text.Plain)
            url.path("status")
            setBody(status)
        }.status.isSuccess()
    }
}