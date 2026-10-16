package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryTrend
import com.example.data.model.DayTrend
import com.example.data.model.HabitCategory
import com.example.data.model.HabitItem
import com.example.data.model.TimePeriodTrend
import com.example.data.model.WeeklyDashboardData
import com.example.ui.AuraHealthViewModel
import com.example.ui.HabitsViewMode
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanAccentLight
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.InfoSky
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
fun WeeklyProgressDashboard(
    viewModel: AuraHealthViewModel,
    modifier: Modifier = Modifier
) {
    val weeklyData by viewModel.weeklyDashboardData.collectAsStateWithLifecycle()
    val allHabits by viewModel.allHabits.collectAsStateWithLifecycle()
    val selectedDayState by viewModel.selectedDashboardDay.collectAsStateWithLifecycle()

    // Default to today if no day explicitly selected
    val activeSelectedDay = selectedDayState ?: weeklyData.days.lastOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalDeepNavy),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Navigation Header & Mode Selector
        item {
            DashboardHeader(
                onBackToRoutine = { viewModel.setHabitsViewMode(HabitsViewMode.DAILY_PROTOCOL) },
                dateRangeText = if (weeklyData.days.isNotEmpty()) {
                    "${weeklyData.days.first().dayName} - ${weeklyData.days.last().dayName}"
                } else "Past 7 Days"
            )
        }

        // Summary Metric Tiles (4-up statistics grid)
        item {
            WeeklyMetricsGrid(weeklyData = weeklyData)
        }

        // Main Recharts-style Data Visualization Chart
        item {
            WeeklyCompletionTrendsChart(
                weeklyData = weeklyData,
                selectedDay = activeSelectedDay,
                onDaySelected = { viewModel.selectDashboardDay(it) }
            )
        }

        // Selected Day Detailed Inspector
        item {
            if (activeSelectedDay != null) {
                SelectedDayInspectorCard(
                    day = activeSelectedDay,
                    habits = allHabits,
                    onToggleHabit = { habitId, completed ->
                        if (activeSelectedDay.isToday) {
                            viewModel.toggleHabit(habitId, completed)
                        }
                    }
                )
            }
        }

        // Biological Category Adherence Breakdown
        item {
            CategoryAdherenceSection(categoryTrends = weeklyData.categoryTrends)
        }

        // Time of Day Sequence Comparison (Morning vs Midday vs Night)
        item {
            TimePeriodComparisonSection(periodTrends = weeklyData.timePeriodTrends)
        }

        // Scientific Routine Insight
        item {
            BioscienceRoutineInsightCard()
        }
    }
}

