package com.example.registration_app.presentation.paymenthistory

import com.example.registration_app.domain.model.PaymentTransaction

data class PaymentHistoryState(
    val transactions: List<PaymentTransaction> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null
)
