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
                    LaunchedEffect(Unit) {
                        val resetCode = DeepLinkHelper.extractResetCode(intent)
                        resetCode?.let { code ->
                            navController.navigate(Screen.ResetPassword.createRoute(code)) {
                                popUpTo(Screen.Login.route) { inclusive = false }
                            }
                        }
                    }
                    
                    NavGraph(
                        navController = navController,
                        startDestination = Screen.Login.route
                    )
                }
            }
        }
    }
}