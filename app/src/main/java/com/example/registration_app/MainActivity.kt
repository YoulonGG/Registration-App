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
import androidx.navigation.compose.rememberNavController
import com.example.registration_app.presentation.navigation.NavGraph
import com.example.registration_app.presentation.navigation.Screen
import com.example.registration_app.ui.theme.RegistrationAppTheme
import com.example.registration_app.util.DeepLinkHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                    
                    // Handle deep link for password reset
                    // Wait for splash screen to complete before navigating
                    LaunchedEffect(Unit) {
                        val resetCode = DeepLinkHelper.extractResetCode(intent)
                        resetCode?.let { code ->
                            // Wait for splash screen to complete navigation (2 seconds + buffer)
                            delay(2500)
                            // Navigate to reset password screen
                            // This will work regardless of current navigation state
                            navController.navigate(Screen.ResetPassword.createRoute(code)) {
                                // Clear back stack up to and including the start destination
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    }
                    
                    NavGraph(
                        navController = navController,
                        startDestination = Screen.Splash.route
                    )
                }
            }
        }
    }
}