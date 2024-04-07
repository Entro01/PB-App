package com.pb.pb_app.utils

import com.pb.pb_app.utils.models.employees.Admin
import com.pb.pb_app.utils.models.employees.Coordinator
import com.pb.pb_app.utils.models.employees.Freelancer
import com.pb.pb_app.utils.models.employees.GenericEmployee
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

object EmployeeSerializer : JsonContentPolymorphicSerializer<GenericEmployee>(GenericEmployee::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<GenericEmployee> {
        val role = (element as JsonObject)["role"]?.jsonPrimitive?.content ?: throw NullPointerException("No role object found")

        return when (role) {
            "Admin" -> Admin.serializer()
            "Coordinator" -> Coordinator.serializer()
            "Freelancer" -> Freelancer.serializer()
            else -> throw IllegalArgumentException("Illegal object retrieved from the server")
        }
    }
}