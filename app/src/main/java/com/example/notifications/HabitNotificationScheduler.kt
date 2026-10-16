package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object HabitNotificationScheduler {

    const val CHANNEL_ID = "aura_habits_channel"
    const val ACTION_HABIT_REMINDER = "com.example.ACTION_HABIT_REMINDER"

    const val EXTRA_HABIT_ID = "extra_habit_id"
    const val EXTRA_HABIT_TITLE = "extra_habit_title"
    const val EXTRA_HABIT_TIME = "extra_habit_time"
    const val EXTRA_HABIT_STEP = "extra_habit_step"
    const val EXTRA_HABIT_RATIONALE = "extra_habit_rationale"

    fun scheduleHabitReminder(
        context: Context,
        habitId: Long,
        title: String,
        scheduledTime: String,
        orderIndex: Int,
        bioscienceRationale: String,
        isReminderEnabled: Boolean = true
    ) {
        if (!isReminderEnabled || scheduledTime.isBlank()) {
            cancelHabitReminder(context, habitId)
            return
        }

        val alarmManager = context.getSystemService(Context.NOTIFICATION_SERVICE).let {
            context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        } ?: return

        val triggerTimeMillis = parseNextTriggerTime(scheduledTime)

        val intent = Intent(context, HabitNotificationReceiver::class.java).apply {
            action = ACTION_HABIT_REMINDER
            putExtra(EXTRA_HABIT_ID, habitId)
            putExtra(EXTRA_HABIT_TITLE, title)
            putExtra(EXTRA_HABIT_TIME, scheduledTime)
            putExtra(EXTRA_HABIT_STEP, orderIndex)
            putExtra(EXTRA_HABIT_RATIONALE, bioscienceRationale)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            }
            Log.d("HabitScheduler", "Scheduled reminder for $title at $scheduledTime (millis: $triggerTimeMillis)")
        } catch (e: SecurityException) {
            Log.w("HabitScheduler", "Exact alarm permission missing, fallback to set(): ${e.message}")
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
        } catch (e: Exception) {
            Log.e("HabitScheduler", "Failed to schedule alarm for $title", e)
        }
    }

    fun cancelHabitReminder(context: Context, habitId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, HabitNotificationReceiver::class.java).apply {
            action = ACTION_HABIT_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("HabitScheduler", "Cancelled reminder for habit $habitId")
        }
    }

    fun triggerInstantTestReminder(
        context: Context,
        habitId: Long = 9999L,
        title: String = "Interdental Flossing & Lip Shield",
        scheduledTime: String = "Now",
        orderIndex: Int = 1,
        bioscienceRationale: String = "Floss first to remove interproximal biofilm so fluoride toothpaste can directly remineralize enamel."
    ) {
        val intent = Intent(context, HabitNotificationReceiver::class.java).apply {
            action = ACTION_HABIT_REMINDER
            putExtra(EXTRA_HABIT_ID, habitId)
            putExtra(EXTRA_HABIT_TITLE, title)
            putExtra(EXTRA_HABIT_TIME, scheduledTime)
            putExtra(EXTRA_HABIT_STEP, orderIndex)
            putExtra(EXTRA_HABIT_RATIONALE, bioscienceRationale)
        }
        context.sendBroadcast(intent)
    }

    private fun parseNextTriggerTime(timeStr: String): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance()

        try {
            val format = SimpleDateFormat("hh:mm a", Locale.US)
            val parsedDate = format.parse(timeStr.trim())
            if (parsedDate != null) {
                val parsedCal = Calendar.getInstance().apply { time = parsedDate }
                target.set(Calendar.HOUR_OF_DAY, parsedCal.get(Calendar.HOUR_OF_DAY))
                target.set(Calendar.MINUTE, parsedCal.get(Calendar.MINUTE))
                target.set(Calendar.SECOND, 0)
                target.set(Calendar.MILLISECOND, 0)

                // If already passed today, advance to tomorrow
                if (target.before(now)) {
                    target.add(Calendar.DAY_OF_YEAR, 1)
                }
                return target.timeInMillis
            }
        } catch (e: Exception) {
            Log.w("HabitScheduler", "Could not parse time: $timeStr, defaulting to +1 hour", e)
        }

        return now.timeInMillis + (60 * 60 * 1000)
    }
}
