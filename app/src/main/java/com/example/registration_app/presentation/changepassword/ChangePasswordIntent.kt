package com.example.registration_app.presentation.changepassword

sealed class ChangePasswordIntent {
    data class UpdateNewPassword(val password: String) : ChangePasswordIntent()
    data class UpdateConfirmPassword(val confirmPassword: String) : ChangePasswordIntent()
    object ChangePassword : ChangePasswordIntent()
    object ClearError : ChangePasswordIntent()
}
