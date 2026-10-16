package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ChatMessage
import com.example.data.model.GeminiModelOption
import com.example.data.model.GroundingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun sendMessage(
        history: List<ChatMessage>,
        userMessage: String,
        modelOption: GeminiModelOption,
        enableSearchGrounding: Boolean = true
    ): Result<ChatMessage> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not set. Please add GEMINI_API_KEY to your environment secrets.")
            )
        }

        try {
            val root = JSONObject()

            // System instruction defining bioscience & clinical hygiene authority
            val systemInstruction = JSONObject().apply {
                val partsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", """
                            You are Aura Bioscience & Health Consultant, an evidence-based clinical and dermatological AI assistant.
                            You explain the biological, dermatological, and physiological rationale behind health, hygiene, and medical routines.
                            Specifically, you explain why routines must be ordered in exact sequences:
                            - Why interdental flossing must precede tooth brushing (to clear proximal spaces for fluoride contact).
                            - Why you must spit and NOT rinse after fluoride brushing (to allow calcium-phosphate remineralization).
                            - Why lip ointment/petrolatum must be applied AFTER brushing (toothpaste SLS/surfactants destroy lip lipid barriers).
                            - Why ceramide moisturizers must be applied within 3 minutes of a warm shower (locks in water before trans-epidermal water loss).
                            - Why mineral sunscreen must be the final morning skincare step (preventing physical disruption of the UV filter film).
                            - Why pure white petrolatum slugging at bedtime prevents nocturnal saliva pooling and angular cheilitis.
                            Provide friendly, structured, scientifically rigorous guidance.
                            Always include a brief closing disclaimer that this is educational advice and not a substitute for clinical medical diagnosis.
                        """.trimIndent())
                    })
                }
                put("parts", partsArray)
            }
            root.put("systemInstruction", systemInstruction)

            // Contents array representing multi-turn conversation
            val contentsArray = JSONArray()

            // Add previous history turns (limit to last 10 messages for context efficiency)
            val recentHistory = history.takeLast(10)
            for (msg in recentHistory) {
                val turnObj = JSONObject()
                turnObj.put("role", if (msg.role == "user") "user" else "model")
                val parts = JSONArray()
                parts.put(JSONObject().apply { put("text", msg.content) })
                turnObj.put("parts", parts)
                contentsArray.put(turnObj)
            }

            // Append current user message
            val currentTurn = JSONObject().apply {
                put("role", "user")
                val parts = JSONArray()
                parts.put(JSONObject().apply { put("text", userMessage) })
                put("parts", parts)
            }
            contentsArray.put(currentTurn)
            root.put("contents", contentsArray)

            // Add Google Search Grounding tool when enabled or using Flash
            if (enableSearchGrounding && modelOption == GeminiModelOption.FLASH) {
                val toolsArray = JSONArray()
                val toolObj = JSONObject()
                toolObj.put("googleSearch", JSONObject())
                toolsArray.put(toolObj)
                root.put("tools", toolsArray)
            }

            // Generation config
            val genConfig = JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
            }
            root.put("generationConfig", genConfig)

            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/${modelOption.modelId}:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(endpoint)
                .post(root.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e("GeminiService", "API error: ${response.code} -> $responseBody")
                return@withContext Result.failure(
                    Exception("Gemini API call failed (${response.code}): $responseBody")
                )
            }

            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(Exception("No response received from Gemini model."))
            }

            val firstCandidate = candidates.getJSONObject(0)
            val contentObj = firstCandidate.optJSONObject("content")
            val partsArray = contentObj?.optJSONArray("parts")

            val replyBuilder = StringBuilder()
            if (partsArray != null) {
                for (i in 0 until partsArray.length()) {
                    val part = partsArray.getJSONObject(i)
                    if (part.has("text")) {
                        replyBuilder.append(part.getString("text"))
                    }
                }
            }

            // Extract Google Search Grounding metadata if present
            val sources = mutableListOf<GroundingSource>()
            val groundingMetadata = firstCandidate.optJSONObject("groundingMetadata")
            if (groundingMetadata != null) {
                val searchChunks = groundingMetadata.optJSONArray("groundingChunks")
                if (searchChunks != null) {
                    for (i in 0 until searchChunks.length()) {
                        val chunk = searchChunks.optJSONObject(i)
                        val webObj = chunk?.optJSONObject("web")
                        if (webObj != null) {
                            val title = webObj.optString("title", "Google Search Reference")
                            val uri = webObj.optString("uri", "")
                            if (uri.isNotBlank()) {
                                sources.add(GroundingSource(title = title, uri = uri))
                            }
                        }
                    }
                }
            }

            val chatMsg = ChatMessage(
                role = "model",
                content = replyBuilder.toString().ifBlank { "I received your message. How else can I assist with your bioscience & hygiene protocol?" },
                modelUsed = modelOption.modelId,
                sources = sources
            )

            Result.success(chatMsg)
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception during Gemini API request", e)
            Result.failure(e)
        }
    }
}
