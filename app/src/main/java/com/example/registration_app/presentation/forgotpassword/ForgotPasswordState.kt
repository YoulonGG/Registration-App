package com.example.registration_app.presentation.forgotpassword

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isResetEmailSent: Boolean = false
)
