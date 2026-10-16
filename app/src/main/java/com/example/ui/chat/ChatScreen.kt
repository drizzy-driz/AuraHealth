package com.example.ui.chat

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChatMessage
import com.example.data.model.GeminiModelOption
import com.example.data.model.GroundingSource
import com.example.ui.AuraHealthViewModel
import com.example.ui.components.AuraTopBar
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
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: AuraHealthViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isSending by viewModel.isChatSending.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedGeminiModel.collectAsStateWithLifecycle()
    val isSearchGroundingEnabled by viewModel.isSearchGroundingEnabled.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size, isSending) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickQuestions = listOf(
        "Why must flossing happen before brushing?",
        "Why apply lip balm AFTER brushing teeth?",
        "Explain the 3-minute post-shower moisture rule",
        "Why should sunscreen always be the final morning step?",
        "What is the science behind bedtime petrolatum slugging?"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalDeepNavy)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AuraTopBar(
                title = "Aura Bioscience AI",
                subtitle = "Clinical Routine Sequencing & Health Consultation",
                badgeText = selectedModel.badge
            )

            // Model Selection & Search Grounding Controls
            ModelAndGroundingControls(
                selectedModel = selectedModel,
                onSelectModel = { viewModel.setGeminiModel(it) },
                searchGrounding = isSearchGroundingEnabled,
                onToggleSearchGrounding = { viewModel.toggleSearchGrounding() }
            )

            // Scrollable Conversation Thread
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Intro Welcome Card if empty
                if (messages.isEmpty()) {
                    item {
                        IntroChatCard(
                            onSelectPrompt = { prompt ->
                                viewModel.sendChatMessage(prompt)
                            }
                        )
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    ChatMessageBubble(
                        message = msg,
                        onOpenUrl = { url ->
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Handled safely
                            }
                        }
                    )
                }

                if (isSending) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = EmeraldPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Aura AI is evaluating bioscience literature...",
                                style = MaterialTheme.typography.bodySmall.copy(color = CyanAccent)
                            )
                        }
                    }
                }
            }

            // Quick Prompt Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickQuestions) { question ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(MedicalNavyCardElevated)
                            .border(1.dp, MedicalBorder, RoundedCornerShape(100.dp))
                            .clickable {
                                if (!isSending) {
                                    viewModel.sendChatMessage(question)
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = question,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextWhiteHigh,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MedicalNavyCard)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Ask about routine order, chemistry, skin, or oral care...",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMutedSlate)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MedicalBorder,
                        focusedTextColor = TextWhiteHigh,
                        unfocusedTextColor = TextWhiteHigh
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isSending) {
                            val text = inputText
                            inputText = ""
                            viewModel.sendChatMessage(text)
                        }
                    },
                    enabled = inputText.isNotBlank() && !isSending,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isSending) EmeraldPrimary else MedicalNavyCardElevated
                        )
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = if (inputText.isNotBlank() && !isSending) Color(0xFF003822) else TextMutedSlate,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModelAndGroundingControls(
    selectedModel: GeminiModelOption,
    onSelectModel: (GeminiModelOption) -> Unit,
    searchGrounding: Boolean,
    onToggleSearchGrounding: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MedicalNavyCardElevated)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(width = 0.5.dp, color = MedicalBorder)
    ) {
        // Model Selection Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(GeminiModelOption.entries) { model ->
                val isSelected = selectedModel == model
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) EmeraldPrimary.copy(alpha = 0.25f) else MedicalNavyCard
                        )
                        .border(
                            1.dp,
                            if (isSelected) EmeraldPrimary else MedicalBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectModel(model) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("model_${model.name.lowercase()}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (model) {
                                GeminiModelOption.ARGON -> Icons.Default.Psychology
                                GeminiModelOption.FLASH_LITE -> Icons.Default.Speed
                                GeminiModelOption.FLASH -> Icons.Default.AutoAwesome
                            },
                            contentDescription = null,
                            tint = if (isSelected) EmeraldPrimaryLight else TextMediumSlate,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = model.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) EmeraldPrimaryLight else TextMediumSlate,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Google Search Grounding Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = if (searchGrounding) CyanAccent else TextMediumSlate,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Google Search Grounding (Live Medical Literature)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (searchGrounding) CyanAccent else TextMediumSlate,
                        fontWeight = if (searchGrounding) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
            }

            Switch(
                checked = searchGrounding,
                onCheckedChange = { onToggleSearchGrounding() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF003822),
                    checkedTrackColor = EmeraldPrimary,
                    uncheckedThumbColor = TextMutedSlate,
                    uncheckedTrackColor = MedicalNavyCard
                ),
                modifier = Modifier.testTag("search_grounding_switch")
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    onOpenUrl: (String) -> Unit
) {
    val isUser = message.role == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Role & Model Label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            if (!isUser) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = if (isUser) "You" else "Aura Bioscience (${message.modelUsed})",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isUser) TealAccent else EmeraldPrimaryLight,
                    fontSize = 11.sp
                )
            )
        }

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) Color(0xFF0C3B2E) else MedicalNavyCard
                )
                .border(
                    1.dp,
                    if (isUser) EmeraldPrimary.copy(alpha = 0.4f) else MedicalBorder,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextWhiteHigh,
                        lineHeight = 20.sp
                    )
                )

                // Render Grounding Sources if present
                if (message.sources.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Google Search Citations & Verification:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        message.sources.forEach { source ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF0C2433))
                                    .border(0.5.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .clickable { onOpenUrl(source.uri) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = source.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyanAccent,
                                        fontSize = 10.sp
                                    ),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(10.dp)
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
fun IntroChatCard(onSelectPrompt: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MedicalNavyCard)
            .border(1.dp, MedicalBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Welcome to Aura Bioscience AI",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhiteHigh,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Evidence-based hygiene scheduling & clinical rationale",
                        style = MaterialTheme.typography.bodySmall.copy(color = TealAccent)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Ask any question regarding why routines must be ordered in specific sequences so one habit does not degrade another (e.g. toothpaste detergents vs lip lipids, shower moisture lock window, sunscreen barrier mechanics).",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMediumSlate,
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tap a bioscience inquiry below to begin:",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = EmeraldPrimaryLight,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            listOf(
                "Why must interdental flossing happen before toothbrushing?",
                "Why is sunscreen always applied as the final step in the morning?",
                "How does bedtime petrolatum prevent angular cheilitis?"
            ).forEach { prompt ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MedicalNavyCardElevated)
                        .border(1.dp, MedicalBorder, RoundedCornerShape(8.dp))
                        .clickable { onSelectPrompt(prompt) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "• $prompt",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhiteHigh
                        )
                    )
                }
            }
        }
    }
}
