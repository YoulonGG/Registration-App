package com.example.registration_app.presentation.adminhome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registration_app.domain.model.StudentRegistration
import com.example.registration_app.ui.theme.LoginTealGreen
import com.example.registration_app.ui.theme.LoginWhite
import com.example.registration_app.util.DrawableResources
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AdminStudentListScreen(
    majorName: String,
    onNavigateBack: () -> Unit,
    viewModel: AdminStudentListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(majorName) {
        viewModel.loadStudents(majorName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginTealGreen)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
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
                        text = majorName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = LoginWhite
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            // Student List Content
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
                    if (state.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Loading...",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    } else if (state.students.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No students registered for this subject",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 24.dp,
                                top = 20.dp,
                                end = 24.dp,
                                bottom = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.students) { student ->
                                StudentCard(student = student)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentCard(student: StudentRegistration) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = DrawableResources.home_profile),
                    contentDescription = "Profile",
                    modifier = Modifier.size(50.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            // Student Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = student.studentName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sex: ${student.gender}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Date/Status
            Text(
                text = formatRegistrationDate(student.registrationDate),
                fontSize = 13.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun formatRegistrationDate(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val todayStart = calendar.timeInMillis
    
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val yesterdayStart = calendar.timeInMillis
    
    val registrationDate = Date(timestamp)
    
    return when {
        timestamp >= todayStart -> "Today"
        timestamp >= yesterdayStart -> "Yesterday"
        else -> {
            val sdf = SimpleDateFormat("dd-MMM-yy", Locale.getDefault())
            sdf.format(registrationDate)
        }
    }
}
