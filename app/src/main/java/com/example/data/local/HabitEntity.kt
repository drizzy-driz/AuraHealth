package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // from HabitCategory.name
    val description: String,
    val targetFrequency: String = "Daily",
    val scheduledTime: String = "07:00 AM",
    val orderIndex: Int = 1,
    val bioscienceRationale: String = "",
    val timeOfDay: String = "Morning",
    val isReminderEnabled: Boolean = true,
    val isCustom: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
