package com.example.data.model

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user" or "model"
    val content: String,
    val modelUsed: String = "gemini-3.8-flash",
    val sources: List<GroundingSource> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class GroundingSource(
    val title: String,
    val uri: String
)

enum class GeminiModelOption(
    val modelId: String,
    val displayName: String,
    val description: String,
    val badge: String
) {
    FLASH(
        modelId = "gemini-3.8-flash",
        displayName = "Gemini 3.8 Flash",
        description = "General health & live Google Search Grounding",
        badge = "Search Grounded"
    ),
    ARGON(
        modelId = "gemini-4-argon",
        displayName = "Gemini 4 Argon",
        description = "Deep bioscience, complex dermatology & biochemical reasoning",
        badge = "Complex Reasoning"
    ),
    FLASH_LITE(
        modelId = "gemini-3.1-flash-lite",
        displayName = "Gemini 3.1 Flash Lite",
        description = "Ultra-fast response for instant hygiene & habit questions",
        badge = "Fast"
    )
}
