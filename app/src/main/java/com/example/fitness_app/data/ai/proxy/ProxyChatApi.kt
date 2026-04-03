package com.example.fitness_app.data.ai.proxy

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import android.content.Context
import android.net.Uri
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProxyChatApi(
    private val client: OkHttpClient,
    private val gson: Gson,
    private val baseUrl: String
) {

    fun sendTextMessage(message: String, mode: String, goal: String?): ProxyChatResponse {
        val requestModel = ProxyChatRequest(message = message, mode = mode, goal = goal)
        val jsonBody = gson.toJson(requestModel)

        val request = Request.Builder()
            .url("$baseUrl/api/chat/text")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string()
                ?: error("Proxy response body is empty")

            return gson.fromJson(body, ProxyChatResponse::class.java)
        }
    }



    fun sendImageMessage(
        context: Context,
        imageUri: Uri,
        message: String,
        mode: String,
        goal: String?
    ): ProxyImageChatResponse {
        val file = copyUriToTempFile(context, imageUri)
        val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("message", message)
            .addFormDataPart("mode", mode)
            .addFormDataPart("goal", goal ?: "")
            .addFormDataPart(
                "image",
                file.name,
                file.asRequestBody(mimeType.toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url("$baseUrl/api/chat/image")
            .addHeader("Accept", "application/json")
            .post(multipartBody)
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string()
                ?: error("Proxy image response body is empty")

            return gson.fromJson(body, ProxyImageChatResponse::class.java)
        }
    }


    private fun copyUriToTempFile(context: Context, uri: Uri): File {
        val tempFile = File.createTempFile("chat_image_", ".jpg", context.cacheDir)

        context.contentResolver.openInputStream(uri).use { inputStream ->
            requireNotNull(inputStream) { "Cannot open input stream from uri" }

            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return tempFile
    }

}