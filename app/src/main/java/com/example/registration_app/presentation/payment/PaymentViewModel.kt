package com.example.registration_app.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.data.datasource.FirestoreDataSource
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.PaymentStatus
import com.example.registration_app.domain.model.PaymentTransaction
import com.example.registration_app.domain.repository.StudentRegistrationRepository
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val studentRegistrationRepository: StudentRegistrationRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state: StateFlow<PaymentState> = _state.asStateFlow()

    fun loadStudentMajors() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMajors = true, errorMessage = null)
            
            val currentUser = getCurrentUserUseCase()
            val userId = currentUser?.uid
            
            if (userId == null || userId.isBlank()) {
                _state.value = _state.value.copy(
                    isLoadingMajors = false,
                    errorMessage = "User not logged in. Please sign in again."
                )
                return@launch
            }

            when (val result = studentRegistrationRepository.getAllStudentRegistrationsByUserId(userId)) {
                is AuthResult.Success -> {
                    val registrations = result.data
                    val majors = registrations.map { it.major }
                        .filter { it.isNotBlank() }
                        .distinct()
                        .sorted()
                    
                    _state.value = _state.value.copy(
                        availableMajors = majors,
                        isLoadingMajors = false
                    )
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoadingMajors = false,
                        errorMessage = result.message ?: "Failed to load registered majors"
                    )
                }
                else -> {
                    _state.value = _state.value.copy(isLoadingMajors = false)
                }
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun updateSelectedMajor(major: String) {
        _state.value = _state.value.copy(selectedMajor = major)
    }

    fun updateSelectedYear(year: String) {
        _state.value = _state.value.copy(selectedYear = year)
    }
}
