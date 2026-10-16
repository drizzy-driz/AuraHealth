package com.example.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.example.R
import com.example.data.local.HabitEntity
import com.example.data.model.ChatMessage
import com.example.data.model.GroundingSource
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseSyncRepository(private val context: Context) {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore by lazy {
        val dbId = context.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(dbId)
    }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun syncUserProfile() {
        val user = auth.currentUser ?: return
        val userDoc = db.collection("users").document(user.uid)
        val profileData = mapOf(
            "displayName" to (user.displayName ?: "Aura Member"),
            "email" to (user.email ?: ""),
            "photoUrl" to (user.photoUrl?.toString() ?: ""),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            userDoc.set(profileData, SetOptions.merge()).await()
            Log.d("FirebaseSync", "User profile synced successfully")
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Failed to sync user profile: ${e.message}")
        }
    }

    suspend fun syncHabitToCloud(habit: HabitEntity) {
        val uid = getCurrentUserId() ?: return
        val docRef = db.collection("users").document(uid).collection("habits").document(habit.id.toString())
        val habitData = mapOf(
            "title" to habit.title,
            "category" to habit.category,
            "description" to habit.description,
            "targetFrequency" to habit.targetFrequency,
            "scheduledTime" to habit.scheduledTime,
            "orderIndex" to habit.orderIndex,
            "bioscienceRationale" to habit.bioscienceRationale,
            "timeOfDay" to habit.timeOfDay,
            "isCustom" to habit.isCustom,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(habitData, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Failed to sync habit: ${e.message}")
        }
    }

    suspend fun syncHabitLogToCloud(habitId: Long, dateString: String, completed: Boolean) {
        val uid = getCurrentUserId() ?: return
        val docRef = db.collection("users").document(uid).collection("habit_logs").document("${habitId}_$dateString")
        val logData = mapOf(
            "habitId" to habitId,
            "dateString" to dateString,
            "completed" to completed,
            "timestamp" to System.currentTimeMillis()
        )
        try {
            if (completed) {
                docRef.set(logData, SetOptions.merge()).await()
            } else {
                docRef.delete().await()
            }
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Failed to sync habit log: ${e.message}")
        }
    }

    suspend fun syncChatMessageToCloud(message: ChatMessage) {
        val uid = getCurrentUserId() ?: return
        val docRef = db.collection("users").document(uid).collection("chat_messages").document(message.id)
        val msgData = mapOf(
            "id" to message.id,
            "role" to message.role,
            "content" to message.content,
            "modelUsed" to message.modelUsed,
            "sources" to message.sources.map { mapOf("title" to it.title, "uri" to it.uri) },
            "timestamp" to message.timestamp
        )
        try {
            docRef.set(msgData, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Failed to sync chat message: ${e.message}")
        }
    }

    fun signOut(
        credentialManager: CredentialManager,
        scope: CoroutineScope,
        onComplete: () -> Unit
    ) {
        auth.signOut()
        _currentUser.value = null
        scope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.e("FirebaseSync", "Failed to clear credential state", e)
            } finally {
                onComplete()
            }
        }
    }
}
