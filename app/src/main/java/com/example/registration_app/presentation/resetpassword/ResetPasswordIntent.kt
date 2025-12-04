package com.example.registration_app.presentation.resetpassword

sealed class ResetPasswordIntent {
    data class UpdateNewPassword(val password: String) : ResetPasswordIntent()
    data class UpdateConfirmPassword(val confirmPassword: String) : ResetPasswordIntent()
    object ResetPassword : ResetPasswordIntent()
    object ClearError : ResetPasswordIntent()
}