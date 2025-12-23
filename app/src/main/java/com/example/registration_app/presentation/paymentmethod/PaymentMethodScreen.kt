package com.example.registration_app.presentation.paymentmethod

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registration_app.presentation.common.ErrorDialog
import com.example.registration_app.ui.theme.HomeTextDark
import com.example.registration_app.ui.theme.LoginGoldenYellow
import com.example.registration_app.ui.theme.LoginTealGreen
import com.example.registration_app.ui.theme.LoginWhite

@Composable
fun PaymentMethodScreen(
    major: String,
    year: String,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    viewModel: PaymentMethodViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(major, year) {
        viewModel.selectedMajor = major
        viewModel.selectedYear = year
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onPaymentSuccess()
        }
    }

    state.errorMessage?.let { error ->
        ErrorDialog(
            title = "Payment Error",
            message = error,
            onDismiss = {
                viewModel.clearError()
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginTealGreen)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PaymentMethodHeader(
                onNavigateBack = onNavigateBack
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
                    .background(color = LoginTealGreen)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(topStart = 75.dp),
                            spotColor = Color.Black.copy(alpha = 0.25f),
                            ambientColor = Color.Black.copy(alpha = 0.15f)
                        )
                        .background(
                            color = LoginWhite,
                            shape = RoundedCornerShape(topStart = 75.dp)
                        )
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Sub-heading
                    Text(
                        text = "Select payment mode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = HomeTextDark.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payment Options
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                    ) {
                        PaymentMethodOption(
                            iconColor = Color(0xFF4CAF50),
                            iconText = "💵",
                            title = "paid in cash at the school",
                            isSelected = state.selectedMethod == "Cash",
                            onClick = { viewModel.updateSelectedMethod("Cash") }
                        )

                        PaymentMethodOption(
                            iconColor = Color(0xFF0066CC),
                            iconText = "ABA",
                            title = "ABA PAY",
                            isSelected = state.selectedMethod == "ABA PAY",
                            onClick = { viewModel.updateSelectedMethod("ABA PAY") }
                        )

                        PaymentMethodOption(
                            iconColor = Color(0xFF003366),
                            iconText = "🐦",
                            title = "ACLEDA PAY",
                            isSelected = state.selectedMethod == "ACLEDA PAY",
                            onClick = { viewModel.updateSelectedMethod("ACLEDA PAY") }
                        )

                        PaymentMethodOption(
                            iconColor = Color(0xFF32CD32),
                            iconText = "WING",
                            title = "WING PAY",
                            isSelected = state.selectedMethod == "WING PAY",
                            onClick = { viewModel.updateSelectedMethod("WING PAY") }
                        )

                        PaymentMethodOption(
                            iconColor = Color(0xFF0099CC),
                            iconText = "WOORI",
                            title = "WOORI PAY",
                            isSelected = state.selectedMethod == "WOORI PAY",
                            onClick = { viewModel.updateSelectedMethod("WOORI PAY") }
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Pay Now Button
                    Button(
                        onClick = {
                            viewModel.processPayment()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LoginGoldenYellow
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 6.dp
                        ),
                        enabled = !state.isLoading && state.selectedMethod.isNotEmpty()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = LoginWhite
                            )
                        } else {
                            Text(
                                text = "Pay Now",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = LoginWhite,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun PaymentMethodHeader(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = LoginWhite,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Payment",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = LoginWhite
            )

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
fun PaymentMethodOption(
    iconColor: Color,
    iconText: String,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = iconColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iconText,
                fontSize = 24.sp,
                color = LoginWhite
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Title
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = HomeTextDark,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Radio Button
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = LoginTealGreen
            )
        )
    }
}
