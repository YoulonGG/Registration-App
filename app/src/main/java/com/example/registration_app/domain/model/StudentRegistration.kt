package com.example.registration_app.domain.model

data class StudentRegistration(
    val studentName: String,
    val email: String,
    val gender: String,
    val phoneNumber: String,
    val address: String,
    val dateOfBirthDay: String,
    val dateOfBirthMonth: String,
    val dateOfBirthYear: String,
    val course: String,
    val major: String,
    val userId: String? = null, // Firebase Auth user ID if available
    val registrationDate: Long = System.currentTimeMillis()
)
