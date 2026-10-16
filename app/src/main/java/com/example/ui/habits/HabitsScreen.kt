package com.example.ui.habits

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.HabitCategory
import com.example.data.model.HabitItem
import com.example.ui.AuraHealthViewModel
import com.example.ui.HabitsViewMode
import com.example.ui.auth.GoogleSignInCard
import com.example.ui.components.AuraTopBar
import com.example.ui.components.HabitProgressCard
import com.example.ui.dashboard.WeeklyProgressDashboard
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AlertRose
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.MedicalBorder
import com.example.ui.theme.MedicalDeepNavy
import com.example.ui.theme.MedicalNavyCard
import com.example.ui.theme.MedicalNavyCardElevated
import com.example.ui.theme.MedicalNavySurface
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TextMediumSlate
import com.example.ui.theme.TextMutedSlate
import com.example.ui.theme.TextWhiteHigh

@Composable
fun HabitsScreen(
    viewModel: AuraHealthViewModel,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.filteredHabits.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedHabitCategory.collectAsStateWithLifecycle()
    val selectedTimeFilter by viewModel.selectedTimeFilter.collectAsStateWithLifecycle()
    val stats by viewModel.completionStats.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val habitsViewMode by viewModel.habitsViewMode.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var showAddHabitDialog by remember { mutableStateOf(false) }
    val timeFilters = listOf("All Times", "Morning", "Midday", "Night")

    // Runtime notification permission launcher for Android 13+
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Notification reminders enabled!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalDeepNavy)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AuraTopBar(
                title = "AuraHealth",
                subtitle = "Bioscience-Ordered Hygiene & Wellness Routine",
                badgeText = if (currentUser != null) "Cloud Synced" else "Offline Ready"
            )

            // Segmented View Toggle: Daily Routine vs. Weekly Trends Dashboard
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MedicalNavySurface)
                    .border(1.dp, MedicalBorder, RoundedCornerShape(14.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Daily Routine
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (habitsViewMode == HabitsViewMode.DAILY_PROTOCOL) EmeraldPrimary else Color.Transparent)
                        .clickable { viewModel.setHabitsViewMode(HabitsViewMode.DAILY_PROTOCOL) }
                        .padding(vertical = 8.dp)
                        .testTag("tab_daily_routine"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = if (habitsViewMode == HabitsViewMode.DAILY_PROTOCOL) Color(0xFF003822) else TextMediumSlate,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Daily Routine",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (habitsViewMode == HabitsViewMode.DAILY_PROTOCOL) Color(0xFF003822) else TextMediumSlate
                            )
                        )
                    }
                }

                // Weekly Trends
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (habitsViewMode == HabitsViewMode.WEEKLY_DASHBOARD) EmeraldPrimary else Color.Transparent)
                        .clickable { viewModel.setHabitsViewMode(HabitsViewMode.WEEKLY_DASHBOARD) }
                        .padding(vertical = 8.dp)
                        .testTag("tab_weekly_trends"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = if (habitsViewMode == HabitsViewMode.WEEKLY_DASHBOARD) Color(0xFF003822) else TextMediumSlate,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Weekly Trends",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (habitsViewMode == HabitsViewMode.WEEKLY_DASHBOARD) Color(0xFF003822) else TextMediumSlate
                            )
                        )
                    }
                }
            }

            if (habitsViewMode == HabitsViewMode.WEEKLY_DASHBOARD) {
                WeeklyProgressDashboard(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                // Google Sign-In & Cloud Sync Card
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        GoogleSignInCard(
                            currentUser = currentUser,
                            firebaseSync = viewModel.firebaseSync
                        )
                    }
                }

                // Notification Service Status & Quick Test Banner
                item {
                    NotificationServiceBanner(
                        hasPermission = hasNotificationPermission,
                        onRequestPermission = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        },
                        onTestReminder = {
                            viewModel.triggerTestNotification(context)
                            Toast.makeText(context, "Triggering test reminder alert...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Hero Banner with Cellular Shield
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .height(130.dp)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.health_hero_banner_1792121493543),
                            contentDescription = "Medical Hygiene Shield Illustration",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MedicalDeepNavy.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                                .padding(14.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Column {
                                Text(
                                    text = "Bioscience Sequential Order Protocol",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = TextWhiteHigh,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Sequenced so tooth detergents, shower steam & UV filters never ruin preceding care",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CyanAccent,
                                        lineHeight = 15.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Daily Progress Summary Card
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        HabitProgressCard(
                            completedCount = stats.first,
                            totalCount = stats.second,
                            percentage = stats.third,
                            onViewWeeklyTrends = {
                                viewModel.setHabitsViewMode(HabitsViewMode.WEEKLY_DASHBOARD)
                            }
                        )
                    }
                }

                // Time of Day Filter Row
                item {
                    Text(
                        text = "Time of Day Sequence:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TealAccent,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 4.dp)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(timeFilters) { time ->
                            CategoryFilterChip(
                                label = time,
                                isSelected = selectedTimeFilter == time,
                                onClick = { viewModel.selectTimeFilter(time) },
                                testTag = "time_filter_${time.lowercase().replace(" ", "_")}"
                            )
                        }
                    }
                }

                // Category Filter Chips
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            CategoryFilterChip(
                                label = "All Categories",
                                isSelected = selectedCategory == null,
                                onClick = { viewModel.selectHabitCategory(null) },
                                testTag = "filter_category_all"
                            )
                        }
                        items(HabitCategory.entries) { category ->
                            CategoryFilterChip(
                                label = category.displayName,
                                isSelected = selectedCategory == category,
                                onClick = { viewModel.selectHabitCategory(category) },
                                testTag = "filter_category_${category.name.lowercase()}"
                            )
                        }
                    }
                }

                // Header for checklist
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chronological Protocol Sequence",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhiteHigh,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "${habits.size} steps",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMediumSlate
                            )
                        )
                    }
                }

                // Checklist Items
                items(habits, key = { it.id }) { habit ->
                    HabitChecklistItem(
                        habit = habit,
                        onToggle = { viewModel.toggleHabit(habit.id, habit.isCompletedToday) },
                        onToggleReminder = {
                            viewModel.toggleHabitReminder(context, habit)
                        },
                        onDelete = if (habit.isCustom) {
                            { viewModel.deleteHabit(habit.id, context) }
                        } else null
                    )
                }

                if (habits.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No habits match this time/category filter. Tap '+' to add a routine.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextMutedSlate)
                            )
                        }
                    }
                }
            }
        }
    }

    // Floating Action Button to Add Custom Habit (only in Daily Protocol mode)
        if (habitsViewMode == HabitsViewMode.DAILY_PROTOCOL) {
            FloatingActionButton(
                onClick = { showAddHabitDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = Color(0xFF003822),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp)
                    .testTag("add_habit_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Custom Habit"
                )
            }
        }

        // Add Habit Dialog
        if (showAddHabitDialog) {
            AddHabitDialog(
                onDismiss = { showAddHabitDialog = false },
                onAdd = { title, category, desc, freq, time, timeOfDay, rationale ->
                    viewModel.addCustomHabit(
                        title = title,
                        category = category,
                        description = desc,
                        targetFrequency = freq,
                        scheduledTime = time,
                        timeOfDay = timeOfDay,
                        bioscienceRationale = rationale,
                        context = context
                    )
                    showAddHabitDialog = false
                }
            )
        }
    }
}

