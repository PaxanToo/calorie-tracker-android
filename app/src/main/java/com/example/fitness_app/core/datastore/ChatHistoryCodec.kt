package com.example.fitness_app.core.datastore

import org.json.JSONArray
import org.json.JSONObject

data class SavedChatMessage(
    val text: String,
    val isUser: Boolean,
    val time: Long
)

fun encodeChatHistory(messages: List<SavedChatMessage>): String {
    val jsonArray = JSONArray()

    messages.forEach { message ->
        val jsonObject = JSONObject()
            .put("text", message.text)
            .put("isUser", message.isUser)
            .put("time", message.time)

        jsonArray.put(jsonObject)
    }

    return jsonArray.toString()
}

fun decodeChatHistory(raw: String): List<SavedChatMessage> {
    if (raw.isBlank()) return emptyList()

    return runCatching {
        val jsonArray = JSONArray(raw)

        buildList {
            for (index in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(index)

                add(
                    SavedChatMessage(
                        text = jsonObject.optString("text"),
                        isUser = jsonObject.optBoolean("isUser"),
                        time = jsonObject.optLong("time")
                    )
                )
            }
        }
    }.getOrDefault(emptyList())
}