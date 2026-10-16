package com.example.data.repository

import com.example.data.local.BookmarkedTermEntity
import com.example.data.local.HabitDao
import com.example.data.local.HabitEntity
import com.example.data.local.HabitLogEntity
import com.example.data.model.CategoryTrend
import com.example.data.model.DayTrend
import com.example.data.model.HabitCategory
import com.example.data.model.HabitItem
import com.example.data.model.TimePeriodTrend
import com.example.data.model.WeeklyDashboardData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HabitRepository(
    private val habitDao: HabitDao,
    private val firebaseSync: FirebaseSyncRepository? = null
) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getTodayDateString(): String = dateFormat.format(Date())

    val allBookmarks: Flow<List<BookmarkedTermEntity>> = habitDao.getAllBookmarks()

    suspend fun toggleBookmark(termId: String, isCurrentlyBookmarked: Boolean) {
        if (isCurrentlyBookmarked) {
            habitDao.deleteBookmark(termId)
        } else {
            habitDao.insertBookmark(BookmarkedTermEntity(termId = termId))
        }
    }

    suspend fun initializeDefaultHabitsIfEmpty() {
        if (habitDao.getHabitCount() == 0) {
            val defaults = listOf(
                // 1. Morning - 07:00 AM
                HabitEntity(
                    title = "Wake-Up Cellular Hydration (500ml)",
                    category = HabitCategory.HYDRATION_WELLNESS.name,
                    description = "Drink 500ml of clean water with a pinch of electrolytes upon rising.",
                    targetFrequency = "Every Morning",
                    scheduledTime = "07:00 AM",
                    orderIndex = 1,
                    timeOfDay = "Morning",
                    bioscienceRationale = "Restores overnight hypovolemia, increases plasma volume, and stimulates baseline salivary flow before any oral hygiene procedures."
                ),

                // 2. Morning - 07:15 AM
                HabitEntity(
                    title = "Tongue Scraping & Interdental Floss",
                    category = HabitCategory.ORAL_LIP.name,
                    description = "Scrape back-to-front 2-3 times, then floss all contact points.",
                    targetFrequency = "Morning",
                    scheduledTime = "07:15 AM",
                    orderIndex = 2,
                    timeOfDay = "Morning",
                    bioscienceRationale = "MUST precede toothbrushing. Clears volatile sulfur compounds (VSCs) and interproximal plaque biofilms so subsequent fluoride can directly contact enamel."
                ),

                // 3. Morning - 07:20 AM
                HabitEntity(
                    title = "2-Min Fluoride Brushing (Spit, Don't Rinse)",
                    category = HabitCategory.ORAL_LIP.name,
                    description = "Gently brush at 45 degrees. Spit excess paste; do not rinse with water.",
                    targetFrequency = "Morning",
                    scheduledTime = "07:20 AM",
                    orderIndex = 3,
                    timeOfDay = "Morning",
                    bioscienceRationale = "CRITICAL ORDER: Spitting without water rinsing leaves an active fluoride reservoir on teeth, promoting fluoroapatite formation for acid resistance."
                ),

                // 4. Morning - 07:25 AM
                HabitEntity(
                    title = "Lip Barrier Ointment / SPF Application",
                    category = HabitCategory.ORAL_LIP.name,
                    description = "Apply pure white petrolatum or ceramide lip shield with SPF 30.",
                    targetFrequency = "Morning",
                    scheduledTime = "07:25 AM",
                    orderIndex = 4,
                    timeOfDay = "Morning",
                    bioscienceRationale = "MUST BE APPLIED AFTER BRUSHING. Toothpaste surfactants (Sodium Lauryl Sulfate - SLS) strip lipid barriers. Applying lip ointment after brushing prevents stripping and locks moisture."
                ),

                // 5. Morning - 07:35 AM
                HabitEntity(
                    title = "Warm Shower & 3-Min Moisture Lock",
                    category = HabitCategory.BODY_SANITATION.name,
                    description = "Bathe in lukewarm water; apply ceramide body lotion within 3 minutes of drying.",
                    targetFrequency = "Post-Shower",
                    scheduledTime = "07:35 AM",
                    orderIndex = 5,
                    timeOfDay = "Morning",
                    bioscienceRationale = "3-MINUTE WINDOW: Evaporation rapidly spikes trans-epidermal water loss (TEWL). Applying occlusives/humectants on damp skin traps hydration in the stratum corneum."
                ),

                // 6. Morning - 07:45 AM
                HabitEntity(
                    title = "Post-Shower Beard & Hairline Hydration",
                    category = HabitCategory.GROOMING_SKIN.name,
                    description = "Work 3-4 drops of squalane or lightweight beard oil into hair follicles.",
                    targetFrequency = "Morning",
                    scheduledTime = "07:45 AM",
                    orderIndex = 6,
                    timeOfDay = "Morning",
                    bioscienceRationale = "Shower warmth and ambient steam soften hair keratin and dilate follicular ostia, allowing rapid lipid penetration without oily residue."
                ),

                // 7. Morning - 07:50 AM
                HabitEntity(
                    title = "Broad-Spectrum Mineral Sunscreen (SPF 30+)",
                    category = HabitCategory.GROOMING_SKIN.name,
                    description = "Evenly apply two finger-lengths of SPF 30+ across face and neck.",
                    targetFrequency = "Morning",
                    scheduledTime = "07:50 AM",
                    orderIndex = 7,
                    timeOfDay = "Morning",
                    bioscienceRationale = "FINAL MORNING STEP: Sunscreen forms a delicate optical barrier film. Applying other moisturizers on top dilutes UV filters; sunscreen must always be the outermost shield."
                ),

                // 8. Midday - 11:00 AM
                HabitEntity(
                    title = "Targeted 20s Hand Hygiene",
                    category = HabitCategory.BODY_SANITATION.name,
                    description = "Lather vigorously with soap and water before lunch or touching face.",
                    targetFrequency = "Midday",
                    scheduledTime = "11:00 AM",
                    orderIndex = 8,
                    timeOfDay = "Midday",
                    bioscienceRationale = "Removes transient pathogenic bacteria and viral particles picked up from transit and workspaces prior to mucosal inoculation."
                ),

                // 9. Midday - 01:30 PM
                HabitEntity(
                    title = "20-20-20 Screen Break & Blink Reset",
                    category = HabitCategory.HYDRATION_WELLNESS.name,
                    description = "Look 20 feet away for 20 seconds; complete 10 deliberate blinks.",
                    targetFrequency = "Work Hours",
                    scheduledTime = "01:30 PM",
                    orderIndex = 9,
                    timeOfDay = "Midday",
                    bioscienceRationale = "Digital strain decreases blink rate by 60%. Deliberate blinking redistributes the meibomian lipid tear layer over the cornea, preventing dry eye evaporation."
                ),

                // 10. Midday - 03:30 PM
                HabitEntity(
                    title = "Cellular Hydration Recharge (1.5L Check)",
                    category = HabitCategory.HYDRATION_WELLNESS.name,
                    description = "Consume 500ml water to reach afternoon hydration milestone.",
                    targetFrequency = "Afternoon",
                    scheduledTime = "03:30 PM",
                    orderIndex = 10,
                    timeOfDay = "Midday",
                    bioscienceRationale = "Maintains circulating blood volume and salivary flow rate, preventing afternoon halitosis and mucosal desiccation."
                ),

                // 11. Night - 07:00 PM
                HabitEntity(
                    title = "High-Touch Device Disinfection",
                    category = HabitCategory.BODY_SANITATION.name,
                    description = "Wipe phone, watch, and glasses with 70% isopropyl wipe.",
                    targetFrequency = "Evening",
                    scheduledTime = "07:00 PM",
                    orderIndex = 11,
                    timeOfDay = "Night",
                    bioscienceRationale = "Mobile phones harbor high staphylococcal colonies. Sanitizing before your evening hygiene prevents transferring microbes to freshly washed skin."
                ),

                // 12. Night - 09:30 PM
                HabitEntity(
                    title = "Evening Gentle Cleanser & Grime Removal",
                    category = HabitCategory.GROOMING_SKIN.name,
                    description = "Wash with mild non-stripping cleanser to melt sunscreen and pollution.",
                    targetFrequency = "Night",
                    scheduledTime = "09:30 PM",
                    orderIndex = 12,
                    timeOfDay = "Night",
                    bioscienceRationale = "Prepares epidermal barrier for nighttime cellular turnover. Removes oxidized sebum without stripping intercellular lipid ceramides."
                ),

                // 13. Night - 09:40 PM
                HabitEntity(
                    title = "Post-Shave / Post-Barber Soothing",
                    category = HabitCategory.GROOMING_SKIN.name,
                    description = "Apply chilled aloe vera or 1% hydrocortisone to freshly groomed skin.",
                    targetFrequency = "Grooming Days",
                    scheduledTime = "09:40 PM",
                    orderIndex = 13,
                    timeOfDay = "Night",
                    bioscienceRationale = "Shaving removes 2-3 layers of stratum corneum. Applying anti-inflammatory agents immediately afterwards prevents follicular erythema and razor bumps."
                ),

                // 14. Night - 10:00 PM
                HabitEntity(
                    title = "Nighttime Interdental Floss & Fluoride Cleanse",
                    category = HabitCategory.ORAL_LIP.name,
                    description = "Floss all teeth first, then brush for 2 minutes. Spit excess paste.",
                    targetFrequency = "Night",
                    scheduledTime = "10:00 PM",
                    orderIndex = 14,
                    timeOfDay = "Night",
                    bioscienceRationale = "Salivary defenses plummet by >80% during sleep. Removing substrate before bed prevents nocturnal acid demineralization by Streptococcus mutans."
                ),

                // 15. Night - 10:10 PM
                HabitEntity(
                    title = "Bedtime Lip & Barrier Slugging (Petrolatum)",
                    category = HabitCategory.ORAL_LIP.name,
                    description = "Coat lips generously with pure white petrolatum; seal dry spots.",
                    targetFrequency = "Bedtime",
                    scheduledTime = "10:10 PM",
                    orderIndex = 15,
                    timeOfDay = "Night",
                    bioscienceRationale = "MUST OCCUR AFTER ORAL HYGIENE: Creates a 98% moisture occlusive shield. Protects the delicate labial commissures against nighttime saliva pooling (angular cheilitis)."
                ),

                // 16. Night - 10:30 PM
                HabitEntity(
                    title = "Circadian Wind-Down (Melatonin Preservation)",
                    category = HabitCategory.HYDRATION_WELLNESS.name,
                    description = "Dim lighting, cease screens 30-45 mins before sleep.",
                    targetFrequency = "Night",
                    scheduledTime = "10:30 PM",
                    orderIndex = 16,
                    timeOfDay = "Night",
                    bioscienceRationale = "Blue wavelengths (460-480nm) stimulate melanopsin intrinsically photosensitive retinal ganglion cells, stopping pineal melatonin release. Darkness restores restorative sleep cycles."
                )
            )
            habitDao.insertHabits(defaults)
        }
        seedPastDaysDemoLogsIfEmpty()
    }

    fun getHabitItemsWithTodayStatus(): Flow<List<HabitItem>> {
        val today = getTodayDateString()
        return combine(
            habitDao.getAllHabits(),
            habitDao.getLogsForDate(today),
            habitDao.getAllLogs()
        ) { habits, todayLogs, allLogs ->
            val completedHabitIds = todayLogs.filter { it.completed }.map { it.habitId }.toSet()
            val logsByHabit = allLogs.filter { it.completed }.groupBy { it.habitId }

            habits.map { habit ->
                val isCompleted = completedHabitIds.contains(habit.id)
                val habitLogs = logsByHabit[habit.id] ?: emptyList()
                val streak = calculateStreak(habitLogs.map { it.dateString }.toSet(), today, isCompleted)

                HabitItem(
                    id = habit.id,
                    title = habit.title,
                    category = HabitCategory.fromName(habit.category),
                    description = habit.description,
                    targetFrequency = habit.targetFrequency,
                    scheduledTime = habit.scheduledTime,
                    orderIndex = habit.orderIndex,
                    bioscienceRationale = habit.bioscienceRationale,
                    timeOfDay = habit.timeOfDay,
                    isReminderEnabled = habit.isReminderEnabled,
                    isCustom = habit.isCustom,
                    isCompletedToday = isCompleted,
                    currentStreak = streak
                )
            }
        }
    }

    private fun calculateStreak(completedDates: Set<String>, today: String, completedToday: Boolean): Int {
        if (completedDates.isEmpty()) return 0

        var streak = 0
        val cal = Calendar.getInstance()

        if (!completedToday) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            val checkDate = dateFormat.format(cal.time)
            if (completedDates.contains(checkDate)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    suspend fun toggleHabitCompletion(habitId: Long, completedNow: Boolean) {
        val today = getTodayDateString()
        if (completedNow) {
            habitDao.insertLog(
                HabitLogEntity(
                    habitId = habitId,
                    dateString = today,
                    completed = true
                )
            )
        } else {
            habitDao.deleteLog(habitId, today)
        }

        // Sync to cloud if user is signed in
        firebaseSync?.syncHabitLogToCloud(habitId, today, completedNow)
    }

    suspend fun addCustomHabit(
        title: String,
        category: HabitCategory,
        description: String,
        targetFrequency: String,
        scheduledTime: String = "08:00 AM",
        timeOfDay: String = "Morning",
        bioscienceRationale: String = "",
        context: android.content.Context? = null
    ): Long {
        val habit = HabitEntity(
            title = title.trim(),
            category = category.name,
            description = description.trim().ifEmpty { "Personal wellness routine." },
            targetFrequency = targetFrequency.trim().ifEmpty { "Daily" },
            scheduledTime = scheduledTime,
            orderIndex = 99,
            timeOfDay = timeOfDay,
            bioscienceRationale = bioscienceRationale.trim().ifEmpty { "Personal hygiene routine scheduled to support optimal physiological health." },
            isReminderEnabled = true,
            isCustom = true
        )
        val id = habitDao.insertHabit(habit)
        val inserted = habit.copy(id = id)
        firebaseSync?.syncHabitToCloud(inserted)
        if (context != null) {
            com.example.notifications.HabitNotificationScheduler.scheduleHabitReminder(
                context = context,
                habitId = id,
                title = inserted.title,
                scheduledTime = inserted.scheduledTime,
                orderIndex = inserted.orderIndex,
                bioscienceRationale = inserted.bioscienceRationale,
                isReminderEnabled = true
            )
        }
        return id
    }

    suspend fun deleteHabit(habitId: Long, context: android.content.Context? = null) {
        habitDao.deleteHabit(habitId)
        if (context != null) {
            com.example.notifications.HabitNotificationScheduler.cancelHabitReminder(context, habitId)
        }
    }

    suspend fun toggleHabitReminder(context: android.content.Context, habit: HabitItem) {
        val newStatus = !habit.isReminderEnabled
        habitDao.updateReminderEnabled(habit.id, newStatus)
        if (newStatus) {
            com.example.notifications.HabitNotificationScheduler.scheduleHabitReminder(
                context = context,
                habitId = habit.id,
                title = habit.title,
                scheduledTime = habit.scheduledTime,
                orderIndex = habit.orderIndex,
                bioscienceRationale = habit.bioscienceRationale,
                isReminderEnabled = true
            )
        } else {
            com.example.notifications.HabitNotificationScheduler.cancelHabitReminder(context, habit.id)
        }
    }

    suspend fun scheduleAllEnabledHabits(context: android.content.Context) {
        val habits = habitDao.getAllHabits().firstOrNull() ?: return
        habits.filter { it.isReminderEnabled }.forEach { habit ->
            com.example.notifications.HabitNotificationScheduler.scheduleHabitReminder(
                context = context,
                habitId = habit.id,
                title = habit.title,
                scheduledTime = habit.scheduledTime,
                orderIndex = habit.orderIndex,
                bioscienceRationale = habit.bioscienceRationale,
                isReminderEnabled = true
            )
        }
    }

    suspend fun seedPastDaysDemoLogsIfEmpty() {
        val existingLogs = habitDao.getAllLogs().firstOrNull() ?: emptyList()
        if (existingLogs.isEmpty()) {
            val habits = habitDao.getAllHabits().firstOrNull() ?: emptyList()
            if (habits.isNotEmpty()) {
                val dayOffsets = listOf(-6, -5, -4, -3, -2, -1)
                val completionCounts = listOf(11, 13, 14, 12, 15, 14)

                dayOffsets.forEachIndexed { index, offset ->
                    val dayCal = Calendar.getInstance()
                    dayCal.add(Calendar.DAY_OF_YEAR, offset)
                    val dateStr = dateFormat.format(dayCal.time)
                    val count = minOf(completionCounts[index], habits.size)

                    val habitsToLog = habits.take(count)
                    habitsToLog.forEach { habit ->
                        habitDao.insertLog(
                            HabitLogEntity(
                                habitId = habit.id,
                                dateString = dateStr,
                                completed = true,
                                timestamp = dayCal.timeInMillis
                            )
                        )
                    }
                }
            }
        }
    }

    fun getWeeklyTrends(): Flow<WeeklyDashboardData> {
        val today = getTodayDateString()
        val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.US)
        val dayNumberFormat = SimpleDateFormat("d", Locale.US)
        val fullDateFormat = SimpleDateFormat("EEEE, MMM d", Locale.US)

        return combine(
            habitDao.getAllHabits(),
            habitDao.getAllLogs()
        ) { habits, logs ->
            val activeHabits = habits.filter { !it.isArchived }
            val totalHabitCount = activeHabits.size

            val logsByDate = logs.filter { it.completed }.groupBy { it.dateString }

            val daysList = mutableListOf<DayTrend>()

            for (offset in -6..0) {
                val dayCal = Calendar.getInstance()
                dayCal.add(Calendar.DAY_OF_YEAR, offset)
                val dateStr = dateFormat.format(dayCal.time)
                val isToday = (dateStr == today)
                val dayLogs = logsByDate[dateStr] ?: emptyList()
                val completedIds = dayLogs.map { it.habitId }.toSet()
                val completedCount = activeHabits.count { completedIds.contains(it.id) }
                val percentage = if (totalHabitCount > 0) {
                    (completedCount.toFloat() / totalHabitCount.toFloat()) * 100f
                } else 0f

                daysList.add(
                    DayTrend(
                        dateString = dateStr,
                        dayName = if (isToday) "Today" else dayOfWeekFormat.format(dayCal.time),
                        dayNumber = dayNumberFormat.format(dayCal.time),
                        fullDateString = fullDateFormat.format(dayCal.time),
                        isToday = isToday,
                        completedCount = completedCount,
                        totalCount = totalHabitCount,
                        percentage = percentage,
                        completedHabitIds = completedIds
                    )
                )
            }

            val totalCompletions = daysList.sumOf { it.completedCount }
            val totalPossible = totalHabitCount * 7
            val averageAdherence = if (daysList.isNotEmpty()) {
                daysList.map { it.percentage }.average().toFloat()
            } else 0f

            val bestDay = daysList.maxByOrNull { it.percentage }
            val bestDayName = bestDay?.let { if (it.isToday) "Today" else it.dayName } ?: "-"
            val bestDayPercentage = bestDay?.percentage ?: 0f

            val todayCompleted = daysList.lastOrNull()?.let { it.completedCount > 0 } ?: false
            val completedDatesSet = logs.filter { it.completed }.map { it.dateString }.toSet()
            val currentStreak = calculateStreak(completedDatesSet, today, todayCompleted)

            val categoryTrends = HabitCategory.entries.map { category ->
                val categoryHabits = activeHabits.filter { it.category == category.name }
                val categoryHabitIds = categoryHabits.map { it.id }.toSet()
                val completedCategoryCount = daysList.sumOf { day ->
                    day.completedHabitIds.count { categoryHabitIds.contains(it) }
                }
                val totalPossibleCategory = categoryHabits.size * 7
                val catPct = if (totalPossibleCategory > 0) {
                    (completedCategoryCount.toFloat() / totalPossibleCategory.toFloat()) * 100f
                } else 0f

                CategoryTrend(
                    category = category,
                    completedCount = completedCategoryCount,
                    totalPossibleCount = totalPossibleCategory,
                    percentage = catPct
                )
            }

            val timePeriods = listOf("Morning", "Midday", "Night")
            val timePeriodTrends = timePeriods.map { period ->
                val periodHabits = activeHabits.filter { it.timeOfDay.equals(period, ignoreCase = true) }
                val periodHabitIds = periodHabits.map { it.id }.toSet()
                val completedPeriodCount = daysList.sumOf { day ->
                    day.completedHabitIds.count { periodHabitIds.contains(it) }
                }
                val totalPossiblePeriod = periodHabits.size * 7
                val periodPct = if (totalPossiblePeriod > 0) {
                    (completedPeriodCount.toFloat() / totalPossiblePeriod.toFloat()) * 100f
                } else 0f

                TimePeriodTrend(
                    timeOfDay = period,
                    completedCount = completedPeriodCount,
                    totalCount = totalPossiblePeriod,
                    percentage = periodPct
                )
            }

            WeeklyDashboardData(
                days = daysList,
                averageAdherence = averageAdherence,
                totalCompletions = totalCompletions,
                totalPossibleCompletions = totalPossible,
                currentStreak = currentStreak,
                bestDayName = bestDayName,
                bestDayPercentage = bestDayPercentage,
                categoryTrends = categoryTrends,
                timePeriodTrends = timePeriodTrends
            )
        }
    }
}
