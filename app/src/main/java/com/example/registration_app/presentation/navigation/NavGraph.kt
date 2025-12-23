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
import com.example.registration_app.presentation.usertypeselection.UserTypeSelectionScreen
import com.example.registration_app.presentation.payment.PaymentScreen
import com.example.registration_app.presentation.registrationsuccess.RegistrationSuccessScreen
import com.example.registration_app.presentation.paymenthistory.PaymentHistoryScreen
import com.example.registration_app.domain.model.StudentRegistration
import com.example.registration_app.util.PreferencesManager
import kotlinx.coroutines.launch

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
    object AdminSubjectList : Screen("admin_subject_list")
    object AdminStudentList : Screen("admin_student_list") {
        fun createRoute(majorName: String) = "admin_student_list/$majorName"
    }
    object AdminProfile : Screen("admin_profile")
    object Payment : Screen("payment") {
        fun createRoute(
            studentId: String,
            studentName: String,
            email: String,
            gender: String,
            phoneNumber: String,
            address: String,
            dateOfBirthDay: String,
            dateOfBirthMonth: String,
            dateOfBirthYear: String,
            course: String,
            major: String
        ) = "payment/$studentId/$studentName/$email/$gender/$phoneNumber/$address/$dateOfBirthDay/$dateOfBirthMonth/$dateOfBirthYear/$course/$major"
    }
    object RegistrationSuccess : Screen("registration_success")
    object PaymentHistory : Screen("payment_history")
    object AdminPaymentHistory : Screen("admin_payment_history")
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
                },
                onNavigateToPaymentHistory = {
                    navController.navigate(Screen.PaymentHistory.route)
                }
            )
        }

        composable(Screen.AdminHome.route) {
            AdminHomeScreen(
                onSignOut = {
                    navController.navigate(Screen.UserTypeSelection.route) {
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
                    navController.navigate(Screen.AdminPaymentHistory.route)
                }
            )
        }

        composable(Screen.AdminProfile.route) {
            AdminProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignOut = {
                    navController.navigate(Screen.UserTypeSelection.route) {
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
                onNavigateToPayment = { registration, studentId ->
                    // URL encode the arguments to handle special characters
                    fun encode(str: String) = java.net.URLEncoder.encode(str, "UTF-8")
                    val route = Screen.Payment.createRoute(
                        studentId = encode(studentId),
                        studentName = encode(registration.studentName),
                        email = encode(registration.email),
                        gender = encode(registration.gender),
                        phoneNumber = encode(registration.phoneNumber),
                        address = encode(registration.address),
                        dateOfBirthDay = encode(registration.dateOfBirthDay),
                        dateOfBirthMonth = encode(registration.dateOfBirthMonth),
                        dateOfBirthYear = encode(registration.dateOfBirthYear),
                        course = encode(registration.course),
                        major = encode(registration.major)
                    )
                    navController.navigate(route)
                },
                onRegistrationSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "payment/{studentId}/{studentName}/{email}/{gender}/{phoneNumber}/{address}/{dateOfBirthDay}/{dateOfBirthMonth}/{dateOfBirthYear}/{course}/{major}",
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("studentName") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType },
                navArgument("gender") { type = NavType.StringType },
                navArgument("phoneNumber") { type = NavType.StringType },
                navArgument("address") { type = NavType.StringType },
                navArgument("dateOfBirthDay") { type = NavType.StringType },
                navArgument("dateOfBirthMonth") { type = NavType.StringType },
                navArgument("dateOfBirthYear") { type = NavType.StringType },
                navArgument("course") { type = NavType.StringType },
                navArgument("major") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // URL decode the arguments
            fun decode(str: String) = java.net.URLDecoder.decode(str, "UTF-8")
            val studentId = decode(backStackEntry.arguments?.getString("studentId") ?: "")
            val studentName = decode(backStackEntry.arguments?.getString("studentName") ?: "")
            val email = decode(backStackEntry.arguments?.getString("email") ?: "")
            val gender = decode(backStackEntry.arguments?.getString("gender") ?: "")
            val phoneNumber = decode(backStackEntry.arguments?.getString("phoneNumber") ?: "")
            val address = decode(backStackEntry.arguments?.getString("address") ?: "")
            val dateOfBirthDay = decode(backStackEntry.arguments?.getString("dateOfBirthDay") ?: "")
            val dateOfBirthMonth = decode(backStackEntry.arguments?.getString("dateOfBirthMonth") ?: "")
            val dateOfBirthYear = decode(backStackEntry.arguments?.getString("dateOfBirthYear") ?: "")
            val course = decode(backStackEntry.arguments?.getString("course") ?: "")
            val major = decode(backStackEntry.arguments?.getString("major") ?: "")

            val registration = StudentRegistration(
                studentName = studentName,
                email = email,
                gender = gender,
                phoneNumber = phoneNumber,
                address = address,
                dateOfBirthDay = dateOfBirthDay,
                dateOfBirthMonth = dateOfBirthMonth,
                dateOfBirthYear = dateOfBirthYear,
                course = course,
                major = major
            )

            PaymentScreen(
                registration = registration,
                studentId = studentId,
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
                        popUpTo(Screen.UserTypeSelection.route) { inclusive = true }
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
