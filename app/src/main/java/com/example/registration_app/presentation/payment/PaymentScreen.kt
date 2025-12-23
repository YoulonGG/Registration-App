package com.example.registration_app.presentation.payment

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registration_app.domain.model.StudentRegistration
import com.example.registration_app.presentation.common.ErrorDialog
import com.example.registration_app.ui.theme.HomeTextDark
import com.example.registration_app.ui.theme.LoginGoldenYellow
import com.example.registration_app.ui.theme.LoginTealGreen
import com.example.registration_app.ui.theme.LoginWhite
import com.example.registration_app.util.DrawableResources

@Composable
fun PaymentScreen(
    registration: StudentRegistration,
    studentId: String,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Use a stable key to ensure this only runs once per screen instance
    // Check if already initialized to prevent duplicate transactions
    var hasInitialized by remember { mutableStateOf(false) }
    
    LaunchedEffect(studentId) {
        // Only initialize once per studentId
        if (!hasInitialized && state.transaction == null && !state.isLoading) {
            hasInitialized = true
            viewModel.initializePayment(registration, studentId)
        }
    }

    LaunchedEffect(state.transaction?.status) {
        if (state.transaction?.status == com.example.registration_app.domain.model.PaymentStatus.SUCCESS) {
            onPaymentSuccess()
        }
    }

    // Handle back navigation - mark payment as failed
    LaunchedEffect(Unit) {
        // This will be called when screen is disposed
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
            PaymentHeader(
                onNavigateBack = {
                    viewModel.cancelPayment()
                    onNavigateBack()
                }
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
                            shape = RoundedTopLeftShape(radius = 75.dp),
                            spotColor = Color.Black.copy(alpha = 0.25f),
                            ambientColor = Color.Black.copy(alpha = 0.15f)
                        )
                        .background(
                            color = LoginWhite,
                            shape = RoundedTopLeftShape(radius = 75.dp)
                        )
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Payment Illustration
                    Image(
                        painter = painterResource(id = DrawableResources.payment_img),
                        contentDescription = "Payment Illustration",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 24.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Instruction Text
                    Text(
                        text = "Please verify your information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HomeTextDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Information Fields
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PaymentInfoField(
                            label = "Student ID",
                            value = studentId,
                            icon = painterResource(DrawableResources.computer_science_icon)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PaymentInfoField(
                            label = "Student Name",
                            value = registration.studentName,
                            icon = painterResource(DrawableResources.account_icon)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PaymentInfoField(
                            label = "Course",
                            value = registration.course,
                            icon = painterResource(DrawableResources.computer_science_icon)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PaymentInfoField(
                            label = "Major",
                            value = registration.major,
                            icon = painterResource(DrawableResources.computer_science_icon)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PaymentInfoField(
                            label = "Price",
                            value = "$${state.transaction?.price?.toInt() ?: 300}",
                            icon = painterResource(DrawableResources.home_payment)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.cancelPayment()
                                onNavigateBack()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LoginGoldenYellow
                            ),
                            enabled = !state.isProcessing
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = LoginWhite
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.processPayment()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LoginGoldenYellow
                            ),
                            enabled = !state.isProcessing && state.transaction != null
                        ) {
                            if (state.isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = LoginWhite
                                )
                            } else {
                                Text(
                                    text = "Pay Now",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LoginWhite
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun PaymentHeader(
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
fun PaymentInfoField(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.painter.Painter
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = HomeTextDark.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFE0F2F1),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomeTextDark,
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

class RoundedTopLeftShape(private val radius: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): Outline {
        val radiusPx = with(density) { radius.toPx() }
        val path = Path().apply {
            moveTo(0f, radiusPx)
            quadraticBezierTo(0f, 0f, radiusPx, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}
