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
        val id = (element as JsonObject)["employee_id"]?.jsonPrimitive?.content ?: "NONE"
        return if (id.startsWith("PB-AM")) {
            Admin.serializer()
        } else if (id.startsWith("PB-PC")) {
            Coordinator.serializer()
        } else if (id.startsWith("PB-FR")) {
            Freelancer.serializer()
        } else {
            throw NullPointerException("IDK WHAT JUST HAPPENED")
        }
    }
}