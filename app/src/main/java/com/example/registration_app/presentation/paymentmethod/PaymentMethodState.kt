package com.example.registration_app.presentation.paymentmethod

data class PaymentMethodState(
    val selectedMethod: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
