package com.example.registration_app.presentation.studentregistration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registration_app.presentation.common.ErrorDialog
import com.example.registration_app.presentation.common.SuccessDialog
import com.example.registration_app.presentation.studentregistration.components.RegistrationTextField
import com.example.registration_app.ui.theme.HomeTextDark
import com.example.registration_app.ui.theme.LoginGoldenYellow
import com.example.registration_app.ui.theme.LoginTealGreen
import com.example.registration_app.ui.theme.LoginWhite
import com.example.registration_app.util.DrawableResources

@Composable
fun MajorRegistrationScreen(
    majorName: String,
    onNavigateBack: () -> Unit,
    onNavigateToPayment: (com.example.registration_app.domain.model.StudentRegistration, String) -> Unit = { _, _ -> },
    onRegistrationSuccess: () -> Unit = {},
    viewModel: MajorRegistrationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(majorName) {
        viewModel.setMajorName(majorName)
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.resetSuccessState()
            onRegistrationSuccess()
        }
    }

    // Show success dialog
    if (state.isSuccess) {
        SuccessDialog(
            title = "Registration Successful",
            message = "Your registration has been submitted successfully!",
            onDismiss = {
                viewModel.resetSuccessState()
                onRegistrationSuccess()
            }
        )
    }

    // Show error dialog
    state.errorMessage?.let { error ->
        ErrorDialog(
            title = "Cannot Register",
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
            MajorRegistrationHeader(
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
                            shape = RoundedTopLeftShape(radius = 75.dp),
                            spotColor = Color.Black.copy(alpha = 0.25f),
                            ambientColor = Color.Black.copy(alpha = 0.15f)
                        )
                        .background(
                            color = LoginWhite,
                            shape = RoundedTopLeftShape(radius = 75.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        Image(
                            painter = painterResource(id = DrawableResources.registration_form_banner),
                            contentDescription = "Registration Illustration",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Major Title
                        Text(
                            text = majorName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = HomeTextDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Student Name
                        Text(
                            text = "Student Name",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = HomeTextDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RegistrationTextField(
                            value = state.studentName,
                            onValueChange = { viewModel.updateStudentName(it) },
                            placeholder = "Full Name",
                            icon = Icons.Default.Person,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Email Address
                        Text(
                            text = "Email Address",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = HomeTextDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RegistrationTextField(
                            value = state.email,
                            onValueChange = { viewModel.updateEmail(it) },
                            placeholder = "Your Email",
                            icon = Icons.Default.Email,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Gender
                        Text(
                            text = "Gender",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = HomeTextDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GenderSelection(
                            selectedGender = state.gender,
                            onGenderSelected = { viewModel.updateGender(it) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Phone Number",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = HomeTextDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RegistrationTextField(
                            value = state.phoneNumber,
                            onValueChange = { viewModel.updatePhoneNumber(it) },
                            placeholder = "Your Number",
                            icon = Icons.Default.Phone,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Address
                        Text(
                            text = "Address",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = HomeTextDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RegistrationTextField(
                            value = state.address,
                            onValueChange = { viewModel.updateAddress(it) },
                            placeholder = "Your Address",
                            icon = Icons.Default.Home,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Date of Birth
                        Text(
                            text = "Date of birth",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = HomeTextDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DateOfBirthPicker(
                            selectedDate = state.dateOfBirth,
                            onDateClick = { viewModel.showDatePicker() },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        if (state.showDatePicker) {
                            DatePickerDialogContent(
                                initialDate = state.dateOfBirth,
                                onDateSelected = { timestamp ->
                                    viewModel.setDateOfBirth(timestamp)
                                },
                                onDismiss = { viewModel.dismissDatePicker() }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Major (Read-only display)
                        Text(
                            text = "Major",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = HomeTextDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = majorName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = HomeTextDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Major",
                                    tint = LoginTealGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Spacer(modifier = Modifier.height(28.dp))

                        // Submit Button
                        Button(
                            onClick = { viewModel.submitRegistration() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LoginGoldenYellow,
                                disabledContainerColor = LoginGoldenYellow,
                                contentColor = LoginWhite,
                                disabledContentColor = LoginWhite
                            ),
                            enabled = !state.isLoading
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = LoginWhite,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    text = "Submit Registration",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LoginWhite,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MajorRegistrationHeader(
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
                text = "Student Registration Form",
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
fun GenderSelection(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = { onGenderSelected("Female") }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            RadioButton(
                selected = selectedGender == "Female",
                onClick = { onGenderSelected("Female") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = LoginTealGreen
                )
            )
            Spacer(modifier = Modifier.width(0.dp))
            Text(
                text = "Female",
                fontSize = 14.sp,
                color = HomeTextDark
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp, horizontal = 0.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = { onGenderSelected("Male") }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            RadioButton(
                selected = selectedGender == "Male",
                onClick = { onGenderSelected("Male") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = LoginTealGreen
                )
            )
            Spacer(modifier = Modifier.width(0.dp))
            Text(
                text = "Male",
                fontSize = 14.sp,
                color = HomeTextDark
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateOfBirthPicker(
    selectedDate: Long?,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateText = if (selectedDate != null) {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = selectedDate
        }
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val month = calendar.get(java.util.Calendar.MONTH) + 1
        val year = calendar.get(java.util.Calendar.YEAR)
        String.format("%02d/%02d/%04d", day, month, year)
    } else {
        "Select Date of Birth"
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onDateClick
            )
            .background(
                color = Color(0xFFE0F2F1),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dateText,
                fontSize = 16.sp,
                color = if (selectedDate != null) HomeTextDark else Color.Gray,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Date Picker",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogContent(
    initialDate: Long?,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(it)
                    } ?: onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

