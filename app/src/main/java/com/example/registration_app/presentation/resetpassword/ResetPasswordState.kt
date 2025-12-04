package com.example.registration_app.presentation.resetpassword

data class ResetPasswordState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordReset: Boolean = false
)