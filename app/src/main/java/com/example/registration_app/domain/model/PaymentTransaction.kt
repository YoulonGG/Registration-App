package com.example.registration_app.domain.model

data class PaymentTransaction(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val course: String = "",
    val major: String = "",
    val price: Double = 300.0,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val userId: String? = null,
    val transactionDate: Long = System.currentTimeMillis(),
    val registrationId: String? = null // Link to student registration if successful
)

enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED
}
