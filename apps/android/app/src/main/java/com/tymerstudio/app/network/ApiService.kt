package com.tymerstudio.app.network

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.sse.EventSource
import okhttp3.sse.EventSources
import okhttp3.sse.EventSourceListener
import java.util.concurrent.TimeUnit

data class PromptRequest(
    val sessionID: String,
    val model: String?,
    val agent: String?,
    val parts: List<Map<String, Any>>
)

data class SessionResponse(
    val id: String,
    val title: String?,
    val time: Map<String, Long>?
)

class ApiService(private val baseUrl: String) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    suspend fun createSession(): Result<SessionResponse> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/v2/session")
                .post("{}".toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Failed to create session: ${response.code}"))
                }

                val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
                val session = gson.fromJson(body, SessionResponse::class.java)
                Result.success(session)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPrompt(
        sessionID: String,
        text: String,
        model: String? = null,
        agent: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val parts = listOf(
                mapOf("type" to "text", "text" to text)
            )

            val requestBody = PromptRequest(
                sessionID = sessionID,
                model = model,
                agent = agent,
                parts = parts
            )

            val request = Request.Builder()
                .url("$baseUrl/v2/session/prompt")
                .post(gson.toJson(requestBody).toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Failed to send prompt: ${response.code}"))
                }

                Result.success("Message sent")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun subscribeToEvents(sessionID: String): Flow<String> = flow {
        val request = Request.Builder()
            .url("$baseUrl/v2/event?sessionID=$sessionID")
            .build()

        val eventSource = EventSources.createFactory(client)
            .newEventSource(request, object : EventSourceListener() {
                override fun onEvent(
                    eventSource: EventSource,
                    id: String?,
                    type: String?,
                    data: String
                ) {
                    // Emit event data
                    // In production, parse JSON and emit structured events
                }

                override fun onFailure(
                    eventSource: EventSource,
                    t: Throwable?,
                    response: okhttp3.Response?
                ) {
                    // Handle failure
                }
            })
    }
}
