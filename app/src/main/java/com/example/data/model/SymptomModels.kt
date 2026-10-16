package com.example.data.model

data class Symptom(
    val id: String,
    val label: String,
    val category: String, // "Lips & Mouth", "Skin & Face", "Scalp & Hair", "General"
    val iconKey: String
)

data class AssessmentRequest(
    val selectedSymptoms: Set<String>,
    val affectedRegion: String,
    val duration: String
)

data class ConditionMatch(
    val conditionName: String,
    val alternativeName: String,
    val matchRelevance: String, // "High Match", "Moderate Match", "Potential Match"
    val whyMatched: String,
    val firstLineActions: List<String>,
    val thingsToAvoid: List<String>,
    val redFlagSigns: String
)

data class AssessmentResult(
    val timestamp: Long = System.currentTimeMillis(),
    val primaryMatches: List<ConditionMatch>,
    val generalFirstLineCare: List<String>,
    val commonTriggersToAvoid: List<String>,
    val urgentCareWarning: String? = null
)
