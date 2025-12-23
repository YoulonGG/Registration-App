package com.example.registration_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.registration_app.domain.model.UserType
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import com.example.registration_app.presentation.adminhome.AdminHomeScreen
import com.example.registration_app.presentation.adminhome.AdminProfileScreen
import com.example.registration_app.presentation.adminhome.AdminStudentListScreen
import com.example.registration_app.presentation.adminhome.AdminSubjectListScreen
import com.example.registration_app.presentation.forgotpassword.ForgotPasswordScreen
import com.example.registration_app.presentation.home.HomeScreen
import com.example.registration_app.presentation.login.LoginScreen
import com.example.registration_app.presentation.onboarding.OnboardingScreen
import com.example.registration_app.presentation.resetpassword.ResetPasswordScreen
import com.example.registration_app.presentation.signup.SignUpScreen
import com.example.registration_app.presentation.splash.SplashScreen
import com.example.registration_app.presentation.studentprofile.StudentProfileScreen
import com.example.registration_app.presentation.studentregistration.MajorRegistrationScreen
import com.example.registration_app.presentation.studentregistration.StudentRegistrationRoute
import com.example.registration_app.presentation.payment.PaymentScreen
import com.example.registration_app.presentation.paymentmethod.PaymentMethodScreen
import com.example.registration_app.presentation.registrationsuccess.RegistrationSuccessScreen
import com.example.registration_app.presentation.paymenthistory.PaymentHistoryScreen
import com.example.registration_app.presentation.transactionhistory.TransactionHistoryScreen
import com.example.registration_app.domain.model.StudentRegistration
import com.example.registration_app.util.PreferencesManager
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
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
    object AdminSubjectList : Screen("admin_subject_list")
    object AdminStudentList : Screen("admin_student_list") {
        fun createRoute(majorName: String) = "admin_student_list/$majorName"
    }
    object AdminProfile : Screen("admin_profile")
    object Payment : Screen("payment")
    object PaymentMethod : Screen("payment_method") {
        fun createRoute(major: String, year: String) = "payment_method/$major/$year"
    }
    object RegistrationSuccess : Screen("registration_success")
    object PaymentHistory : Screen("payment_history")
    object AdminPaymentHistory : Screen("admin_payment_history")
    object TransactionHistory : Screen("transaction_history")
    object AdminTransactionHistory : Screen("admin_transaction_history")
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
                            navController.navigate(Screen.Login.route) {
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
                            navController.navigate(Screen.Login.route) {
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
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = { userType ->
                    // Route based on userType after login
                    val destination = if (userType == UserType.ADMIN) {
                        Screen.AdminHome.route
                    } else {
                        Screen.Home.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
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
                },
                onNavigateToStudentProfile = {
                    navController.navigate(Screen.StudentProfile.route)
                },
                onNavigateToPaymentHistory = {
                    navController.navigate(Screen.TransactionHistory.route)
                },
                onNavigateToPayment = {
                    navController.navigate(Screen.Payment.route)
                }
            )
        }

        composable(Screen.AdminHome.route) {
            AdminHomeScreen(
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminHome.route) { inclusive = true }
                    }
                },
                onNavigateToStudentList = {
                    navController.navigate(Screen.AdminSubjectList.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.AdminProfile.route)
                },
                onNavigateToPaymentHistory = {
                    navController.navigate(Screen.AdminTransactionHistory.route)
                },
                onNavigateToPayment = {
                    navController.navigate(Screen.Payment.route)
                }
            )
        }

        composable(Screen.AdminProfile.route) {
            AdminProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminHome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminSubjectList.route) {
            AdminSubjectListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToStudentList = { majorName ->
                    navController.navigate(Screen.AdminStudentList.createRoute(majorName))
                }
            )
        }

        composable(
            route = "admin_student_list/{majorName}",
            arguments = listOf(navArgument("majorName") { type = NavType.StringType })
        ) { backStackEntry ->
            val majorName = backStackEntry.arguments?.getString("majorName") ?: ""
            AdminStudentListScreen(
                majorName = majorName,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.StudentProfile.route) {
            StudentProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
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
                onNavigateToPayment = { _, _ -> },
                onRegistrationSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Payment.route) {
            PaymentScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPaymentMethod = { major, year ->
                    fun encode(str: String) = java.net.URLEncoder.encode(str, "UTF-8")
                    navController.navigate(Screen.PaymentMethod.createRoute(encode(major), encode(year)))
                },
                onNavigateToTransactionHistory = {
                    navController.navigate(Screen.TransactionHistory.route)
                }
            )
        }

        composable(
            route = "payment_method/{major}/{year}",
            arguments = listOf(
                navArgument("major") { type = NavType.StringType },
                navArgument("year") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            fun decode(str: String) = java.net.URLDecoder.decode(str, "UTF-8")
            val major = decode(backStackEntry.arguments?.getString("major") ?: "")
            val year = decode(backStackEntry.arguments?.getString("year") ?: "")
            
            PaymentMethodScreen(
                major = major,
                year = year,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPaymentSuccess = {
                    navController.navigate(Screen.RegistrationSuccess.route) {
                        popUpTo(Screen.Payment.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.RegistrationSuccess.route) {
            RegistrationSuccessScreen(
                onContinue = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PaymentHistory.route) {
            PaymentHistoryScreen(
                isAdmin = false,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AdminPaymentHistory.route) {
            PaymentHistoryScreen(
                isAdmin = true,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.TransactionHistory.route) {
            TransactionHistoryScreen(
                isAdmin = false,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AdminTransactionHistory.route) {
            TransactionHistoryScreen(
                isAdmin = true,
                onNavigateBack = {
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
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
