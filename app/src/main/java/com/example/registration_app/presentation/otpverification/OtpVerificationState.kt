package com.example.registration_app.presentation.otpverification

data class OtpVerificationState(
    val email: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOtpVerified: Boolean = false
)
