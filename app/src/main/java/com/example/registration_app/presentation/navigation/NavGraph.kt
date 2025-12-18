package com.example.registration_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.registration_app.domain.model.UserType
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import com.example.registration_app.presentation.forgotpassword.ForgotPasswordScreen
import com.example.registration_app.presentation.home.HomeScreen
import com.example.registration_app.presentation.login.LoginScreen
import com.example.registration_app.presentation.resetpassword.ResetPasswordScreen
import com.example.registration_app.presentation.signup.SignUpScreen
import com.example.registration_app.presentation.splash.SplashScreen
import com.example.registration_app.presentation.studentprofile.StudentProfileScreen
import com.example.registration_app.presentation.onboarding.OnboardingScreen
import com.example.registration_app.presentation.adminhome.AdminHomeScreen
import com.example.registration_app.presentation.studentregistration.MajorRegistrationScreen
import com.example.registration_app.presentation.studentregistration.StudentRegistrationRoute
import com.example.registration_app.presentation.usertypeselection.UserTypeSelectionScreen
import com.example.registration_app.util.PreferencesManager

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Splash : Screen("splash")
    object UserTypeSelection : Screen("user_type_selection")
    object Login : Screen("login") {
        fun createRoute(userType: String) = "login/$userType"
    }
    object SignUp : Screen("signup") {
        fun createRoute(userType: String) = "signup/$userType"
    }
    object Home : Screen("home")
    object AdminHome : Screen("admin_home")
    object ForgotPassword : Screen("forgot_password")
    object ResetPassword : Screen("reset_password") {
        fun createRoute(resetCode: String) = "reset_password/$resetCode"
    }
    object StudentRegistration : Screen("student_registration")
    object MajorRegistration : Screen("major_registration") {
        fun createRoute(majorName: String) = "major_registration/$majorName"
    }
    object StudentProfile : Screen("student_profile")
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
        composable(Screen.Onboarding.route) {
            val coroutineScope = rememberCoroutineScope()
            
            OnboardingScreen(
                onGetStarted = {
                    preferencesManager.setOnboardingCompleted(true)
                    coroutineScope.launch {
                        val user = getCurrentUserUseCase()
                        if (user != null) {
                            val destination = if (user.userType == UserType.ADMIN) {
                                Screen.AdminHome.route
                            } else {
                                Screen.Home.route
                            }
                            navController.navigate(destination) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.UserTypeSelection.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    }
                },
                onSkip = {
                    preferencesManager.setOnboardingCompleted(true)
                    coroutineScope.launch {
                        val user = getCurrentUserUseCase()
                        if (user != null) {
                            val destination = if (user.userType == UserType.ADMIN) {
                                Screen.AdminHome.route
                            } else {
                                Screen.Home.route
                            }
                            navController.navigate(destination) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.UserTypeSelection.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.UserTypeSelection.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.UserTypeSelection.route) {
            UserTypeSelectionScreen(
                onNavigateToAdminLogin = {
                    navController.navigate(Screen.Login.createRoute(UserType.ADMIN.name)) {
                        popUpTo(Screen.UserTypeSelection.route) { inclusive = false }
                    }
                },
                onNavigateToStudentLogin = {
                    navController.navigate(Screen.Login.createRoute(UserType.STUDENT.name)) {
                        popUpTo(Screen.UserTypeSelection.route) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = "login/{userType}",
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) { backStackEntry ->
            val userTypeString = backStackEntry.arguments?.getString("userType") ?: ""
            val userType = try {
                UserType.valueOf(userTypeString)
            } catch (e: Exception) {
                null
            }
            
            LoginScreen(
                userType = userType ?: UserType.STUDENT,
                onNavigateToSignUp = { currentUserType ->
                    val targetUserType = currentUserType ?: userType ?: UserType.STUDENT
                    navController.navigate(Screen.SignUp.createRoute(targetUserType.name))
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = {
                    val targetUserType = userType ?: UserType.STUDENT
                    val destination = if (targetUserType == UserType.ADMIN) {
                        Screen.AdminHome.route
                    } else {
                        Screen.Home.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.UserTypeSelection.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "signup/{userType}",
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) { backStackEntry ->
            val userTypeString = backStackEntry.arguments?.getString("userType") ?: ""
            val userType = try {
                UserType.valueOf(userTypeString)
            } catch (e: Exception) {
                null
            }
            
            SignUpScreen(
                userType = userType ?: UserType.STUDENT,
                onNavigateToLogin = { currentUserType ->
                    val targetUserType = currentUserType ?: userType ?: UserType.STUDENT
                    navController.navigate(Screen.Login.createRoute(targetUserType.name))
                },
                onSignUpSuccess = {
                    val targetUserType = userType ?: UserType.STUDENT
                    val destination = if (targetUserType == UserType.ADMIN) {
                        Screen.AdminHome.route
                    } else {
                        Screen.Home.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.UserTypeSelection.route) { inclusive = true }
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
                    navController.navigate(Screen.UserTypeSelection.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToStudentRegistration = {
                    navController.navigate(Screen.StudentRegistration.route)
                },
                onNavigateToStudentProfile = {
                    navController.navigate(Screen.StudentProfile.route)
                }
            )
        }

        composable(Screen.AdminHome.route) {
            AdminHomeScreen(
                onSignOut = {
                    navController.navigate(Screen.UserTypeSelection.route) {
                        popUpTo(Screen.AdminHome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.StudentProfile.route) {
            StudentProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignOut = {
                    navController.navigate(Screen.UserTypeSelection.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
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
                    navController.navigate(Screen.UserTypeSelection.route) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
