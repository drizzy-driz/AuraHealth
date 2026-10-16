package com.example.data.model

data class DayTrend(
    val dateString: String,
    val dayName: String,
    val dayNumber: String,
    val fullDateString: String,
    val isToday: Boolean,
    val completedCount: Int,
    val totalCount: Int,
    val percentage: Float,
    val completedHabitIds: Set<Long> = emptySet()
)

data class CategoryTrend(
    val category: HabitCategory,
    val completedCount: Int,
    val totalPossibleCount: Int,
    val percentage: Float
)

data class TimePeriodTrend(
    val timeOfDay: String,
    val completedCount: Int,
    val totalCount: Int,
    val percentage: Float
)

data class WeeklyDashboardData(
    val days: List<DayTrend> = emptyList(),
    val averageAdherence: Float = 0f,
    val totalCompletions: Int = 0,
    val totalPossibleCompletions: Int = 0,
    val currentStreak: Int = 0,
    val bestDayName: String = "-",
    val bestDayPercentage: Float = 0f,
    val categoryTrends: List<CategoryTrend> = emptyList(),
    val timePeriodTrends: List<TimePeriodTrend> = emptyList()
)
