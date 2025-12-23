package com.example.registration_app.presentation.transactionhistory

import com.example.registration_app.domain.model.PaymentTransaction

data class TransactionHistoryState(
    val transactions: List<PaymentTransaction> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null
)
