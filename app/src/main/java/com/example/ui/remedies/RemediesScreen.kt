package com.example.ui.remedies

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.HomeRemedy
import com.example.ui.AuraHealthViewModel
import com.example.ui.components.AuraTopBar
import com.example.ui.components.ModernSearchBar
import com.example.ui.dictionary.DetailSectionHeader
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
import com.example.ui.theme.MedicalNavySurface
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TextMediumSlate
import com.example.ui.theme.TextMutedSlate
import com.example.ui.theme.TextWhiteHigh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemediesScreen(
    viewModel: AuraHealthViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.remedySearch.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedRemedyCategory.collectAsStateWithLifecycle()
    val remedies by viewModel.filteredRemedies.collectAsStateWithLifecycle()
    val selectedRemedyDetail by viewModel.selectedRemedyDetail.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val categories = listOf("All", "Skin", "Mouth/Lips", "Digestion", "First Aid")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalDeepNavy)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AuraTopBar(
                title = "Home Remedies & OTC",
                subtitle = "Barrier Treatments, Soothers & Rehydration Guide",
                badgeText = "${remedies.size} Guides"
            )

            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                ModernSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.setRemedySearch(it) },
                    placeholderText = "Search active ingredients or indications...",
                    testTag = "remedy_search_input"
                )
            }

            // Categories Filter Pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryFilterChip(
                        label = category,
                        isSelected = selectedCategory.equals(category, ignoreCase = true),
                        onClick = { viewModel.setRemedyCategory(category) },
                        testTag = "remedy_cat_${category.lowercase().replace("/", "_")}"
                    )
                }
            }

            // Remedies List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                items(remedies, key = { it.id }) { remedy ->
                    RemedyCard(
                        remedy = remedy,
                        onClick = { viewModel.viewRemedyDetail(remedy) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (remedies.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No remedies match your search or filter criteria.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextMutedSlate)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Remedy Detail Modal Sheet
        if (selectedRemedyDetail != null) {
            val remedy = selectedRemedyDetail!!
            ModalBottomSheet(
                onDismissRequest = { viewModel.viewRemedyDetail(null) },
                sheetState = sheetState,
                containerColor = MedicalNavySurface,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .size(width = 40.dp, height = 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MedicalBorder)
                    )
                }
            ) {
                RemedyDetailContent(
                    remedy = remedy,
                    onClose = { viewModel.viewRemedyDetail(null) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemedyCard(
    remedy: HomeRemedy,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MedicalBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("remedy_card_${remedy.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = remedy.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhiteHigh,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = remedy.category,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TealAccent,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "View detail",
                    tint = TextMediumSlate,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Ingredient Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MedicalNavyCardElevated)
                    .border(1.dp, MedicalBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Active: ${remedy.activeIngredient}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = remedy.mechanismOfAction,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMediumSlate,
                    lineHeight = 16.sp
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Indications
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                remedy.indications.take(3).forEach { indication ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F2633))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = indication,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldPrimaryLight,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RemedyDetailContent(
    remedy: HomeRemedy,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = remedy.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = TextWhiteHigh,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Category: ${remedy.category}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TealAccent
                    )
                )
            }

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = TextMediumSlate
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active Ingredient Callout
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MedicalNavyCardElevated)
                .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "Pharmacological / Active Ingredient",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EmeraldPrimaryLight,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = remedy.activeIngredient,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextWhiteHigh,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mechanism of Action
        DetailSectionHeader(title = "Mechanism of Action")
        Text(
            text = remedy.mechanismOfAction,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextWhiteHigh,
                lineHeight = 20.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Indications
        DetailSectionHeader(title = "Clinical Indications")
        remedy.indications.forEach { indication ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
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
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = indication,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextMediumSlate,
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // How to Apply / Dosage Guidance
        DetailSectionHeader(title = "Application & Usage Protocol")
        remedy.applicationSteps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(TealAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TealAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextWhiteHigh,
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Precautions Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF26181B))
                .border(1.dp, AlertRose.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = "Precautions",
                    tint = AlertRose,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Precautions & Contraindications",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = AlertRose,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    remedy.precautions.forEach { prec ->
                        Text(
                            text = "• $prec",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextWhiteHigh,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Clinical Pearls Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MedicalNavyCardElevated)
                .border(1.dp, AlertAmber.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Clinical Pearl",
                    tint = AlertAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Clinical Pearl / Dermatologist Tip",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AlertAmber,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = remedy.clinicalPearls,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhiteHigh,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
