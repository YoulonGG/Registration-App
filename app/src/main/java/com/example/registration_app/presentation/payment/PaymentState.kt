package com.example.registration_app.presentation.payment

import com.example.registration_app.domain.model.PaymentTransaction

data class PaymentState(
    val transaction: PaymentTransaction? = null,
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val selectedMajor: String = "",
    val selectedYear: String = "",
    val availableMajors: List<String> = emptyList(),
    val isLoadingMajors: Boolean = false
)
