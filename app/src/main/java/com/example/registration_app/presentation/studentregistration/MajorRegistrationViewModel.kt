package com.example.registration_app.presentation.studentregistration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.StudentRegistration
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import com.example.registration_app.domain.usecase.RegisterStudentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MajorRegistrationViewModel @Inject constructor(
    private val registerStudentUseCase: RegisterStudentUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MajorRegistrationState())
    val state: StateFlow<MajorRegistrationState> = _state.asStateFlow()
    
    var majorName: String = ""
        private set

    fun updateStudentName(name: String) {
        _state.value = _state.value.copy(studentName = name)
    }

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun updateGender(gender: String) {
        _state.value = _state.value.copy(gender = gender)
    }

    fun updatePhoneNumber(phone: String) {
        _state.value = _state.value.copy(phoneNumber = phone)
    }

    fun updateAddress(address: String) {
        _state.value = _state.value.copy(address = address)
    }

    fun updateDateOfBirthDay(day: String) {
        _state.value = _state.value.copy(dateOfBirthDay = day)
    }

    fun updateDateOfBirthMonth(month: String) {
        _state.value = _state.value.copy(dateOfBirthMonth = month)
    }

    fun updateDateOfBirthYear(year: String) {
        _state.value = _state.value.copy(dateOfBirthYear = year)
    }

    fun setDateOfBirth(timestamp: Long) {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        _state.value = _state.value.copy(
            dateOfBirth = timestamp,
            dateOfBirthDay = calendar.get(java.util.Calendar.DAY_OF_MONTH).toString(),
            dateOfBirthMonth = (calendar.get(java.util.Calendar.MONTH) + 1).toString(),
            dateOfBirthYear = calendar.get(java.util.Calendar.YEAR).toString(),
            showDatePicker = false
        )
    }

    fun showDatePicker() {
        _state.value = _state.value.copy(showDatePicker = true)
    }

    fun dismissDatePicker() {
        _state.value = _state.value.copy(showDatePicker = false)
    }

    fun updateCourse(course: String) {
        _state.value = _state.value.copy(course = course)
    }

    fun setMajorName(major: String) {
        majorName = major
    }

    fun submitRegistration() {
        val currentState = _state.value
        
        // Validation
        if (currentState.studentName.isBlank() || 
            currentState.email.isBlank() || 
            currentState.gender.isBlank() ||
            currentState.phoneNumber.isBlank() ||
            currentState.address.isBlank() ||
            currentState.dateOfBirth == null ||
            currentState.course.isBlank()) {
            _state.value = currentState.copy(
                errorMessage = "Please fill in all fields"
            )
            return
        }

        // Email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _state.value = currentState.copy(
                errorMessage = "Please enter a valid email"
            )
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(
                isLoading = true,
                errorMessage = null,
                isSuccess = false
            )

            // Get current user ID if available
            val currentUser = getCurrentUserUseCase()
            val userId = currentUser?.uid

            // Format date from timestamp
            val calendar = java.util.Calendar.getInstance().apply {
                timeInMillis = currentState.dateOfBirth!!
            }
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH).toString()
            val month = (calendar.get(java.util.Calendar.MONTH) + 1).toString()
            val year = calendar.get(java.util.Calendar.YEAR).toString()

            val registration = StudentRegistration(
                studentName = currentState.studentName.trim(),
                email = currentState.email.trim(),
                gender = currentState.gender,
                phoneNumber = currentState.phoneNumber.trim(),
                address = currentState.address.trim(),
                dateOfBirthDay = day,
                dateOfBirthMonth = month,
                dateOfBirthYear = year,
                course = currentState.course.trim(),
                major = majorName,
                userId = userId
            )

            when (val result = registerStudentUseCase(registration)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        isSuccess = false
                    )
                }
                is AuthResult.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun resetSuccessState() {
        _state.value = _state.value.copy(isSuccess = false)
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
