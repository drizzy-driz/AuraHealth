package com.example.ui.dictionary

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
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
import com.example.data.model.MedicalTerm
import com.example.ui.AuraHealthViewModel
import com.example.ui.components.AuraTopBar
import com.example.ui.components.ModernSearchBar
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
fun DictionaryScreen(
    viewModel: AuraHealthViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.dictionarySearch.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedDictionaryCategory.collectAsStateWithLifecycle()
    val terms by viewModel.filteredMedicalTerms.collectAsStateWithLifecycle()
    val selectedTermDetail by viewModel.selectedTermDetail.collectAsStateWithLifecycle()
    val bookmarkedIds by viewModel.bookmarkedTermIds.collectAsStateWithLifecycle()
    val showOnlyBookmarks by viewModel.showOnlyBookmarks.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val categories = listOf("All", "Oral & Lips", "Skin & Face", "Body & Systemic")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalDeepNavy)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AuraTopBar(
                title = "Medical Dictionary",
                subtitle = "Offline Clinical Reference & Anatomy Guide",
                badgeText = "${terms.size} Entries"
            )

            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                ModernSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.setDictionarySearch(it) },
                    placeholderText = "Search conditions, symptoms, causes...",
                    testTag = "dictionary_search_input"
                )
            }

            // Categories & Bookmark Filter
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
                        isSelected = selectedCategory == category && !showOnlyBookmarks,
                        onClick = {
                            if (showOnlyBookmarks) viewModel.toggleShowOnlyBookmarks()
                            viewModel.setDictionaryCategory(category)
                        },
                        testTag = "dict_cat_${category.lowercase().replace(" ", "_")}"
                    )
                }

                item {
                    CategoryFilterChip(
                        label = "★ Saved (${bookmarkedIds.size})",
                        isSelected = showOnlyBookmarks,
                        onClick = { viewModel.toggleShowOnlyBookmarks() },
                        testTag = "dict_cat_saved"
                    )
                }
            }

            // A-Z Term List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                items(terms, key = { it.id }) { term ->
                    val isBookmarked = bookmarkedIds.contains(term.id)
                    MedicalTermCard(
                        term = term,
                        isBookmarked = isBookmarked,
                        onBookmarkToggle = { viewModel.toggleBookmarkTerm(term.id) },
                        onClick = { viewModel.viewTermDetail(term) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (terms.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No medical reference entries match your query.",
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

        // Detailed Modal Bottom Sheet for Medical Term
        if (selectedTermDetail != null) {
            val term = selectedTermDetail!!
            val isBookmarked = bookmarkedIds.contains(term.id)

            ModalBottomSheet(
                onDismissRequest = { viewModel.viewTermDetail(null) },
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
                MedicalTermDetailContent(
                    term = term,
                    isBookmarked = isBookmarked,
                    onBookmarkToggle = { viewModel.toggleBookmarkTerm(term.id) },
                    onClose = { viewModel.viewTermDetail(null) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MedicalTermCard(
    term: MedicalTerm,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MedicalBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("term_card_${term.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = term.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhiteHigh,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (term.alternativeName.isNotEmpty()) {
                        Text(
                            text = term.alternativeName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TealAccent,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.testTag("bookmark_${term.id}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) AlertAmber else TextMediumSlate
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "View details",
                        tint = TextMediumSlate,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = term.definition,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMediumSlate,
                    lineHeight = 16.sp
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Symptoms Pills
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                term.primarySymptoms.take(3).forEach { symptom ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MedicalNavyCardElevated)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = symptom,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanAccent,
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
fun MedicalTermDetailContent(
    term: MedicalTerm,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title & Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = term.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = TextWhiteHigh,
                        fontWeight = FontWeight.Bold
                    )
                )
                if (term.alternativeName.isNotEmpty()) {
                    Text(
                        text = term.alternativeName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TealAccent
                        )
                    )
                }
            }

            Row {
                IconButton(onClick = onBookmarkToggle) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save Bookmark",
                        tint = if (isBookmarked) AlertAmber else TextMediumSlate
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Detail",
                        tint = TextMediumSlate
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Region Tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(MedicalNavyCardElevated)
                .border(1.dp, MedicalBorder, RoundedCornerShape(100.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text = "Anatomy: ${term.anatomicalRegion}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyanAccent,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Definition
        DetailSectionHeader(title = "Clinical Definition")
        Text(
            text = term.definition,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextWhiteHigh,
                lineHeight = 20.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Primary Symptoms
        DetailSectionHeader(title = "Primary Symptoms")
        term.primarySymptoms.forEach { symptom ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = symptom,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextMediumSlate,
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Common Causes
        DetailSectionHeader(title = "Common Causes & Etiology")
        term.commonCauses.forEach { cause ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(TealAccent)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = cause,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextMediumSlate,
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // At-Home First Line Care
        DetailSectionHeader(title = "At-Home First Line Care")
        term.atHomeCare.forEach { care ->
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
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = care,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextWhiteHigh,
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Red Flags Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF261218))
                .border(1.dp, AlertRose.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = "Red Flags",
                    tint = AlertRose,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Red Flags (Seek Medical Care)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = AlertRose,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = term.redFlags,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhiteHigh,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Prevention Tip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MedicalNavyCardElevated)
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Prevention Tip",
                    tint = CyanAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Clinical Prevention Rule",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = term.preventionTip,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMediumSlate
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun DetailSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            color = EmeraldPrimaryLight,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp
        ),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}
