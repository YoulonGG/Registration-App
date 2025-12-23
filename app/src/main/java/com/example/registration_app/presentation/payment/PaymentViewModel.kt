package com.example.registration_app.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.data.datasource.FirestoreDataSource
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.model.PaymentStatus
import com.example.registration_app.domain.model.PaymentTransaction
import com.example.registration_app.domain.model.StudentRegistration
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

    private var pendingRegistration: StudentRegistration? = null
    private var studentId: String = ""
    private var initializedStudentId: String? = null // Track which studentId was initialized

    fun initializePayment(registration: StudentRegistration, studentId: String) {
        // Prevent multiple initializations for the same studentId
        if (initializedStudentId == studentId && _state.value.transaction != null) {
            return
        }

        viewModelScope.launch {
            // Double-check to prevent race conditions
            if (initializedStudentId == studentId && _state.value.transaction != null) {
                return@launch
            }
            
            initializedStudentId = studentId
            _state.value = _state.value.copy(isLoading = true)
            this@PaymentViewModel.pendingRegistration = registration
            this@PaymentViewModel.studentId = studentId

            val currentUser = getCurrentUserUseCase()
            val userId = currentUser?.uid

            // Ensure userId is not null
            if (userId == null || userId.isBlank()) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "User not logged in. Please sign in again."
                )
                initializedStudentId = null // Reset on error
                return@launch
            }

            // Create payment transaction
            val transaction = PaymentTransaction(
                studentId = studentId,
                studentName = registration.studentName,
                course = registration.course,
                major = registration.major,
                price = 300.0,
                status = PaymentStatus.PENDING,
                userId = userId
            )

            // Save transaction to Firestore
            when (val result = firestoreDataSource.savePaymentTransaction(transaction)) {
                is AuthResult.Success -> {
                    _state.value = _state.value.copy(
                        transaction = transaction.copy(id = result.data),
                        isLoading = false
                    )
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                    initializedStudentId = null // Reset on error so user can retry
                }
                else -> {
                    initializedStudentId = null
                }
            }
        }
    }

    fun processPayment() {
        viewModelScope.launch {
            val currentTransaction = _state.value.transaction
            val registration = pendingRegistration
            if (currentTransaction == null || registration == null) {
                _state.value = _state.value.copy(errorMessage = "No transaction or registration found")
                return@launch
            }

            _state.value = _state.value.copy(isProcessing = true, errorMessage = null)

            // Save registration
            when (val regResult = studentRegistrationRepository.registerStudent(registration)) {
                is AuthResult.Success -> {
                    // Get the registration ID by querying for it
                    val currentUser = getCurrentUserUseCase()
                    val userId = currentUser?.uid
                    if (userId != null) {
                        // Query for the registration we just created
                        when (val regQueryResult = studentRegistrationRepository.getStudentRegistrationByUserId(userId)) {
                            is AuthResult.Success -> {
                                val savedRegistration = regQueryResult.data
                                // Update payment transaction to SUCCESS
                                // Note: We don't have a direct registration ID, so we'll use the transaction ID
                                when (val updateResult = firestoreDataSource.updatePaymentTransaction(
                                    currentTransaction.id,
                                    PaymentStatus.SUCCESS,
                                    currentTransaction.id // Use transaction ID as reference
                                )) {
                                    is AuthResult.Success -> {
                                        _state.value = _state.value.copy(
                                            isProcessing = false,
                                            transaction = currentTransaction.copy(
                                                status = PaymentStatus.SUCCESS,
                                                registrationId = currentTransaction.id
                                            )
                                        )
                                    }
                                    is AuthResult.Error -> {
                                        _state.value = _state.value.copy(
                                            isProcessing = false,
                                            errorMessage = updateResult.message
                                        )
                                    }
                                    else -> {}
                                }
                            }
                            is AuthResult.Error -> {
                                // Registration saved but couldn't retrieve it - still mark payment as success
                                firestoreDataSource.updatePaymentTransaction(
                                    currentTransaction.id,
                                    PaymentStatus.SUCCESS
                                )
                                _state.value = _state.value.copy(
                                    isProcessing = false,
                                    transaction = currentTransaction.copy(
                                        status = PaymentStatus.SUCCESS
                                    )
                                )
                            }
                            else -> {}
                        }
                    } else {
                        _state.value = _state.value.copy(
                            isProcessing = false,
                            errorMessage = "User not found"
                        )
                    }
                }
                is AuthResult.Error -> {
                    // Mark payment as failed
                    firestoreDataSource.updatePaymentTransaction(
                        currentTransaction.id,
                        PaymentStatus.FAILED
                    )
                    _state.value = _state.value.copy(
                        isProcessing = false,
                        errorMessage = regResult.message
                    )
                }
                else -> {}
            }
        }
    }

    fun cancelPayment() {
        viewModelScope.launch {
            val currentTransaction = _state.value.transaction
            if (currentTransaction != null) {
                // Mark payment as failed
                firestoreDataSource.updatePaymentTransaction(
                    currentTransaction.id,
                    PaymentStatus.FAILED
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