@Composable
fun NotificationServiceBanner(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onTestReminder: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MedicalNavyCard)
            .border(1.dp, MedicalBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Notifications",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Daily Habit Reminder Service",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextWhiteHigh,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = if (hasPermission)
                                "Scheduled alarms active for each habit's time"
                            else
                                "Tap to grant notification permissions",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (hasPermission) EmeraldPrimaryLight else AlertAmber,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!hasPermission) {
                        Button(
                            onClick = onRequestPermission,
                            colors = ButtonDefaults.buttonColors(containerColor = AlertAmber),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Enable", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = onTestReminder,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(MedicalBorder)
                        ),
                        modifier = Modifier.testTag("test_reminder_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Alert", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun HabitChecklistItem(
    habit: HabitItem,
    onToggle: () -> Unit,
    onToggleReminder: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val isCompleted = habit.isCompletedToday
    var expandedRationale by remember { mutableStateOf(false) }

    val cardBg by animateColorAsState(
        targetValue = if (isCompleted) Color(0xFF0E222C) else MedicalNavyCard,
        label = "card_bg"
    )
    val cardBorder by animateColorAsState(
        targetValue = if (isCompleted) EmeraldPrimary.copy(alpha = 0.5f) else MedicalBorder,
        label = "card_border"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("habit_card_${habit.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox Circle
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) EmeraldPrimary else Color.Transparent)
                        .border(2.dp, if (isCompleted) EmeraldPrimary else TextMutedSlate, CircleShape)
                        .clickable(onClick = onToggle)
                        .testTag("habit_checkbox_${habit.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color(0xFF00331F),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Details
                Column(modifier = Modifier.weight(1f)) {
                    // Scheduled Time & Step Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TealAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = TealAccent,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = habit.scheduledTime,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TealAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Text(
                            text = "Step ${habit.orderIndex}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        if (habit.isCustom) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AlertAmber.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "CUSTOM",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AlertAmber,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCompleted) TextMediumSlate else TextWhiteHigh,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        modifier = Modifier.clickable(onClick = onToggle)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isCompleted) TextMutedSlate else TextMediumSlate,
                            lineHeight = 15.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Badges: Frequency, Streak
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "• ${habit.targetFrequency}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMutedSlate,
                                fontSize = 11.sp
                            )
                        )

                        if (habit.currentStreak > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = AlertAmber,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${habit.currentStreak} day streak",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AlertAmber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Notification Bell Toggle Button
                IconButton(
                    onClick = onToggleReminder,
                    modifier = Modifier.testTag("toggle_reminder_${habit.id}")
                ) {
                    Icon(
                        imageVector = if (habit.isReminderEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        contentDescription = if (habit.isReminderEnabled) "Disable Reminder" else "Enable Reminder",
                        tint = if (habit.isReminderEnabled) EmeraldPrimary else TextMutedSlate,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_habit_${habit.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Habit",
                            tint = AlertRose.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Expandable Bioscience Order Rationale Callout
            if (habit.bioscienceRationale.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MedicalNavyCardElevated)
                        .clickable { expandedRationale = !expandedRationale }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Order Rationale (Why this sequence?)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldPrimaryLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Icon(
                        imageVector = if (expandedRationale) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = EmeraldPrimaryLight,
                        modifier = Modifier.size(16.dp)
                    )
                }

                AnimatedVisibility(visible = expandedRationale) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0A1E29))
                            .border(0.5.dp, TealAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = habit.bioscienceRationale,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextWhiteHigh,
                                lineHeight = 16.sp,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val backgroundColor = if (isSelected) EmeraldPrimary else MedicalNavyCard
    val contentColor = if (isSelected) Color(0xFF003822) else TextMediumSlate
    val borderColor = if (isSelected) EmeraldPrimaryLight else MedicalBorder

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        )
    }
}

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onAdd: (
        title: String,
        category: HabitCategory,
        description: String,
        targetFrequency: String,
        scheduledTime: String,
        timeOfDay: String,
        bioscienceRationale: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(HabitCategory.ORAL_LIP) }
    var scheduledTime by remember { mutableStateOf("08:00 AM") }
    var selectedTimeOfDay by remember { mutableStateOf("Morning") }
    var bioscienceRationale by remember { mutableStateOf("") }

    val presetTimes = listOf("07:00 AM", "08:00 AM", "12:30 PM", "06:30 PM", "09:00 PM", "10:30 PM")
    val timeOfDayOptions = listOf("Morning", "Midday", "Evening", "Night")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MedicalNavySurface,
        title = {
            Text(
                text = "Add Custom Protocol",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextWhiteHigh,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Routine / Habit Title", color = TextMediumSlate) },
                    placeholder = { Text("e.g. Zinc Barrier Cream Application", color = TextMutedSlate) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhiteHigh,
                        unfocusedTextColor = TextWhiteHigh,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MedicalBorder
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_habit_title")
                )

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium.copy(color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(HabitCategory.entries) { cat ->
                        CategoryFilterChip(
                            label = cat.displayName,
                            isSelected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            testTag = "dialog_cat_${cat.name.lowercase()}"
                        )
                    }
                }

                Text(
                    text = "Routine Period & Timing",
                    style = MaterialTheme.typography.labelMedium.copy(color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(timeOfDayOptions) { period ->
                        CategoryFilterChip(
                            label = period,
                            isSelected = selectedTimeOfDay == period,
                            onClick = { selectedTimeOfDay = period },
                            testTag = "dialog_tod_${period.lowercase()}"
                        )
                    }
                }

                OutlinedTextField(
                    value = scheduledTime,
                    onValueChange = { scheduledTime = it },
                    label = { Text("Reminder Time (hh:mm AM/PM)", color = TextMediumSlate) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhiteHigh,
                        unfocusedTextColor = TextWhiteHigh,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MedicalBorder
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_habit_time")
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetTimes) { time ->
                        CategoryFilterChip(
                            label = time,
                            isSelected = scheduledTime == time,
                            onClick = { scheduledTime = time },
                            testTag = "preset_time_${time.replace(" ", "_")}"
                        )
                    }
                }

                OutlinedTextField(
                    value = bioscienceRationale,
                    onValueChange = { bioscienceRationale = it },
                    label = { Text("Sequence / Order Rationale", color = TextMediumSlate) },
                    placeholder = { Text("e.g. Cleansed skin before barrier occlusion", color = TextMutedSlate) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhiteHigh,
                        unfocusedTextColor = TextWhiteHigh,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MedicalBorder
                    ),
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_habit_rationale")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(
                            title.trim(),
                            selectedCategory,
                            description.ifBlank { "Custom scheduled routine" },
                            "Daily",
                            scheduledTime.trim().ifBlank { "08:00 AM" },
                            selectedTimeOfDay,
                            bioscienceRationale.ifBlank { "Custom scientific sequence order" }
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color(0xFF003822)
                ),
                modifier = Modifier.testTag("dialog_confirm_add_habit")
            ) {
                Text("Add Protocol", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_cancel_add_habit")
            ) {
                Text("Cancel", color = TextMediumSlate)
            }
        }
    )
}

