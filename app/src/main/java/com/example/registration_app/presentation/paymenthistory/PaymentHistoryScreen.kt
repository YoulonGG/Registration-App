package com.example.registration_app.presentation.paymenthistory

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registration_app.domain.model.PaymentStatus
import com.example.registration_app.presentation.common.ErrorDialog
import com.example.registration_app.ui.theme.HomeTextDark
import com.example.registration_app.ui.theme.LoginTealGreen
import com.example.registration_app.ui.theme.LoginWhite
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PaymentHistoryScreen(
    isAdmin: Boolean = false,
    onNavigateBack: () -> Unit,
    viewModel: PaymentHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(isAdmin) {
        viewModel.loadPaymentHistory(isAdmin)
    }

    state.errorMessage?.let { error ->
        ErrorDialog(
            title = "Error",
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
            PaymentHistoryHeader(
                title = if (isAdmin) "Transaction List" else "Payment History",
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
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    if (state.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = LoginTealGreen)
                        }
                    } else if (state.errorMessage != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Text(
                                    text = "Error loading payment history",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF44336),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = state.errorMessage ?: "Unknown error",
                                    fontSize = 14.sp,
                                    color = HomeTextDark.copy(alpha = 0.6f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else if (state.transactions.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Text(
                                    text = "No payment history found",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = HomeTextDark.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = if (isAdmin) "No payment transactions have been made yet." else "You haven't made any payments yet.",
                                    fontSize = 14.sp,
                                    color = HomeTextDark.copy(alpha = 0.6f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 24.dp,
                                vertical = 16.dp
                            ),
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.transactions,
                                key = { it.id }
                            ) { transaction ->
                                PaymentTransactionCard(
                                    transaction = transaction,
                                    isAdmin = isAdmin,
                                    onDelete = { viewModel.deleteTransaction(transaction.id, isAdmin) },
                                    isDeleting = state.isDeleting,
                                    showDeleteButton = true // Show delete for both admin and students (students can only delete their own)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentHistoryHeader(
    title: String,
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
                text = title,
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
fun PaymentTransactionCard(
    transaction: com.example.registration_app.domain.model.PaymentTransaction,
    isAdmin: Boolean = false,
    onDelete: () -> Unit = {},
    isDeleting: Boolean = false,
    showDeleteButton: Boolean = true
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Determine status for display
    val isPaid = transaction.status == PaymentStatus.SUCCESS
    val isFailed = transaction.status == PaymentStatus.FAILED
    val statusText = when (transaction.status) {
        PaymentStatus.SUCCESS -> "Paid"
        PaymentStatus.FAILED -> "Unpaid"
        PaymentStatus.PENDING -> "Pending"
    }
    val statusIcon = if (isPaid) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    val statusIconColor = if (isPaid) Color(0xFF2196F3) else Color(0xFFF44336) // Blue for Paid, Red for Not Pay/Unpaid
    val statusTextColor = if (isFailed) Color(0xFFF44336) else HomeTextDark.copy(alpha = 0.7f) // Red for Unpaid, gray for others
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5) // Light gray background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon (Left)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = statusIconColor.copy(alpha = 0.1f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = statusText,
                    tint = statusIconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User Information (Middle)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (isAdmin) {
                    Text(
                        text = transaction.studentName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = HomeTextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = statusText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = statusTextColor
                    )
                } else {
                    Text(
                        text = transaction.major,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = HomeTextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = transaction.course,
                        fontSize = 12.sp,
                        color = HomeTextDark.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = statusText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = statusTextColor
                    )
                }
            }

            // Payment Amount (Right)
            Text(
                text = "$${transaction.price.toInt()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = HomeTextDark
            )

            // Delete Button (show based on showDeleteButton parameter)
            if (showDeleteButton) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(40.dp),
                    enabled = !isDeleting
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFFF44336)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Transaction",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this payment transaction? This action cannot be undone.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Delete", color = LoginWhite)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
