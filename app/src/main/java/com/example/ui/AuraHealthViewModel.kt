package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AssessmentRequest
import com.example.data.model.AssessmentResult
import com.example.data.model.ChatMessage
import com.example.data.model.DayTrend
import com.example.data.model.GeminiModelOption
import com.example.data.model.HabitCategory
import com.example.data.model.HabitItem
import com.example.data.model.HomeRemedy
import com.example.data.model.MedicalTerm
import com.example.data.model.WeeklyDashboardData
import com.example.data.remote.GeminiService
import com.example.data.repository.FirebaseSyncRepository
import com.example.data.repository.HabitRepository
import com.example.data.repository.MedicalDataRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val testTag: String) {
    HABITS("Habits", "tab_habits"),
    CHAT("Aura AI", "tab_chat"),
    DICTIONARY("Dictionary", "tab_dictionary"),
    SYMPTOMS("Symptom Guide", "tab_symptoms"),
    REMEDIES("Remedies", "tab_remedies")
}

enum class HabitsViewMode {
    DAILY_PROTOCOL,
    WEEKLY_DASHBOARD
}

class AuraHealthViewModel(
    private val habitRepository: HabitRepository,
    val firebaseSync: FirebaseSyncRepository,
    private val geminiService: GeminiService = GeminiService()
) : ViewModel() {

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.HABITS)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // --- Firebase Auth User ---
    val currentUser: StateFlow<FirebaseUser?> = firebaseSync.currentUser

    // --- Module 1: Habits Tracker with Bioscience Ordering & Times ---
    val allHabits: StateFlow<List<HabitItem>> = habitRepository.getHabitItemsWithTodayStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedHabitCategory = MutableStateFlow<HabitCategory?>(null)
    val selectedHabitCategory: StateFlow<HabitCategory?> = _selectedHabitCategory.asStateFlow()

    private val _selectedTimeFilter = MutableStateFlow("All Times")
    val selectedTimeFilter: StateFlow<String> = _selectedTimeFilter.asStateFlow()

    fun selectHabitCategory(category: HabitCategory?) {
        _selectedHabitCategory.value = category
    }

    fun selectTimeFilter(time: String) {
        _selectedTimeFilter.value = time
    }

    val filteredHabits: StateFlow<List<HabitItem>> = combine(
        allHabits,
        selectedHabitCategory,
        selectedTimeFilter
    ) { habits, category, timeFilter ->
        habits.filter { habit ->
            val matchCategory = category == null || habit.category == category
            val matchTime = timeFilter == "All Times" || habit.timeOfDay.equals(timeFilter, ignoreCase = true)
            matchCategory && matchTime
        }.sortedBy { it.orderIndex }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completionStats = allHabits.map { habits ->
        val total = habits.size
        val completed = habits.count { it.isCompletedToday }
        val percentage = if (total > 0) (completed.toFloat() / total.toFloat()) * 100f else 0f
        Triple(completed, total, percentage)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(0, 0, 0f))

    // Habits View Mode (Daily checklist vs. Weekly analytics dashboard)
    private val _habitsViewMode = MutableStateFlow(HabitsViewMode.DAILY_PROTOCOL)
    val habitsViewMode: StateFlow<HabitsViewMode> = _habitsViewMode.asStateFlow()

    fun setHabitsViewMode(mode: HabitsViewMode) {
        _habitsViewMode.value = mode
    }

    // Weekly Progress & Completion Trends Data
    val weeklyDashboardData: StateFlow<WeeklyDashboardData> = habitRepository.getWeeklyTrends()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WeeklyDashboardData()
        )

    private val _selectedDashboardDay = MutableStateFlow<DayTrend?>(null)
    val selectedDashboardDay: StateFlow<DayTrend?> = _selectedDashboardDay.asStateFlow()

    fun selectDashboardDay(day: DayTrend?) {
        _selectedDashboardDay.value = day
    }

    fun toggleHabit(habitId: Long, currentlyCompleted: Boolean) {
        viewModelScope.launch {
            habitRepository.toggleHabitCompletion(habitId, !currentlyCompleted)
        }
    }

    fun addCustomHabit(
        title: String,
        category: HabitCategory,
        description: String,
        targetFrequency: String,
        scheduledTime: String = "08:00 AM",
        timeOfDay: String = "Morning",
        bioscienceRationale: String = "",
        context: android.content.Context? = null
    ) {
        viewModelScope.launch {
            habitRepository.addCustomHabit(
                title = title,
                category = category,
                description = description,
                targetFrequency = targetFrequency,
                scheduledTime = scheduledTime,
                timeOfDay = timeOfDay,
                bioscienceRationale = bioscienceRationale,
                context = context
            )
        }
    }

    fun deleteHabit(habitId: Long, context: android.content.Context? = null) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habitId, context)
        }
    }

    fun toggleHabitReminder(context: android.content.Context, habit: HabitItem) {
        viewModelScope.launch {
            habitRepository.toggleHabitReminder(context, habit)
        }
    }

    fun triggerTestNotification(context: android.content.Context) {
        com.example.notifications.HabitNotificationScheduler.triggerInstantTestReminder(context)
    }

    // --- Gemini AI Chatbot Module ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatSending = MutableStateFlow(false)
    val isChatSending: StateFlow<Boolean> = _isChatSending.asStateFlow()

    private val _selectedGeminiModel = MutableStateFlow(GeminiModelOption.FLASH)
    val selectedGeminiModel: StateFlow<GeminiModelOption> = _selectedGeminiModel.asStateFlow()

    private val _isSearchGroundingEnabled = MutableStateFlow(true)
    val isSearchGroundingEnabled: StateFlow<Boolean> = _isSearchGroundingEnabled.asStateFlow()

    fun setGeminiModel(model: GeminiModelOption) {
        _selectedGeminiModel.value = model
    }

    fun toggleSearchGrounding() {
        _isSearchGroundingEnabled.value = !_isSearchGroundingEnabled.value
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank() || _isChatSending.value) return

        val userMessage = ChatMessage(
            role = "user",
            content = userText.trim(),
            modelUsed = _selectedGeminiModel.value.modelId
        )

        val updatedList = _chatMessages.value + userMessage
        _chatMessages.value = updatedList
        _isChatSending.value = true

        viewModelScope.launch {
            firebaseSync.syncChatMessageToCloud(userMessage)

            val result = geminiService.sendMessage(
                history = updatedList.dropLast(1),
                userMessage = userText.trim(),
                modelOption = _selectedGeminiModel.value,
                enableSearchGrounding = _isSearchGroundingEnabled.value
            )

            result.onSuccess { modelReply ->
                _chatMessages.value = _chatMessages.value + modelReply
                _isChatSending.value = false
                firebaseSync.syncChatMessageToCloud(modelReply)
            }.onFailure { error ->
                val errorMessage = ChatMessage(
                    role = "model",
                    content = "Bioscience Consultation Notice: ${error.localizedMessage ?: "Unable to connect to Gemini service. Please verify your connection or API key."}",
                    modelUsed = _selectedGeminiModel.value.modelId
                )
                _chatMessages.value = _chatMessages.value + errorMessage
                _isChatSending.value = false
            }
        }
    }

    // --- Module 2: Medical Reference Dictionary ---
    private val _dictionarySearch = MutableStateFlow("")
    val dictionarySearch: StateFlow<String> = _dictionarySearch.asStateFlow()

    private val _selectedDictionaryCategory = MutableStateFlow("All")
    val selectedDictionaryCategory: StateFlow<String> = _selectedDictionaryCategory.asStateFlow()

    private val _selectedTermDetail = MutableStateFlow<MedicalTerm?>(null)
    val selectedTermDetail: StateFlow<MedicalTerm?> = _selectedTermDetail.asStateFlow()

    private val _showOnlyBookmarks = MutableStateFlow(false)
    val showOnlyBookmarks: StateFlow<Boolean> = _showOnlyBookmarks.asStateFlow()

    val bookmarkedTermIds: StateFlow<Set<String>> = habitRepository.allBookmarks
        .map { list -> list.map { it.termId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val filteredMedicalTerms: StateFlow<List<MedicalTerm>> = combine(
        _dictionarySearch,
        _selectedDictionaryCategory,
        _showOnlyBookmarks,
        bookmarkedTermIds
    ) { query, category, onlyBookmarks, bookmarks ->
        MedicalDataRepository.medicalTerms.filter { term ->
            val matchesQuery = query.isBlank() ||
                    term.name.contains(query, ignoreCase = true) ||
                    term.alternativeName.contains(query, ignoreCase = true) ||
                    term.definition.contains(query, ignoreCase = true) ||
                    term.primarySymptoms.any { it.contains(query, ignoreCase = true) } ||
                    term.commonCauses.any { it.contains(query, ignoreCase = true) }

            val matchesCategory = category == "All" || term.category.contains(category, ignoreCase = true)
            val matchesBookmark = !onlyBookmarks || bookmarks.contains(term.id)

            matchesQuery && matchesCategory && matchesBookmark
        }.sortedBy { it.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MedicalDataRepository.medicalTerms)

    fun setDictionarySearch(query: String) {
        _dictionarySearch.value = query
    }

    fun setDictionaryCategory(category: String) {
        _selectedDictionaryCategory.value = category
    }

    fun toggleShowOnlyBookmarks() {
        _showOnlyBookmarks.value = !_showOnlyBookmarks.value
    }

    fun viewTermDetail(term: MedicalTerm?) {
        _selectedTermDetail.value = term
    }

    fun toggleBookmarkTerm(termId: String) {
        viewModelScope.launch {
            val isBookmarked = bookmarkedTermIds.value.contains(termId)
            habitRepository.toggleBookmark(termId, isBookmarked)
        }
    }

    // --- Module 3: Symptom Assessment & First-Line Guidance ---
    private val _selectedSymptoms = MutableStateFlow<Set<String>>(emptySet())
    val selectedSymptoms: StateFlow<Set<String>> = _selectedSymptoms.asStateFlow()

    private val _affectedRegion = MutableStateFlow("Lips & Mouth")
    val affectedRegion: StateFlow<String> = _affectedRegion.asStateFlow()

    private val _symptomDuration = MutableStateFlow("3 - 7 days")
    val symptomDuration: StateFlow<String> = _symptomDuration.asStateFlow()

    private val _assessmentResult = MutableStateFlow<AssessmentResult?>(null)
    val assessmentResult: StateFlow<AssessmentResult?> = _assessmentResult.asStateFlow()

    fun toggleSymptomSelection(symptomId: String) {
        val current = _selectedSymptoms.value.toMutableSet()
        if (current.contains(symptomId)) {
            current.remove(symptomId)
        } else {
            current.add(symptomId)
        }
        _selectedSymptoms.value = current
    }

    fun setAffectedRegion(region: String) {
        _affectedRegion.value = region
    }

    fun setSymptomDuration(duration: String) {
        _symptomDuration.value = duration
    }

    fun runAssessment() {
        if (_selectedSymptoms.value.isEmpty()) return
        val result = MedicalDataRepository.assessSymptoms(
            AssessmentRequest(
                selectedSymptoms = _selectedSymptoms.value,
                affectedRegion = _affectedRegion.value,
                duration = _symptomDuration.value
            )
        )
        _assessmentResult.value = result
    }

    fun resetAssessment() {
        _selectedSymptoms.value = emptySet()
        _affectedRegion.value = "Lips & Mouth"
        _symptomDuration.value = "3 - 7 days"
        _assessmentResult.value = null
    }

    // --- Module 4: Non-Prescription & Home Remedy Guide ---
    private val _remedySearch = MutableStateFlow("")
    val remedySearch: StateFlow<String> = _remedySearch.asStateFlow()

    private val _selectedRemedyCategory = MutableStateFlow("All")
    val selectedRemedyCategory: StateFlow<String> = _selectedRemedyCategory.asStateFlow()

    private val _selectedRemedyDetail = MutableStateFlow<HomeRemedy?>(null)
    val selectedRemedyDetail: StateFlow<HomeRemedy?> = _selectedRemedyDetail.asStateFlow()

    val filteredRemedies: StateFlow<List<HomeRemedy>> = combine(
        _remedySearch,
        _selectedRemedyCategory
    ) { query, category ->
        MedicalDataRepository.homeRemedies.filter { remedy ->
            val matchesQuery = query.isBlank() ||
                    remedy.name.contains(query, ignoreCase = true) ||
                    remedy.activeIngredient.contains(query, ignoreCase = true) ||
                    remedy.indications.any { it.contains(query, ignoreCase = true) } ||
                    remedy.mechanismOfAction.contains(query, ignoreCase = true)

            val matchesCategory = category == "All" || remedy.category.equals(category, ignoreCase = true)

            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MedicalDataRepository.homeRemedies)

    fun setRemedySearch(query: String) {
        _remedySearch.value = query
    }

    fun setRemedyCategory(category: String) {
        _selectedRemedyCategory.value = category
    }

    fun viewRemedyDetail(remedy: HomeRemedy?) {
        _selectedRemedyDetail.value = remedy
    }

    init {
        viewModelScope.launch {
            habitRepository.initializeDefaultHabitsIfEmpty()
        }
    }

    companion object {
        fun provideFactory(
            repository: HabitRepository,
            firebaseSync: FirebaseSyncRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuraHealthViewModel(repository, firebaseSync) as T
                }
            }
    }
}
