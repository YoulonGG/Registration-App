package com.example.registration_app.presentation.adminhome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registration_app.presentation.common.ErrorDialog
import com.example.registration_app.presentation.studentregistration.components.RegistrationTextField
import com.example.registration_app.ui.theme.HomeTextDark
import com.example.registration_app.ui.theme.LoginGoldenYellow
import com.example.registration_app.ui.theme.LoginTealGreen
import com.example.registration_app.ui.theme.LoginWhite

@Composable
fun AdminProfileScreen(
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: AdminProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadAdminProfile()
    }

    // Show error dialog
    state.errorMessage?.let { error ->
        ErrorDialog(
            title = "Cannot Load Profile",
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
            AdminProfileHeader(
                onNavigateBack = onNavigateBack,
                onEditClick = { viewModel.enableEditMode() },
                isEditMode = state.isEditMode
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

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(80.dp),
                                tint = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "My Profile",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = HomeTextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Show editable or read-only fields based on edit mode
                    if (state.isEditMode) {
                        EditableAdminProfileFields(
                            state = state,
                            viewModel = viewModel
                        )
                    } else {
                        ReadOnlyAdminProfileFields(
                            firstName = state.editedFirstName,
                            lastName = state.editedLastName,
                            username = state.editedUsername,
                            phoneNumber = state.editedPhoneNumber,
                            gender = state.editedGender,
                            dateOfBirth = state.dateOfBirth
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save/Cancel buttons in edit mode
                    if (state.isEditMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.cancelEditMode() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Gray
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel",
                                    tint = LoginWhite,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Cancel",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LoginWhite
                                )
                            }

                            Button(
                                onClick = { viewModel.saveProfile() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LoginGoldenYellow
                                )
                            ) {
                                if (state.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = LoginWhite
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Save",
                                        tint = LoginWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (state.isSaving) "Saving..." else "Save",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LoginWhite
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Sign Out Button (only show when not in edit mode)
                    if (!state.isEditMode) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                showLogoutDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .height(56.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LoginGoldenYellow
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Sign Out",
                                tint = LoginWhite,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sign Out",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = LoginWhite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Sign Out",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out?",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.signOut()
                        onSignOut()
                    }
                ) {
                    Text(
                        text = "Sign Out",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )
    }
}

@Composable
fun ReadOnlyAdminProfileFields(
    firstName: String,
    lastName: String,
    username: String,
    phoneNumber: String,
    gender: String,
    dateOfBirth: Long?
) {
    ProfileDetailRow(
        label = "First Name",
        value = firstName,
        icon = Icons.Default.Person
    )

    ProfileDetailRow(
        label = "Last Name",
        value = lastName,
        icon = Icons.Default.Person
    )

    ProfileDetailRow(
        label = "Username",
        value = username,
        icon = Icons.Default.Person
    )

    ProfileDetailRow(
        label = "Phone Number",
        value = phoneNumber,
        icon = Icons.Default.Phone
    )

    ProfileDetailRow(
        label = "Gender",
        value = gender,
        icon = Icons.Default.Person
    )

    val dateText = if (dateOfBirth != null) {
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = dateOfBirth
        }
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val month = calendar.get(java.util.Calendar.MONTH) + 1
        val year = calendar.get(java.util.Calendar.YEAR)
        String.format("%02d/%02d/%04d", day, month, year)
    } else {
        ""
    }

    ProfileDetailRow(
        label = "Date of Birth",
        value = dateText,
        icon = Icons.Default.Info,
        isLast = true
    )
}

@Composable
fun EditableAdminProfileFields(
    state: AdminProfileState,
    viewModel: AdminProfileViewModel
) {
    var showDatePicker by remember { mutableStateOf(false) }

    RegistrationTextField(
        value = state.editedFirstName,
        onValueChange = { viewModel.updateEditedFirstName(it) },
        placeholder = "First Name",
        icon = Icons.Default.Person,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    RegistrationTextField(
        value = state.editedLastName,
        onValueChange = { viewModel.updateEditedLastName(it) },
        placeholder = "Last Name",
        icon = Icons.Default.Person,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    RegistrationTextField(
        value = state.editedUsername,
        onValueChange = { viewModel.updateEditedUsername(it) },
        placeholder = "Username",
        icon = Icons.Default.Person,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    RegistrationTextField(
        value = state.editedPhoneNumber,
        onValueChange = { viewModel.updateEditedPhoneNumber(it) },
        placeholder = "Phone Number",
        icon = Icons.Default.Phone,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Gender",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = HomeTextDark,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    GenderSelection(
        selectedGender = state.editedGender,
        onGenderSelected = { viewModel.updateEditedGender(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    DateOfBirthPicker(
        selectedDate = state.dateOfBirth,
        onDateClick = { showDatePicker = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )

    if (showDatePicker) {
        DatePickerDialogContent(
            initialDate = state.dateOfBirth,
            onDateSelected = { timestamp ->
                viewModel.setDateOfBirth(timestamp)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun AdminProfileHeader(
    onNavigateBack: () -> Unit,
    onEditClick: () -> Unit,
    isEditMode: Boolean
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
                text = "Admin profile",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = LoginWhite
            )

            Spacer(modifier = Modifier.weight(1f))
            
            if (!isEditMode) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = LoginWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    }
}

@Composable
fun ProfileDetailRow(
    label: String,
    value: String,
    icon: ImageVector,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = LoginGoldenYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = value.ifEmpty { "" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (value.isEmpty()) Color.Gray.copy(alpha = 0.6f) else HomeTextDark
                    )
                }
            }
        }
        
        if (!isLast) {
            Divider(
                color = Color.LightGray.copy(alpha = 0.5f),
                thickness = 1.dp
            )
        }
    }
}

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
