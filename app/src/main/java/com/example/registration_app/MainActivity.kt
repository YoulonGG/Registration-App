package com.example.registration_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import com.example.registration_app.presentation.navigation.NavGraph
import com.example.registration_app.presentation.navigation.Screen
import com.example.registration_app.ui.theme.LoginTealGreen
import com.example.registration_app.ui.theme.RegistrationAppTheme
import com.example.registration_app.util.DeepLinkHelper
import com.example.registration_app.util.DrawableResources
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LoginTealGreen)
                ) {
                    val navController = rememberNavController()
                    var startDestination by remember { mutableStateOf<String?>(null) }
                    
                    LaunchedEffect(Unit) {
                        val isOnboardingCompleted = preferencesManager.isOnboardingCompleted()
                        startDestination = if (isOnboardingCompleted) {
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
                    
                    LaunchedEffect(Unit) {
                        val resetCode = DeepLinkHelper.extractResetCode(intent)
                        resetCode?.let { code ->
                            delay(500)
                            navController.navigate(Screen.ResetPassword.createRoute(code)) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    }
                    
                    if (startDestination == null) {
                        Image(
                            painter = painterResource(id = DrawableResources.Logo),
                            contentDescription = "University Logo",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(180.dp)
                        )
                    }
                    
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