package com.example.registration_app.presentation.changepassword

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    email: String,
    onNavigateToLogin: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(email) {
        viewModel.setEmail(email)
    }

    // Note: Password change is completed via email link
    // After OTP verification, reset email is sent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Change Password",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = if (state.resetEmailSent) {
                "Password reset email sent! Please check your email and click the link to reset your password."
            } else {
                "Please enter your new password. A password reset email will be sent to your email address."
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp),
            color = if (state.resetEmailSent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )

        if (!state.resetEmailSent) {
            OutlinedTextField(
                value = state.newPassword,
                onValueChange = { viewModel.handleIntent(ChangePasswordIntent.UpdateNewPassword(it)) },
                label = { Text("New Password") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "New Password")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !state.isLoading
            )

            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.handleIntent(ChangePasswordIntent.UpdateConfirmPassword(it)) },
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Confirm Password")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !state.isLoading
            )
        }

        state.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        if (!state.resetEmailSent) {
            Button(
                onClick = { viewModel.handleIntent(ChangePasswordIntent.ChangePassword) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Send Password Reset Email", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        TextButton(onClick = onNavigateToLogin) {
            Text("Back to Login")
        }
    }
}
