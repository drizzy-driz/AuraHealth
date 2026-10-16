package com.example.data.model

data class HomeRemedy(
    val id: String,
    val name: String,
    val category: String, // "Skin", "Mouth/Lips", "Digestion", "First Aid"
    val activeIngredient: String,
    val indications: List<String>,
    val mechanismOfAction: String,
    val applicationSteps: List<String>,
    val precautions: List<String>,
    val clinicalPearls: String
)
