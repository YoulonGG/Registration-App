package com.example.registration_app.presentation.studentregistration

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StudentRegistrationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(StudentRegistrationState())
    val state: StateFlow<StudentRegistrationState> = _state.asStateFlow()
}
