package com.example.registration_app.presentation.otpverification

sealed class OtpVerificationIntent {
    data class UpdateOtp(val otp: String) : OtpVerificationIntent()
    object VerifyOtp : OtpVerificationIntent()
    object ClearError : OtpVerificationIntent()
}
