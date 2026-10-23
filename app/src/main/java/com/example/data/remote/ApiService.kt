package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatApiMessage(
    val role: String,
    val content: String
)

data class ApiResponse(
    val success: Boolean,
    val content: String,
    val error: String? = null,
    val latencyMs: Long = 0L
)

class ApiService {
    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    // 1. OpenAI-compatible API call (DeepSeek, GLM, Qwen, Z.ai, Custom)
    suspend fun sendChatCompletion(
        endpointUrl: String,
        apiKey: String,
        model: String,
        messages: List<ChatApiMessage>,
        systemPrompt: String = "You are Hazon AI, an expert software developer and architect. Provide clean, production-ready code and guidance."
    ): ApiResponse = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            val messagesArray = JSONArray()

            // System prompt
            messagesArray.put(
                JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                }
            )

            // User & Assistant conversation
            messages.forEach { msg ->
                messagesArray.put(
                    JSONObject().apply {
                        put("role", msg.role)
                        put("content", msg.content)
                    }
                )
            }

            val requestJson = JSONObject().apply {
                put("model", model)
                put("messages", messagesArray)
                put("temperature", 0.7)
            }

            val requestBody = requestJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(endpointUrl)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val latency = System.currentTimeMillis() - startTime
                val bodyStr = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    return@withContext ApiResponse(
                        success = false,
                        content = "",
                        error = "HTTP ${response.code}: $bodyStr",
                        latencyMs = latency
                    )
                }

                val jsonResponse = JSONObject(bodyStr)
                val choices = jsonResponse.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val firstChoice = choices.getJSONObject(0)
                    val message = firstChoice.optJSONObject("message")
                    val content = message?.optString("content") ?: ""
                    return@withContext ApiResponse(
                        success = true,
                        content = content,
                        latencyMs = latency
                    )
                } else {
                    return@withContext ApiResponse(
                        success = false,
                        content = "",
                        error = "Empty choices array in API response",
                        latencyMs = latency
                    )
                }
            }
        } catch (e: Exception) {
            val latency = System.currentTimeMillis() - startTime
            return@withContext ApiResponse(
                success = false,
                content = "",
                error = e.localizedMessage ?: "Unknown network error",
                latencyMs = latency
            )
        }
    }

    // 2. Google AI Studio (Gemini) REST API call (supports custom endpoints/proxies)
    suspend fun sendGeminiCompletion(
        apiKey: String,
        model: String = "gemini-2.5-flash",
        systemPrompt: String,
        userPrompt: String,
        endpointUrl: String = ""
    ): ApiResponse = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            val base = if (endpointUrl.isNotBlank()) {
                endpointUrl.trim()
            } else {
                "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent"
            }
            val url = when {
                apiKey.isBlank() -> base
                base.contains("key=") -> base
                base.contains("?") -> "$base&key=$apiKey"
                else -> "$base?key=$apiKey"
            }

            val requestJson = JSONObject().apply {
                if (systemPrompt.isNotBlank()) {
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().apply { put("text", systemPrompt) }))
                    })
                }

                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().put(JSONObject().apply { put("text", userPrompt) }))
                    })
                }
                put("contents", contentsArray)

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                })
            }

            val requestBody = requestJson.toString().toRequestBody(jsonMediaType)
            val requestBuilder = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
            if (apiKey.isNotBlank()) {
                requestBuilder.addHeader("x-goog-api-key", apiKey)
            }
            val request = requestBuilder.post(requestBody).build()

            client.newCall(request).execute().use { response ->
                val latency = System.currentTimeMillis() - startTime
                val bodyStr = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    return@withContext ApiResponse(
                        success = false,
                        content = "",
                        error = "HTTP ${response.code}: $bodyStr",
                        latencyMs = latency
                    )
                }

                val jsonResponse = JSONObject(bodyStr)
                // Gemini native format check
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val contentObj = candidate.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text") ?: ""
                    return@withContext ApiResponse(
                        success = true,
                        content = text,
                        latencyMs = latency
                    )
                }
                // OpenAI proxy compatibility check (if user points to an OpenAI-compatible Gemini wrapper)
                val choices = jsonResponse.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val message = choices.getJSONObject(0).optJSONObject("message")
                    val content = message?.optString("content") ?: ""
                    return@withContext ApiResponse(
                        success = true,
                        content = content,
                        latencyMs = latency
                    )
                }

                return@withContext ApiResponse(
                    success = false,
                    content = "",
                    error = "Unexpected response structure from endpoint",
                    latencyMs = latency
                )
            }
        } catch (e: Exception) {
            val latency = System.currentTimeMillis() - startTime
            return@withContext ApiResponse(
                success = false,
                content = "",
                error = e.localizedMessage ?: "Unknown network error",
                latencyMs = latency
            )
        }
    }

    suspend fun testGeminiConnection(
        apiKey: String,
        model: String = "gemini-2.5-flash",
        endpointUrl: String = ""
    ): ApiResponse {
        return sendGeminiCompletion(
            apiKey = apiKey,
            model = model,
            systemPrompt = "Respond with 'OK'",
            userPrompt = "Ping check",
            endpointUrl = endpointUrl
        )
    }

    suspend fun testConnection(
        endpointUrl: String,
        apiKey: String,
        model: String
    ): ApiResponse {
        return sendChatCompletion(
            endpointUrl = endpointUrl,
            apiKey = apiKey,
            model = model,
            messages = listOf(
                ChatApiMessage(role = "user", content = "Reply with 'OK'")
            ),
            systemPrompt = "Respond with 'OK' if you receive this."
        )
    }
}
