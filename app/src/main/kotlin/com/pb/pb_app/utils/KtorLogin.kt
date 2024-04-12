package com.pb.pb_app.utils

import com.pb.pb_app.utils.models.Credentials
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.Token
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


const val PORT = 8080;
const val HOST = "10.0.2.2"

suspend fun ktorLogin(credentials: Credentials): Resource<Token> {

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

    return if (response.status.isSuccess()) Resource.Success(response.body<Token>()) else Resource.Failure("Couldn't log in")
}