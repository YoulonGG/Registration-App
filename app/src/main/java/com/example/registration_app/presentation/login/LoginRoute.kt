package com.example.registration_app.presentation.login

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

/**
 * Created by johnyoulong on 12/4/25.
 */

@Composable
fun LoginRoute(
    navController: NavController
) {
    LoginScreen(
        onNavigateToSignUp = {
            navController.navigate("signup")
        },
        onNavigateToForgotPassword = {
            navController.navigate("forgot_password")
        },
        onLoginSuccess = {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    )
}