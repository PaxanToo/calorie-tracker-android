package com.example.fitness_app.data.ai.proxy

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.math.max
import kotlin.math.roundToInt

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
        val file = copyUriToCompressedTempFile(context, imageUri)

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("message", message)
            .addFormDataPart("mode", mode)
            .addFormDataPart("goal", goal ?: "")
            .addFormDataPart(
                "image",
                file.name,
                file.asRequestBody("image/jpeg".toMediaType())
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

    private fun copyUriToCompressedTempFile(context: Context, uri: Uri): File {
        val originalBytes = context.contentResolver.openInputStream(uri)?.use { input ->
            input.readBytes()
        } ?: error("Cannot open input stream from uri")

        val originalBitmap = BitmapFactory.decodeByteArray(
            originalBytes,
            0,
            originalBytes.size
        ) ?: error("Cannot decode bitmap from uri")

        val resizedBitmap = resizeBitmapIfNeeded(
            bitmap = originalBitmap,
            maxSide = 1280
        )

        val tempFile = File.createTempFile("chat_image_", ".jpg", context.cacheDir)

        tempFile.outputStream().use { outputStream ->
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        }

        if (resizedBitmap != originalBitmap) {
            originalBitmap.recycle()
        }
        resizedBitmap.recycle()

        return tempFile
    }

    private fun resizeBitmapIfNeeded(
        bitmap: Bitmap,
        maxSide: Int
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val currentMaxSide = max(width, height)

        if (currentMaxSide <= maxSide) {
            return bitmap
        }

        val scale = maxSide.toFloat() / currentMaxSide.toFloat()
        val newWidth = (width * scale).roundToInt()
        val newHeight = (height * scale).roundToInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}