@Composable
private fun DashboardHeader(
    onBackToRoutine: () -> Unit,
    dateRangeText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onBackToRoutine,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MedicalNavyCardElevated)
                    .border(1.dp, MedicalBorder, CircleShape)
                    .testTag("back_to_habits_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Return to daily protocol",
                    tint = EmeraldPrimaryLight,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Weekly Analytics",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextWhiteHigh,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                )
                Text(
                    text = "Health & Hygiene Completion Velocity",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMediumSlate
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(MedicalNavyCardElevated)
                .border(1.dp, MedicalBorder, RoundedCornerShape(100.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = TealAccent,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = dateRangeText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TealAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun WeeklyMetricsGrid(weeklyData: WeeklyDashboardData) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Weekly Adherence",
                value = "${weeklyData.averageAdherence.toInt()}%",
                subtitle = "Target: 80% daily",
                icon = Icons.Default.Insights,
                accentColor = EmeraldPrimary,
                modifier = Modifier.weight(1f),
                testTag = "metric_weekly_adherence"
            )

            MetricCard(
                title = "Active Streak",
                value = "${weeklyData.currentStreak} Days",
                subtitle = "Consistent daily flow",
                icon = Icons.Default.LocalFireDepartment,
                accentColor = AlertAmber,
                modifier = Modifier.weight(1f),
                testTag = "metric_active_streak"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Habits Logged",
                value = "${weeklyData.totalCompletions}",
                subtitle = "Of ${weeklyData.totalPossibleCompletions} scheduled",
                icon = Icons.Default.TaskAlt,
                accentColor = CyanAccent,
                modifier = Modifier.weight(1f),
                testTag = "metric_total_habits"
            )

            MetricCard(
                title = "Peak Day",
                value = weeklyData.bestDayName,
                subtitle = "${weeklyData.bestDayPercentage.toInt()}% adherence",
                icon = Icons.Default.EmojiEvents,
                accentColor = InfoSky,
                modifier = Modifier.weight(1f),
                testTag = "metric_peak_day"
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MedicalNavyCard)
            .border(1.dp, MedicalBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMediumSlate,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextWhiteHigh,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun WeeklyCompletionTrendsChart(
    weeklyData: WeeklyDashboardData,
    selectedDay: DayTrend?,
    onDaySelected: (DayTrend) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MedicalNavyCardElevated)
            .border(1.dp, MedicalBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("weekly_trends_chart_card")
    ) {
        Column {
            // Chart Header & Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Weekly Completion Trends",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhiteHigh,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Tap any day to inspect adherence breakdown",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMediumSlate,
                            fontSize = 11.sp
                        )
                    )
                }

                // Legend Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                    )
                    Text(
                        text = "Adherence",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMediumSlate,
                            fontSize = 10.sp
                        )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(2.dp)
                            .background(AlertAmber.copy(alpha = 0.8f))
                    )
                    Text(
                        text = "80% Goal",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AlertAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Chart Canvas & Interactive Bars
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Background Gridlines & 80% Benchmark Line
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Grid lines at 100%, 75%, 50%, 25%, 0%
                    val steps = listOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f)
                    steps.forEach { step ->
                        val y = height * (1f - step)
                        drawLine(
                            color = Color(0xFF1E3557).copy(alpha = 0.4f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    // 80% Benchmark dashed guideline
                    val y80 = height * (1f - 0.8f)
                    drawLine(
                        color = AlertAmber.copy(alpha = 0.5f),
                        start = Offset(0f, y80),
                        end = Offset(width, y80),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }

                // 7 Interactive Vertical Bar Columns
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyData.days.forEach { day ->
                        val isSelected = selectedDay?.dateString == day.dateString
                        DailyBarColumn(
                            day = day,
                            isSelected = isSelected,
                            onSelect = { onDaySelected(day) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Insight Footer
            val selected = selectedDay ?: weeklyData.days.lastOrNull()
            if (selected != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MedicalNavySurface)
                        .border(1.dp, MedicalBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected.percentage >= 80f) EmeraldPrimary else AlertAmber
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${selected.fullDateString}:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextWhiteHigh,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${selected.completedCount}/${selected.totalCount} completed",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EmeraldPrimaryLight,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Text(
                        text = if (selected.percentage >= 80f) "Goal Exceeded 🎯" else "Under 80% Target",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (selected.percentage >= 80f) EmeraldPrimary else AlertAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyBarColumn(
    day: DayTrend,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val barHeightFraction by animateFloatAsState(
        targetValue = (day.percentage / 100f).coerceIn(0.04f, 1.0f),
        animationSpec = tween(durationMillis = 600),
        label = "bar_height"
    )

    val barBorderColor by animateColorAsState(
        targetValue = if (isSelected) EmeraldPrimaryLight else Color.Transparent,
        label = "bar_border"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onSelect)
            .padding(horizontal = 2.dp)
            .testTag("day_bar_${day.dateString}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Value callout above bar
        Text(
            text = "${day.percentage.toInt()}%",
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isSelected) TextWhiteHigh else TextMediumSlate,
                fontSize = 9.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Bar container
        Box(
            modifier = Modifier
                .width(28.dp)
                .weight(1f),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Background track
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(Color(0xFF0F1E33))
            )

            // Animated Fill Bar
            val gradientColors = when {
                day.percentage >= 80f -> listOf(EmeraldPrimaryLight, EmeraldPrimary, TealAccent)
                day.percentage >= 50f -> listOf(CyanAccentLight, CyanAccent, TealAccent)
                else -> listOf(AlertAmber, Color(0xFFD97706))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(barHeightFraction)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(Brush.verticalGradient(gradientColors))
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = barBorderColor,
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Day label (e.g. Mon, Tue, etc.)
        Text(
            text = day.dayName,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (day.isToday) EmeraldPrimaryLight else if (isSelected) TextWhiteHigh else TextMediumSlate,
                fontSize = 10.sp,
                fontWeight = if (day.isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )

        // Day Number (e.g. 15, 16)
        Text(
            text = day.dayNumber,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isSelected) CyanAccent else TextMutedSlate,
                fontSize = 9.sp
            )
        )

        if (day.isToday) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary)
            )
        }
    }
}

@Composable
fun SelectedDayInspectorCard(
    day: DayTrend,
    habits: List<HabitItem>,
    onToggleHabit: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedList by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MedicalNavyCard)
            .border(1.dp, MedicalBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("day_inspector_card")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldGlow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = day.fullDateString,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextWhiteHigh,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            if (day.isToday) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldPrimary.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "TODAY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = EmeraldPrimaryLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }

                        Text(
                            text = "${day.completedCount} of ${day.totalCount} habits completed (${day.percentage.toInt()}%)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMediumSlate
                            )
                        )
                    }
                }

                // Circular Progress Indicator for selected day
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { day.percentage / 100f },
                        modifier = Modifier.size(46.dp),
                        color = if (day.percentage >= 80f) EmeraldPrimary else AlertAmber,
                        trackColor = MedicalBorder,
                        strokeWidth = 4.dp
                    )
                    Text(
                        text = "${day.percentage.toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextWhiteHigh,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Expandable Habit Items List
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MedicalNavyCardElevated)
                    .clickable { expandedList = !expandedList }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (expandedList) "Hide Habit Verification Checklist" else "View Daily Habits Breakdown (${day.completedCount}/${day.totalCount})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = if (expandedList) "▲" else "▼",
                    color = CyanAccent,
                    fontSize = 12.sp
                )
            }

            AnimatedVisibility(visible = expandedList) {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    habits.forEach { habit ->
                        val isDone = if (day.isToday) {
                            habit.isCompletedToday
                        } else {
                            day.completedHabitIds.contains(habit.id)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF091424))
                                .border(0.5.dp, MedicalBorder, RoundedCornerShape(8.dp))
                                .clickable(enabled = day.isToday) {
                                    onToggleHabit(habit.id, isDone)
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDone) EmeraldPrimary else Color.Transparent
                                        )
                                        .border(
                                            1.dp,
                                            if (isDone) EmeraldPrimary else TextMutedSlate,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF003822),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = habit.title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isDone) TextWhiteHigh else TextMediumSlate,
                                            fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Step ${habit.orderIndex} • ${habit.scheduledTime} (${habit.timeOfDay})",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextMutedSlate,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            if (day.isToday) {
                                Text(
                                    text = if (isDone) "Done" else "Tap to log",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isDone) EmeraldPrimaryLight else TealAccent,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryAdherenceSection(
    categoryTrends: List<CategoryTrend>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MedicalNavyCard)
            .border(1.dp, MedicalBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Biological Category Coverage",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhiteHigh,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "7-Day completion rate per physiological domain",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMediumSlate,
                            fontSize = 11.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(TealAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = TealAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                categoryTrends.forEach { trend ->
                    CategoryTrendItem(trend = trend)
                }
            }
        }
    }
}

