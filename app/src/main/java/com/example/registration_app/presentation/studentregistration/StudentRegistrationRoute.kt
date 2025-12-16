package com.example.registration_app.presentation.studentregistration

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.registration_app.presentation.navigation.Screen

@Composable
fun StudentRegistrationRoute(
    navController: NavController
) {
    StudentRegistrationScreen(
        onNavigateBack = {
            navController.popBackStack()
        },
        onNavigateToMajorRegistration = { majorName ->
            navController.navigate(Screen.MajorRegistration.createRoute(majorName))
        }
    )
}
