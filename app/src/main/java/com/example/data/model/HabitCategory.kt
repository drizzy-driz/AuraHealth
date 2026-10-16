package com.example.data.model

enum class HabitCategory(
    val displayName: String,
    val iconKey: String,
    val description: String
) {
    ORAL_LIP(
        displayName = "Oral & Lip Care",
        iconKey = "oral",
        description = "Flossing, lip ointment, gentle tongue scraping & enamel protection"
    ),
    GROOMING_SKIN(
        displayName = "Grooming & Skin Health",
        iconKey = "skin",
        description = "Beard/hairline hydration, post-barber soothing & sun protection"
    ),
    BODY_SANITATION(
        displayName = "Body & Sanitation",
        iconKey = "sanitation",
        description = "Hand hygiene, shower barrier locks & bedding cleanliness"
    ),
    HYDRATION_WELLNESS(
        displayName = "Hydration & Wellness",
        iconKey = "wellness",
        description = "Electrolyte/water goals, ergonomic screen resets & posture"
    );

    companion object {
        fun fromName(name: String): HabitCategory {
            return entries.find { it.name == name || it.displayName == name } ?: ORAL_LIP
        }
    }
}
