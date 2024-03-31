package com.pb.pb_app.utils

import android.util.Log
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.employees.Coordinator
import com.pb.pb_app.utils.models.employees.Freelancer
import com.pb.pb_app.utils.models.employees.GenericEmployee
import com.pb.pb_app.utils.models.projects.Enquiry
import com.pb.pb_app.utils.models.projects.EnquiryStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private const val TAG = "KtorServerConnector"

const val BASE_URL = "https://morning-sunset-da33ae42.zvgz4d.on-acorn.io/"

object KtorServerConnector {
    private val ktorClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }


    suspend fun getCoordinators(): Resource.Success<List<Coordinator>> {
        val arg = "Coordinator"
        var url = "${BASE_URL}print-employee-details/"
        url += "?arg=$arg"
        return Resource.Success(ktorClient.get(url).body())
    }


    suspend fun getFreelancers(): Resource.Success<List<Freelancer>> {
        val arg = "Freelancer"
        var url = "${BASE_URL}print-employee-details/"
        url += "?arg=$arg"
        return Resource.Success(ktorClient.get(url).body())
    }

    suspend fun getEmployee(username: String): Resource.Success<GenericEmployee> {
        return Resource.Success(ktorClient.get("${BASE_URL}print-employee-details/?arg=${username}").body<List<GenericEmployee>>()[0])
    }

    suspend fun authenticate(username: String, password: String): Boolean {
        val result = ktorClient.post("${BASE_URL}login/") {
            contentType(ContentType.Application.Json)
            setBody(
                JsonObject(
                    mapOf(
                        "employee_id" to JsonPrimitive(username), "password" to JsonPrimitive(password)
                    )
                )
            )
        }
        return result.status.isSuccess()
    }

    suspend fun changeProjectStatus(enquiryID: Int, employeeID: String, targetStatus: EnquiryStatus) {
        ktorClient.post("${BASE_URL}update-enquiry-status/") {
            contentType(ContentType.Application.Json)
            setBody(
                JsonObject(
                    mapOf(
                        "enquiry_id" to JsonPrimitive(enquiryID),
                        "employee_id" to JsonPrimitive(employeeID),
                        "stats" to JsonPrimitive(targetStatus.statusCode)
                    )
                )
            )
        }
    }

    suspend fun createEnquiry(name: String, description: String) {
        ktorClient.post("${BASE_URL}enquiry-create") {
            contentType(ContentType.Application.Json)
            setBody {
                JsonObject(
                    mapOf(
                        //
                    )
                )
            }
        }
    }

    suspend fun getProjectsByUsernameAndStatus(username: String, enquiryStatus: EnquiryStatus? = null): Resource.Success<List<Enquiry>> {
        val projectStatusArg = if (enquiryStatus != null) "&arg2=${enquiryStatus.statusCode}" else ""
        val url = "${BASE_URL}print-enquiry-details/?arg1=$username$projectStatusArg"
        Log.e(TAG, "getProjectsByUsernameAndStatus: $url")
        return Resource.Success(ktorClient.get(url).body())
    }

    suspend fun setOnlineStatus(username: String, status: Boolean) {
        ktorClient.post("${BASE_URL}employee-status-update/") {
            contentType(ContentType.Application.Json)
            setBody(
                JsonObject(
                    mapOf(
                        "employee_id" to JsonPrimitive(username), "is_online" to JsonPrimitive(status)
                    )
                )
            )
        }
    }
}

