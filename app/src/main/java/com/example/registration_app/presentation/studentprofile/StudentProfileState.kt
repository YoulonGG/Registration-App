package com.example.registration_app.presentation.studentprofile

import com.example.registration_app.domain.model.StudentRegistration

data class StudentProfileState(
    val studentRegistration: StudentRegistration? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEditMode: Boolean = false,
    val editedFirstName: String = "",
    val editedLastName: String = "",
    val editedEmail: String = "",
    val editedUsername: String = "",
    val editedPhoneNumber: String = "",
    val editedGender: String = "",
    val editedDateOfBirthDay: String = "",
    val editedDateOfBirthMonth: String = "",
    val editedDateOfBirthYear: String = "",
    val isSaving: Boolean = false
)
