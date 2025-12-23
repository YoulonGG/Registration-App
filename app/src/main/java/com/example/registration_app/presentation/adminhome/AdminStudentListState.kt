package com.example.registration_app.presentation.adminhome

import com.example.registration_app.domain.model.StudentRegistration

data class AdminStudentListState(
    val students: List<StudentRegistration> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
