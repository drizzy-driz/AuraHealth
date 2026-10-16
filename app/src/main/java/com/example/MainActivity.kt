package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppDatabase
import com.example.data.repository.FirebaseSyncRepository
import com.example.data.repository.HabitRepository
import com.example.ui.AuraHealthViewModel
import com.example.ui.auth.attemptAutoSignIn
import com.example.ui.navigation.MainScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: AuraHealthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val firebaseSync = FirebaseSyncRepository(applicationContext)
        val repository = HabitRepository(database.habitDao(), firebaseSync)
        val factory = AuraHealthViewModel.provideFactory(repository, firebaseSync)
        viewModel = ViewModelProvider(this, factory)[AuraHealthViewModel::class.java]

        // Silent auto-sign-in on app startup if previously authorized
        val credentialManager = CredentialManager.create(this)
        attemptAutoSignIn(
            context = this,
            credentialManager = credentialManager,
            onAuthSuccess = {
                // User already signed in; trigger cloud sync
                lifecycleScope.launch {
                    viewModel.firebaseSync.syncUserProfile()
                }
            },
            onUnauthenticated = {
                // Interactive fallback available via UI button
            },
            scope = lifecycleScope
        )

        // Ensure all enabled habit alarms are scheduled
        lifecycleScope.launch {
            repository.scheduleAllEnabledHabits(applicationContext)
        }

        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
