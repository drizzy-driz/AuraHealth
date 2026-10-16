package com.example

import com.example.data.model.AssessmentRequest
import com.example.data.model.GeminiModelOption
import com.example.data.model.HabitCategory
import com.example.data.model.HabitItem
import com.example.data.repository.MedicalDataRepository
import com.example.notifications.HabitNotificationScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testMedicalDictionaryHasRequiredConditions() {
        val terms = MedicalDataRepository.medicalTerms
        val ids = terms.map { it.id }.toSet()

        assertTrue("Should contain cheilitis", ids.contains("cheilitis"))
        assertTrue("Should contain angular cheilitis", ids.contains("angular_cheilitis"))
        assertTrue("Should contain folliculitis", ids.contains("folliculitis"))
        assertTrue("Should contain contact dermatitis", ids.contains("contact_dermatitis"))
        assertTrue("Should contain xerostomia", ids.contains("xerostomia"))
        assertTrue("Should contain dehydration", ids.contains("dehydration"))

        terms.forEach { term ->
            assertTrue("${term.id} should have a definition", term.definition.isNotBlank())
            assertTrue("${term.id} should have primary symptoms", term.primarySymptoms.isNotEmpty())
            assertTrue("${term.id} should have causes", term.commonCauses.isNotEmpty())
            assertTrue("${term.id} should have atHomeCare", term.atHomeCare.isNotEmpty())
            assertTrue("${term.id} should have redFlags", term.redFlags.isNotBlank())
        }
    }

    @Test
    fun testHomeRemediesCoverage() {
        val remedies = MedicalDataRepository.homeRemedies
        val ids = remedies.map { it.id }.toSet()

        assertTrue("Should contain petrolatum", ids.contains("petrolatum"))
        assertTrue("Should contain hydrocortisone", ids.contains("hydrocortisone"))
        assertTrue("Should contain salicylate/aloe", ids.contains("salicylate_aloe"))
        assertTrue("Should contain electrolytes", ids.contains("electrolytes_ors"))

        val categories = remedies.map { it.category }.toSet()
        assertTrue(categories.contains("Skin"))
        assertTrue(categories.contains("First Aid"))
        assertTrue(categories.contains("Digestion"))
        assertTrue(categories.contains("Mouth/Lips"))
    }

    @Test
    fun testSymptomAssessmentLipCare() {
        val request = AssessmentRequest(
            selectedSymptoms = setOf("scaling_lips", "cracked_corners"),
            affectedRegion = "Lips & Mouth",
            duration = "3 - 7 days"
        )

        val result = MedicalDataRepository.assessSymptoms(request)
        assertNotNull(result)
        assertTrue(result.primaryMatches.isNotEmpty())

        val firstMatch = result.primaryMatches.first()
        assertTrue(firstMatch.conditionName.contains("Cheilitis"))
        assertTrue(firstMatch.firstLineActions.any { it.contains("petrolatum", ignoreCase = true) })
        assertTrue(firstMatch.thingsToAvoid.any { it.contains("licking", ignoreCase = true) })
    }

    @Test
    fun testSymptomAssessmentFolliculitis() {
        val request = AssessmentRequest(
            selectedSymptoms = setOf("razor_irritation", "skin_bumps"),
            affectedRegion = "Face & Beard",
            duration = "< 48 hours"
        )

        val result = MedicalDataRepository.assessSymptoms(request)
        val match = result.primaryMatches.find { it.conditionName.contains("Razor Bumps") }
        assertNotNull("Should identify razor bumps", match)
        assertTrue(match!!.thingsToAvoid.any { it.contains("grain", ignoreCase = true) })
    }

    @Test
    fun testGeminiModelOptions() {
        val options = GeminiModelOption.entries
        val modelIds = options.map { it.modelId }.toSet()

        assertTrue(modelIds.contains("gemini-3.8-flash"))
        assertTrue(modelIds.contains("gemini-4-argon"))
        assertTrue(modelIds.contains("gemini-3.1-flash-lite"))
    }

    @Test
    fun testHabitNotificationSchedulerConstants() {
        assertEquals("aura_habits_channel", HabitNotificationScheduler.CHANNEL_ID)
        assertEquals("com.example.ACTION_HABIT_REMINDER", HabitNotificationScheduler.ACTION_HABIT_REMINDER)

        val habit = HabitItem(
            id = 1L,
            title = "Tongue Scraping",
            category = HabitCategory.ORAL_LIP,
            description = "Scrape back to front",
            scheduledTime = "07:15 AM",
            orderIndex = 2,
            bioscienceRationale = "Removes bacteria before brushing",
            isReminderEnabled = true
        )
        assertTrue(habit.isReminderEnabled)
        assertEquals("07:15 AM", habit.scheduledTime)
    }
}