@Composable
private fun CategoryTrendItem(trend: CategoryTrend) {
    val categoryLabel = when (trend.category) {
        HabitCategory.ORAL_LIP -> "🦷 Oral & Lip Barrier"
        HabitCategory.HYDRATION_WELLNESS -> "💧 Cellular Hydration & Circadian"
        HabitCategory.BODY_SANITATION -> "🧼 Sanitation & Body Barrier"
        HabitCategory.GROOMING_SKIN -> "✨ Skin, Hair & UV Defense"
    }

    val barColor = when (trend.category) {
        HabitCategory.ORAL_LIP -> EmeraldPrimary
        HabitCategory.HYDRATION_WELLNESS -> CyanAccent
        HabitCategory.BODY_SANITATION -> TealAccent
        HabitCategory.GROOMING_SKIN -> InfoSky
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = categoryLabel,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextWhiteHigh,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${trend.completedCount}/${trend.totalPossibleCount}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMediumSlate,
                        fontSize = 11.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${trend.percentage.toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = barColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { (trend.percentage / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = Color(0xFF0F1E33)
        )
    }
}

@Composable
fun TimePeriodComparisonSection(
    periodTrends: List<TimePeriodTrend>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MedicalNavyCard)
            .border(1.dp, MedicalBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Text(
                text = "Time-of-Day Rhythm Consistency",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextWhiteHigh,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Comparing morning, afternoon, and nocturnal hygiene compliance",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMediumSlate,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                periodTrends.forEach { trend ->
                    val (icon, color) = when (trend.timeOfDay.lowercase()) {
                        "morning" -> Icons.Default.WbSunny to AlertAmber
                        "midday" -> Icons.Default.LightMode to CyanAccent
                        else -> Icons.Default.NightsStay to InfoSky
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MedicalNavyCardElevated)
                            .border(1.dp, MedicalBorder, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = trend.timeOfDay,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextWhiteHigh,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${trend.percentage.toInt()}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = color,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "${trend.completedCount} steps",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextMutedSlate,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BioscienceRoutineInsightCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0F2B3E),
                        Color(0xFF091C2C)
                    )
                )
            )
            .border(1.dp, TealAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(TealAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = TealAccent,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Why Sequential Consistency Matters",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextWhiteHigh,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Weekly adherence above 80% maintains the protective fluorapatite reservoir on tooth enamel, prevents trans-epidermal water loss (TEWL) after bathing, and safeguards pineal melatonin synthesis before sleep.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMediumSlate,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}
