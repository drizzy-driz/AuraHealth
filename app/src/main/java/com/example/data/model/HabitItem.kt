package com.example.data.model

data class HabitItem(
    val id: Long,
    val title: String,
    val category: HabitCategory,
    val description: String,
    val targetFrequency: String = "Daily",
    val scheduledTime: String = "07:00 AM",
    val orderIndex: Int = 1,
    val bioscienceRationale: String = "",
    val timeOfDay: String = "Morning",
    val isReminderEnabled: Boolean = true,
    val isCustom: Boolean = false,
    val isCompletedToday: Boolean = false,
    val currentStreak: Int = 0
)
