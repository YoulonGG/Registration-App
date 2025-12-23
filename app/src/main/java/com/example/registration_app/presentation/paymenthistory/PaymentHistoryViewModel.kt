package com.example.registration_app.presentation.paymenthistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registration_app.data.datasource.FirestoreDataSource
import com.example.registration_app.domain.model.AuthResult
import com.example.registration_app.domain.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentHistoryViewModel @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentHistoryState())
    val state: StateFlow<PaymentHistoryState> = _state.asStateFlow()

    fun loadPaymentHistory(isAdmin: Boolean = false) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val result = if (isAdmin) {
                    // Admin sees all payment transactions
                    firestoreDataSource.getAllPaymentTransactions()
                } else {
                    // Student sees only their own transactions (filtered by userId)
                    val currentUser = getCurrentUserUseCase()
                    if (currentUser?.uid != null && currentUser.uid.isNotBlank()) {
                        firestoreDataSource.getPaymentTransactionsByUserId(currentUser.uid)
                    } else {
                        AuthResult.Error("User not logged in. Please sign in again.")
                    }
                }

                when (result) {
                    is AuthResult.Success -> {
                        _state.value = _state.value.copy(
                            transactions = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    is AuthResult.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Failed to load payment history"
                        )
                    }
                    else -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Unknown error occurred"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message ?: "Failed to load payment history"}"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun deleteTransaction(transactionId: String, isAdmin: Boolean = false) {
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase()
            
            // For students, verify they own the transaction
            if (!isAdmin) {
                val transaction = _state.value.transactions.find { it.id == transactionId }
                if (transaction?.userId != currentUser?.uid) {
                    _state.value = _state.value.copy(
                        errorMessage = "You can only delete your own transactions"
                    )
                    return@launch
                }
            }

            _state.value = _state.value.copy(isDeleting = true, errorMessage = null)

            when (val result = firestoreDataSource.deletePaymentTransaction(transactionId)) {
                is AuthResult.Success -> {
                    // Remove from local state immediately for better UX
                    val updatedTransactions = _state.value.transactions.filter { it.id != transactionId }
                    _state.value = _state.value.copy(
                        transactions = updatedTransactions,
                        isDeleting = false
                    )
                    // Optionally reload to ensure sync
                    loadPaymentHistory(isAdmin)
                }
                is AuthResult.Error -> {
                    _state.value = _state.value.copy(
                        isDeleting = false,
                        errorMessage = result.message
                    )
                }
                else -> {
                    _state.value = _state.value.copy(isDeleting = false)
                }
            }
        }
    }
}
