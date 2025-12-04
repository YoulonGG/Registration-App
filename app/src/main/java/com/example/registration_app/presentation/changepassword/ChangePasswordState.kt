package com.example.registration_app.presentation.changepassword

data class ChangePasswordState(
    val email: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordChanged: Boolean = false,
    val resetEmailSent: Boolean = false
)
