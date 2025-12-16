package com.example.registration_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import com.example.registration_app.presentation.navigation.NavGraph
import com.example.registration_app.presentation.navigation.Screen
import com.example.registration_app.ui.theme.RegistrationAppTheme
import com.example.registration_app.util.DeepLinkHelper
import com.example.registration_app.util.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var preferencesManager: PreferencesManager
    
    @Inject
    lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            RegistrationAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var startDestination by remember { mutableStateOf<String?>(null) }
                    
                    // Determine start destination before rendering NavHost
                    LaunchedEffect(Unit) {
                        val isOnboardingCompleted = preferencesManager.isOnboardingCompleted()
                        startDestination = if (isOnboardingCompleted) {
                            // Check if user is authenticated
                            val user = getCurrentUserUseCase()
                            if (user != null) {
                                Screen.Home.route
                            } else {
                                Screen.Login.route
                            }
                        } else {
                            Screen.Splash.route
                        }
                    }
                    
                    // Handle deep link for password reset
                    LaunchedEffect(Unit) {
                        val resetCode = DeepLinkHelper.extractResetCode(intent)
                        resetCode?.let { code ->
                            // Wait for navigation to be ready
                            delay(500)
                            // Navigate to reset password screen
                            navController.navigate(Screen.ResetPassword.createRoute(code)) {
                                // Clear back stack up to and including the start destination
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    }
                    
                    // Only render NavGraph when start destination is determined
                    startDestination?.let { destination ->
                        NavGraph(
                            navController = navController,
                            preferencesManager = preferencesManager,
                            getCurrentUserUseCase = getCurrentUserUseCase,
                            startDestination = destination
                        )
                    }
                }
            }
        }
    }
}