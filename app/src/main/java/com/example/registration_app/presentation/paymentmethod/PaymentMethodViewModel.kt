package com.example.registration_app.presentation.paymentmethod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.data.datasource.FirestoreDataSource
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.PaymentStatus
import com.example.registration_app.domain.model.PaymentTransaction
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentMethodViewModel @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentMethodState())
    val state: StateFlow<PaymentMethodState> = _state.asStateFlow()

    var selectedMajor: String = ""
    var selectedYear: String = ""

    fun updateSelectedMethod(method: String) {
        _state.value = _state.value.copy(selectedMethod = method)
    }

    fun processPayment() {
        viewModelScope.launch {
            val currentState = _state.value
            
            if (currentState.selectedMethod.isEmpty()) {
                _state.value = currentState.copy(errorMessage = "Please select a payment method")
                return@launch
            }

            if (selectedMajor.isEmpty() || selectedYear.isEmpty()) {
                _state.value = currentState.copy(errorMessage = "Missing payment information")
                return@launch
            }

            _state.value = currentState.copy(isLoading = true, errorMessage = null)

            val currentUser = getCurrentUserUseCase()
            val userId = currentUser?.uid
            
            if (userId == null || userId.isBlank()) {
                _state.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "User not logged in. Please sign in again."
                )
                return@launch
            }

            // Check if payment already exists for this semester
            when (val checkResult = firestoreDataSource.checkExistingPayment(userId, selectedMajor, selectedYear)) {
                is AuthResult.Success -> {
                    if (checkResult.data) {
                        _state.value = currentState.copy(
                            isLoading = false,
                            errorMessage = "You have already paid for $selectedMajor - $selectedYear. Please select a different semester."
                        )
                        return@launch
                    }
                }
                is AuthResult.Error -> {
                    _state.value = currentState.copy(
                        isLoading = false,
                        errorMessage = checkResult.message ?: "Cannot verify existing payment. Please try again."
                    )
                    return@launch
                }
                else -> {
                    // Continue if check is still loading (shouldn't happen)
                }
            }

            // Generate student ID
            val studentId = String.format("000%04d", System.currentTimeMillis() % 10000)
            
            // Create payment transaction
            val transaction = PaymentTransaction(
                studentId = studentId,
                studentName = currentUser.username ?: "Student",
                course = selectedYear, // selectedYear is now "Year 1 Semester 1", etc.
                major = selectedMajor,
                price = 300.0,
                status = PaymentStatus.SUCCESS,
                userId = userId
            )

            // Save transaction
            when (val result = firestoreDataSource.savePaymentTransaction(transaction)) {
                is AuthResult.Success -> {
                    _state.value = currentState.copy(
                        isLoading = false,
                        isSuccess = true,
                        selectedMethod = currentState.selectedMethod
                    )
                }
                is AuthResult.Error -> {
                    _state.value = currentState.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = result.message ?: "Failed to process payment"
                    )
                }
                else -> {
                    _state.value = currentState.copy(isLoading = false, isSuccess = false)
                }
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
