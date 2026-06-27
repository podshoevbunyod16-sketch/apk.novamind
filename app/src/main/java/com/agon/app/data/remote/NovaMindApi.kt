package com.agon.app.data.remote

import android.content.Context
import android.net.Uri
import com.agon.app.data.model.AdminLoginRequest
import com.agon.app.data.model.AdminLoginResponse
import com.agon.app.data.model.AdminSettingsRequest
import com.agon.app.data.model.AdminStatsResponse
import com.agon.app.data.model.AutoSearchRequest
import com.agon.app.data.model.AutoSearchResponse
import com.agon.app.data.model.CommandRequest
import com.agon.app.data.model.CommandResponse
import com.agon.app.data.model.ModelsListResponse
import com.agon.app.data.model.SendRequest
import com.agon.app.data.model.SendResponse
import com.agon.app.data.model.SwitchResponse
import com.agon.app.data.model.WebSearchRequest
import com.agon.app.data.model.WebSearchResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NovaMindApi(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .build()
    }

    private fun base(url: String): String {
        return url.trim().trimEnd('/').ifEmpty { "http://10.0.2.2:5000" }
    }

    private suspend fun postJson(path: String, bodyJson: String, serverUrl: String): String =
        suspendCoroutine { continuation ->
            val request = Request.Builder()
                .url("${base(serverUrl)}$path")
                .post(bodyJson.toRequestBody("application/json; charset=utf-8".toMediaType()))
                .header("Accept", "application/json")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {
                    val body = response.body?.string()
                    if (response.isSuccessful) {
                        continuation.resume(body ?: "{}")
                    } else {
                        continuation.resumeWithException(IOException("HTTP ${response.code}: ${body ?: ""}"))
                    }
                }
            })
        }

    private suspend fun get(path: String, serverUrl: String): String = suspendCoroutine { continuation ->
        val request = Request.Builder()
            .url("${base(serverUrl)}$path")
            .header("Accept", "application/json")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val body = response.body?.string()
                if (response.isSuccessful) {
                    continuation.resume(body ?: "{}")
                } else {
                    continuation.resumeWithException(IOException("HTTP ${response.code}: ${body ?: ""}"))
                }
            }
        })
    }

    suspend fun sendMessage(message: String, reasoning: Boolean, serverUrl: String): SendResponse {
        return try {
            val body = json.encodeToString(SendRequest.serializer(), SendRequest(message, reasoning))
            val resp = postJson("/send", body, serverUrl)
            json.decodeFromString(SendResponse.serializer(), resp)
        } catch (e: Exception) {
            SendResponse(error = e.message)
        }
    }

    suspend fun sendCommand(command: String, serverUrl: String): CommandResponse {
        if (serverUrl.isBlank()) return CommandResponse()
        return try {
            val body = json.encodeToString(CommandRequest.serializer(), CommandRequest(command))
            val resp = postJson("/command", body, serverUrl)
            json.decodeFromString(CommandResponse.serializer(), resp)
        } catch (e: Exception) {
            CommandResponse(error = e.message)
        }
    }

    suspend fun webSearch(query: String, serverUrl: String): WebSearchResponse {
        return try {
            val body = json.encodeToString(WebSearchRequest.serializer(), WebSearchRequest(query))
            val resp = postJson("/api/web_search_groq", body, serverUrl)
            json.decodeFromString(WebSearchResponse.serializer(), resp)
        } catch (e: Exception) {
            WebSearchResponse(error = e.message)
        }
    }

    suspend fun autoSearch(message: String, serverUrl: String): AutoSearchResponse {
        return try {
            val body = json.encodeToString(AutoSearchRequest.serializer(), AutoSearchRequest(message))
            val resp = postJson("/api/auto_search", body, serverUrl)
            json.decodeFromString(AutoSearchResponse.serializer(), resp)
        } catch (e: Exception) {
            AutoSearchResponse(error = e.message)
        }
    }

    suspend fun uploadImage(uri: Uri, description: String, serverUrl: String): CommandResponse {
        return uploadFile(uri, description, serverUrl, "image", "upload_image")
    }

    suspend fun uploadDocument(uri: Uri, description: String, serverUrl: String): CommandResponse {
        return uploadFile(uri, description, serverUrl, "file", "upload_file")
    }

    private suspend fun uploadFile(
        uri: Uri,
        description: String,
        serverUrl: String,
        partName: String,
        endpoint: String,
    ): CommandResponse {
        return try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IOException("Cannot read file")
            val name = getFileName(uri)
            val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"

            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(partName, name, bytes.toRequestBody(mime.toMediaType()))
                .addFormDataPart("description", description)
                .build()

            val request = Request.Builder()
                .url("${base(serverUrl)}/$endpoint")
                .post(body)
                .header("Accept", "application/json")
                .build()

            val resp = suspendCoroutine { continuation ->
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resumeWithException(e)
                    }

                    override fun onResponse(call: Call, response: okhttp3.Response) {
                        val bodyString = response.body?.string()
                        if (response.isSuccessful) {
                            continuation.resume(bodyString ?: "{}")
                        } else {
                            continuation.resumeWithException(IOException("HTTP ${response.code}: ${bodyString ?: ""}"))
                        }
                    }
                })
            }
            json.decodeFromString(CommandResponse.serializer(), resp)
        } catch (e: Exception) {
            CommandResponse(error = e.message)
        }
    }

    private fun getFileName(uri: Uri): String {
        var result = "file"
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) result = cursor.getString(idx) ?: result
                }
            }
        }
        return result
    }

    suspend fun listModels(serverUrl: String): ModelsListResponse {
        return try {
            val resp = get("/models_list", serverUrl)
            json.decodeFromString(ModelsListResponse.serializer(), resp)
        } catch (e: Exception) {
            ModelsListResponse()
        }
    }

    suspend fun switchModel(modelId: String, serverUrl: String): SwitchResponse {
        return try {
            val encoded = URLEncoder.encode(modelId, "UTF-8")
            val resp = get("/switch_model?model_id=$encoded", serverUrl)
            json.decodeFromString(SwitchResponse.serializer(), resp)
        } catch (e: Exception) {
            SwitchResponse(error = e.message)
        }
    }

    suspend fun adminLogin(code: String, serverUrl: String): AdminLoginResponse {
        return try {
            val body = json.encodeToString(AdminLoginRequest.serializer(), AdminLoginRequest("admin", code))
            val resp = postJson("/api/admin/login", body, serverUrl)
            json.decodeFromString(AdminLoginResponse.serializer(), resp)
        } catch (e: Exception) {
            // Local fallback: default admin code matches the backend default
            if (code == "007") AdminLoginResponse(success = true, username = "admin")
            else AdminLoginResponse(success = false, error = e.message)
        }
    }

    suspend fun adminStats(serverUrl: String): AdminStatsResponse {
        return try {
            val resp = get("/api/admin/stats", serverUrl)
            json.decodeFromString(AdminStatsResponse.serializer(), resp)
        } catch (e: Exception) {
            AdminStatsResponse()
        }
    }

    suspend fun adminSaveSettings(settings: AdminSettingsRequest, serverUrl: String): Boolean {
        return try {
            val body = json.encodeToString(AdminSettingsRequest.serializer(), settings)
            val resp = postJson("/api/admin/settings", body, serverUrl)
            val obj = json.parseToJsonElement(resp).jsonObject
            obj["success"]?.let { json.decodeFromJsonElement<Boolean>(it) } ?: false
        } catch (e: Exception) {
            false
        }
    }
}
