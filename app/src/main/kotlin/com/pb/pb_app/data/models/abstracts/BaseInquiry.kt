package com.pb.pb_app.data.models.abstracts

abstract class BaseInquiry {
    abstract val name: String
    abstract val description: String
    abstract val creationTime: Long
    abstract val deadlineMillis: Long
    abstract val service: String
    abstract val contactNumber: String
    abstract val deliveryArea: String
    abstract val reference: Boolean
}