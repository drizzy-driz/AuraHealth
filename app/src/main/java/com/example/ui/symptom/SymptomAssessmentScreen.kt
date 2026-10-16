package com.example.ui.symptom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AssessmentResult
import com.example.data.model.ConditionMatch
import com.example.data.repository.MedicalDataRepository
import com.example.ui.AuraHealthViewModel
import com.example.ui.components.AuraTopBar
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.habits.CategoryFilterChip
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AlertRose
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.MedicalBorder
import com.example.ui.theme.MedicalDeepNavy
import com.example.ui.theme.MedicalNavyCard
import com.example.ui.theme.MedicalNavyCardElevated
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TextMediumSlate
import com.example.ui.theme.TextMutedSlate
import com.example.ui.theme.TextWhiteHigh

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SymptomAssessmentScreen(
    viewModel: AuraHealthViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSymptoms by viewModel.selectedSymptoms.collectAsStateWithLifecycle()
    val affectedRegion by viewModel.affectedRegion.collectAsStateWithLifecycle()
    val symptomDuration by viewModel.symptomDuration.collectAsStateWithLifecycle()
    val assessmentResult by viewModel.assessmentResult.collectAsStateWithLifecycle()

    val regions = listOf("Lips & Mouth", "Face & Beard", "Scalp", "Body & Neck", "Hands & Nails")
    val durations = listOf("< 48 hours", "3 - 7 days", "> 1 week")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalDeepNavy)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AuraTopBar(
                title = "Symptom Assessment",
                subtitle = "Guided Clinical Questionnaire & First-Line Care",
                badgeText = "Diagnostic Aid"
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Prominent Medical Disclaimer Banner MANDATED on top of all assessments
                item {
                    MedicalDisclaimerBanner(
                        modifier = Modifier.testTag("symptom_disclaimer_banner")
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // If assessment result is present, display full clinical breakdown
                if (assessmentResult != null) {
                    item {
                        AssessmentResultView(
                            result = assessmentResult!!,
                            onReset = { viewModel.resetAssessment() }
                        )
                    }
                } else {
                    // Questionnaire Section 1: Symptom Selection
                    item {
                        Text(
                            text = "Step 1: Select Active Symptoms",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhiteHigh,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Choose all symptoms currently affecting your skin, lips, or oral mucosa:",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMediumSlate)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MedicalDataRepository.symptomsList.forEach { symptom ->
                                val isSelected = selectedSymptoms.contains(symptom.id)
                                SymptomSelectableChip(
                                    label = symptom.label,
                                    isSelected = isSelected,
                                    onClick = { viewModel.toggleSymptomSelection(symptom.id) },
                                    testTag = "symptom_chip_${symptom.id}"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Questionnaire Section 2: Affected Anatomical Region
                    item {
                        Text(
                            text = "Step 2: Primary Affected Region",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhiteHigh,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(regions) { region ->
                                CategoryFilterChip(
                                    label = region,
                                    isSelected = affectedRegion == region,
                                    onClick = { viewModel.setAffectedRegion(region) },
                                    testTag = "region_chip_${region.lowercase().replace(" ", "_")}"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Questionnaire Section 3: Symptom Duration
                    item {
                        Text(
                            text = "Step 3: Duration of Discomfort",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhiteHigh,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(durations) { dur ->
                                CategoryFilterChip(
                                    label = dur,
                                    isSelected = symptomDuration == dur,
                                    onClick = { viewModel.setSymptomDuration(dur) },
                                    testTag = "duration_chip_${dur.replace(" ", "_")}"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Generate Assessment Button
                    item {
                        Button(
                            onClick = { viewModel.runAssessment() },
                            enabled = selectedSymptoms.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                disabledContainerColor = MedicalNavyCardElevated
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("run_assessment_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = if (selectedSymptoms.isNotEmpty()) Color(0xFF003822) else TextMutedSlate
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedSymptoms.isNotEmpty())
                                    "Generate Clinical Guidance (${selectedSymptoms.size} symptoms)"
                                else
                                    "Select at least 1 symptom above",
                                color = if (selectedSymptoms.isNotEmpty()) Color(0xFF003822) else TextMutedSlate,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(96.dp))
                }
            }
        }
    }
}

@Composable
fun SymptomSelectableChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) EmeraldPrimary.copy(alpha = 0.25f) else MedicalNavyCard
            )
            .border(
                1.dp,
                if (isSelected) EmeraldPrimary else MedicalBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp)
            .testTag(testTag)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isSelected) EmeraldPrimaryLight else TextWhiteHigh,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}

@Composable
fun AssessmentResultView(
    result: AssessmentResult,
    onReset: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Results Header with Reset Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Clinical Evaluation Summary",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextWhiteHigh,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Potential matches based on symptom pattern analysis",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMediumSlate)
                )
            }

            OutlinedButton(
                onClick = onReset,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalBorder)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("reset_assessment_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Check", fontSize = 12.sp)
            }
        }

        // Urgent Warning if symptoms persisted > 1 week
        if (result.urgentCareWarning != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF2E1A1A))
                    .border(1.dp, AlertRose, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = "Alert",
                        tint = AlertRose,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = result.urgentCareWarning,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhiteHigh,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        // Primary Matched Conditions
        Text(
            text = "Potential Conditions & Etiology",
            style = MaterialTheme.typography.titleMedium.copy(
                color = EmeraldPrimaryLight,
                fontWeight = FontWeight.Bold
            )
        )

        result.primaryMatches.forEach { match ->
            ConditionMatchCard(match = match)
        }

        // General First-Line Care Steps
        Card(
            colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MedicalBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Recommended First-Line Care Steps",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextWhiteHigh,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                result.generalFirstLineCare.forEach { step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextMediumSlate,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }
        }

        // Common Triggers To Avoid
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F2E)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AlertRose.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Critical Habits & Triggers to Avoid",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AlertRose,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                result.commonTriggersToAvoid.forEach { trigger ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            tint = AlertRose,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = trigger,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextWhiteHigh,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConditionMatchCard(match: ConditionMatch) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MedicalBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = match.conditionName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhiteHigh,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = match.alternativeName,
                        style = MaterialTheme.typography.bodySmall.copy(color = TealAccent)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            when (match.matchRelevance) {
                                "High Match" -> EmeraldPrimary.copy(alpha = 0.2f)
                                else -> CyanAccent.copy(alpha = 0.2f)
                            }
                        )
                        .border(
                            1.dp,
                            when (match.matchRelevance) {
                                "High Match" -> EmeraldPrimary
                                else -> CyanAccent
                            },
                            RoundedCornerShape(100.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = match.matchRelevance,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (match.matchRelevance == "High Match") EmeraldPrimaryLight else CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = match.whyMatched,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMediumSlate,
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Targeted Actions:",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            match.firstLineActions.forEach { action ->
                Text(
                    text = "• $action",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextWhiteHigh,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF26181B))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Red Flag: ${match.redFlagSigns}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AlertRose,
                        lineHeight = 14.sp
                    )
                )
            }
        }
    }
}
