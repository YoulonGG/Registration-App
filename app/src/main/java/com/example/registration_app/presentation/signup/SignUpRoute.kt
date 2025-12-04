package com.example.registration_app.presentation.signup

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

/**
 * Created by johnyoulong on 12/4/25.
 */

@Composable
fun SignUpRoute(
    navController: NavController
) {
    SignUpScreen(
        onNavigateToLogin = {
            navController.popBackStack()
        },
        onSignUpSuccess = {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    )
}