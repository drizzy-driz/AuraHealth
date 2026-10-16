package com.example.data.model

data class MedicalTerm(
    val id: String,
    val name: String,
    val alternativeName: String = "",
    val category: String, // e.g. "Oral & Lips", "Skin & Beard", "Body & Systemic"
    val definition: String,
    val primarySymptoms: List<String>,
    val commonCauses: List<String>,
    val atHomeCare: List<String>,
    val redFlags: String,
    val preventionTip: String,
    val anatomicalRegion: String
)
