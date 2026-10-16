package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HabitNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d("HabitReceiver", "Received action: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule all habit alarms after device reboot
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val habits = db.habitDao().getAllHabits().firstOrNull() ?: emptyList()
                    habits.forEach { habit ->
                        HabitNotificationScheduler.scheduleHabitReminder(
                            context = context,
                            habitId = habit.id,
                            title = habit.title,
                            scheduledTime = habit.scheduledTime,
                            orderIndex = habit.orderIndex,
                            bioscienceRationale = habit.bioscienceRationale,
                            isReminderEnabled = true
                        )
                    }
                } catch (e: Exception) {
                    Log.e("HabitReceiver", "Failed to reschedule on boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
            return
        }

        if (action == HabitNotificationScheduler.ACTION_HABIT_REMINDER) {
            val habitId = intent.getLongExtra(HabitNotificationScheduler.EXTRA_HABIT_ID, 0L)
            val title = intent.getStringExtra(HabitNotificationScheduler.EXTRA_HABIT_TITLE) ?: "Hygiene Routine"
            val scheduledTime = intent.getStringExtra(HabitNotificationScheduler.EXTRA_HABIT_TIME) ?: ""
            val stepNumber = intent.getIntExtra(HabitNotificationScheduler.EXTRA_HABIT_STEP, 1)
            val rationale = intent.getStringExtra(HabitNotificationScheduler.EXTRA_HABIT_RATIONALE) ?: ""

            showNotification(
                context = context,
                habitId = habitId,
                title = title,
                scheduledTime = scheduledTime,
                stepNumber = stepNumber,
                rationale = rationale
            )

            // Reschedule for next day at the same time
            HabitNotificationScheduler.scheduleHabitReminder(
                context = context,
                habitId = habitId,
                title = title,
                scheduledTime = scheduledTime,
                orderIndex = stepNumber,
                bioscienceRationale = rationale,
                isReminderEnabled = true
            )
        }
    }

    private fun showNotification(
        context: Context,
        habitId: Long,
        title: String,
        scheduledTime: String,
        stepNumber: Int,
        rationale: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = HabitNotificationScheduler.CHANNEL_ID

        // Create high importance notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Aura Hygiene & Routine Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Evidence-based scheduled reminders for daily hygiene and wellness routines"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            habitId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val headerText = if (scheduledTime.isNotBlank()) "$scheduledTime • Step $stepNumber" else "Step $stepNumber"
        val bodyText = rationale.ifBlank { "Time to complete your scheduled hygiene protocol." }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.aura_launcher_icon_1792121453525)
            .setContentTitle("$headerText: $title")
            .setContentText(bodyText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("$headerText: $title")
                    .bigText(bodyText)
                    .setSummaryText("AuraHealth Bioscience Protocol")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setColor(0xFF10B981.toInt()) // Emerald primary
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(habitId.toInt(), notification)
    }
}
