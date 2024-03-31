package com.pb.pb_app.utils.models

sealed class Resource<T>(open val data: T?) {
    class Success<T>(override val data: T) : Resource<T>(data)
    class Failure<T>(val message: String) : Resource<T>(null)
    class Loading<T> : Resource<T>(null)
}