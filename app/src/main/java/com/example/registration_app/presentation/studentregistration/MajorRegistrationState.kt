package com.example.registration_app.presentation.studentregistration

data class MajorRegistrationState(
    val studentName: String = "",
    val email: String = "",
    val gender: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val dateOfBirth: Long? = null, // Timestamp in milliseconds
    val dateOfBirthDay: String = "",
    val dateOfBirthMonth: String = "",
    val dateOfBirthYear: String = "",
    val course: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val showDatePicker: Boolean = false,
    val readyForPayment: Boolean = false,
    val preparedRegistration: com.example.registration_app.domain.model.StudentRegistration? = null,
    val studentId: String = ""
)
