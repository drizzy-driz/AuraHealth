package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppTab
import com.example.ui.AuraHealthViewModel
import com.example.ui.HabitsViewMode
import com.example.ui.chat.ChatScreen
import com.example.ui.dictionary.DictionaryScreen
import com.example.ui.habits.HabitsScreen
import com.example.ui.remedies.RemediesScreen
import com.example.ui.symptom.SymptomAssessmentScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MedicalBorder
import com.example.ui.theme.MedicalDeepNavy
import com.example.ui.theme.MedicalNavySurface
import com.example.ui.theme.TextMediumSlate

data class NavigationItem(
    val tab: AppTab,
    val icon: ImageVector,
    val label: String
)

@Composable
fun MainScreen(
    viewModel: AuraHealthViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val habitsViewMode by viewModel.habitsViewMode.collectAsStateWithLifecycle()
    val selectedTermDetail by viewModel.selectedTermDetail.collectAsStateWithLifecycle()
    val selectedRemedyDetail by viewModel.selectedRemedyDetail.collectAsStateWithLifecycle()
    val assessmentResult by viewModel.assessmentResult.collectAsStateWithLifecycle()

    // Handle system back navigation cleanly
    BackHandler(
        enabled = (currentTab == AppTab.HABITS && habitsViewMode == HabitsViewMode.WEEKLY_DASHBOARD) ||
                currentTab != AppTab.HABITS ||
                selectedTermDetail != null ||
                selectedRemedyDetail != null ||
                assessmentResult != null
    ) {
        when {
            selectedTermDetail != null -> viewModel.viewTermDetail(null)
            selectedRemedyDetail != null -> viewModel.viewRemedyDetail(null)
            assessmentResult != null -> viewModel.resetAssessment()
            currentTab == AppTab.HABITS && habitsViewMode == HabitsViewMode.WEEKLY_DASHBOARD -> {
                viewModel.setHabitsViewMode(HabitsViewMode.DAILY_PROTOCOL)
            }
            currentTab != AppTab.HABITS -> viewModel.setTab(AppTab.HABITS)
        }
    }

    val navItems = listOf(
        NavigationItem(AppTab.HABITS, Icons.Default.TaskAlt, "Habits"),
        NavigationItem(AppTab.CHAT, Icons.Default.AutoAwesome, "Aura AI"),
        NavigationItem(AppTab.DICTIONARY, Icons.Default.MenuBook, "Dictionary"),
        NavigationItem(AppTab.SYMPTOMS, Icons.Default.HealthAndSafety, "Symptom Guide"),
        NavigationItem(AppTab.REMEDIES, Icons.Default.Healing, "Remedies")
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MedicalDeepNavy,
        bottomBar = {
            NavigationBar(
                containerColor = MedicalNavySurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MedicalBorder
                    )
            ) {
                navItems.forEach { item ->
                    val isSelected = currentTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF003822),
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = EmeraldPrimary,
                            unselectedIconColor = TextMediumSlate,
                            unselectedTextColor = TextMediumSlate
                        ),
                        modifier = Modifier.testTag(item.tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MedicalDeepNavy)
        ) {
            when (currentTab) {
                AppTab.HABITS -> HabitsScreen(viewModel = viewModel)
                AppTab.CHAT -> ChatScreen(viewModel = viewModel)
                AppTab.DICTIONARY -> DictionaryScreen(viewModel = viewModel)
                AppTab.SYMPTOMS -> SymptomAssessmentScreen(viewModel = viewModel)
                AppTab.REMEDIES -> RemediesScreen(viewModel = viewModel)
            }
        }
    }
}
