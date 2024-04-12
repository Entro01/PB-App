package com.pb.pb_app.utils.serializers

import com.pb.pb_app.utils.models.employees.Admin
import com.pb.pb_app.utils.models.employees.BaseEmployee
import com.pb.pb_app.utils.models.employees.Coordinator
import com.pb.pb_app.utils.models.employees.Freelancer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

object  EmployeeSerializer : JsonContentPolymorphicSerializer<BaseEmployee>(BaseEmployee::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<BaseEmployee> {
        val role = (element as JsonObject)["role"]?.jsonPrimitive?.content ?: throw NullPointerException("No role object found")

        return when (role) {
            "ADMIN" -> Admin.serializer()
            "COORDINATOR" -> Coordinator.serializer()
            "FREELANCER" -> Freelancer.serializer()
            else -> throw IllegalArgumentException("Illegal object retrieved from the server")
        }
    }
}