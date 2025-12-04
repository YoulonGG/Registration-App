package com.example.registration_app.data.datasource

import java.util.Random
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtpDataSource @Inject constructor() {
    private val otpStorage = ConcurrentHashMap<String, OtpData>()
    private val verifiedEmails = ConcurrentHashMap<String, Long>() // Email -> verification timestamp
    private val random = Random()

    data class OtpData(
        val otp: String,
        val email: String,
        val expiryTime: Long
    )

    fun generateAndStoreOtp(email: String): String {
        val otp = generate6DigitOtp()
        val expiryTime = System.currentTimeMillis() + (10 * 60 * 1000) // 10 minutes
        otpStorage[email] = OtpData(otp, email, expiryTime)
        return otp
    }

    fun verifyOtp(email: String, otp: String): Boolean {
        val storedData = otpStorage[email] ?: return false
        
        // Check if OTP expired
        if (System.currentTimeMillis() > storedData.expiryTime) {
            otpStorage.remove(email)
            verifiedEmails.remove(email)
            return false
        }
        
        // Verify OTP
        if (storedData.otp == otp) {
            // Mark email as verified (valid for 15 minutes)
            verifiedEmails[email] = System.currentTimeMillis() + (15 * 60 * 1000)
            return true
        }
        
        return false
    }

    fun clearOtp(email: String) {
        otpStorage.remove(email)
    }

    fun isOtpVerified(email: String): Boolean {
        val verificationExpiry = verifiedEmails[email] ?: return false
        // Check if verification is still valid (not expired)
        if (System.currentTimeMillis() > verificationExpiry) {
            verifiedEmails.remove(email)
            return false
        }
        return true
    }
    
    fun clearVerification(email: String) {
        verifiedEmails.remove(email)
        otpStorage.remove(email)
    }

    private fun generate6DigitOtp(): String {
        return String.format("%06d", random.nextInt(999999))
    }
}
