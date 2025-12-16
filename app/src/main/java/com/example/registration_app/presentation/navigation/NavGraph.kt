package com.example.registration_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import com.example.registration_app.presentation.forgotpassword.ForgotPasswordScreen
import com.example.registration_app.presentation.home.HomeScreen
import com.example.registration_app.presentation.login.LoginScreen
import com.example.registration_app.presentation.resetpassword.ResetPasswordScreen
import com.example.registration_app.presentation.signup.SignUpScreen
import com.example.registration_app.presentation.splash.SplashScreen
import com.example.registration_app.presentation.studentregistration.MajorRegistrationScreen
import com.example.registration_app.presentation.studentregistration.StudentRegistrationRoute
import com.example.registration_app.util.PreferencesManager

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object ForgotPassword : Screen("forgot_password")
    object ResetPassword : Screen("reset_password") {
        fun createRoute(resetCode: String) = "reset_password/$resetCode"
    }
    object StudentRegistration : Screen("student_registration")
    object MajorRegistration : Screen("major_registration") {
        fun createRoute(majorName: String) = "major_registration/$majorName"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    preferencesManager: PreferencesManager,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.Login.route) { inclusive = false }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToStudentRegistration = {
                    navController.navigate(Screen.StudentRegistration.route)
                }
            )
        }

        composable(Screen.StudentRegistration.route) {
            StudentRegistrationRoute(navController = navController)
        }

        composable(
            route = "major_registration/{majorName}",
            arguments = listOf(navArgument("majorName") { type = NavType.StringType })
        ) { backStackEntry ->
            val majorName = backStackEntry.arguments?.getString("majorName") ?: ""
            MajorRegistrationScreen(
                majorName = majorName,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegistrationSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "reset_password/{resetCode}",
            arguments = listOf(navArgument("resetCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val resetCode = backStackEntry.arguments?.getString("resetCode") ?: ""
            ResetPasswordScreen(
                resetCode = resetCode,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
