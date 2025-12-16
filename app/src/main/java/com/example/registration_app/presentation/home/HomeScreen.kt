package com.example.registration_app.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registration_app.presentation.home.components.HomeContentCard
import com.example.registration_app.presentation.home.components.HomeHeader
import com.example.registration_app.presentation.home.components.HomeHorizontalContentCard
import com.example.registration_app.ui.theme.HomeCardBorder
import com.example.registration_app.ui.theme.HomeTextDark
import com.example.registration_app.ui.theme.LoginWhite
import com.example.registration_app.util.DrawableResources

data class HomeMenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    onSignOut: () -> Unit,
    onNavigateToStudentRegistration: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginWhite)
    ) {
        HomeHeader()

        Image(
            painter = painterResource(id = DrawableResources.HomeBanner),
            contentDescription = "University Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "ចង់ចេះត្រូវខំរៀន ចង់មានត្រូវខំរក",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = HomeTextDark,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 24.dp)
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HomeContentCard(
                    modifier = Modifier.weight(1f),
                    borderColor = com.example.registration_app.ui.theme.HomeCardBorder.copy(alpha = 0.5f),
                    imagePainter = painterResource(DrawableResources.home_information),
                    text = "Information",
                    onClick = { /* TODO: Navigate to Information screen */ }
                )
                Spacer(modifier = Modifier.width(5.dp))
                HomeContentCard(
                    modifier = Modifier.weight(1f),
                    borderColor = com.example.registration_app.ui.theme.HomeCardBorder.copy(alpha = 0.5f),
                    imagePainter = painterResource(DrawableResources.home_payment),
                    text = "Payment",
                    onClick = { /* TODO: Navigate to Payment screen */ }
                )
                Spacer(modifier = Modifier.width(5.dp))
                HomeContentCard(
                    modifier = Modifier.weight(1f),
                    borderColor = com.example.registration_app.ui.theme.HomeCardBorder.copy(alpha = 0.5f),
                    imagePainter = painterResource(DrawableResources.home_profile),
                    text = "Student Profile",
                    onClick = { /* TODO: Navigate to Student Profile screen */ }
                )
            }
            HomeHorizontalContentCard(
                borderColor = HomeCardBorder.copy(alpha = 0.5f),
                imagePainter = painterResource(DrawableResources.home_profile),
                text = "Student Registration",
                onClick = onNavigateToStudentRegistration
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HomeContentCard(
                    modifier = Modifier.weight(1f),
                    borderColor = HomeCardBorder.copy(alpha = 0.5f),
                    imagePainter = painterResource(DrawableResources.home_study_program),
                    text = "Study Program",
                    onClick = { /* TODO: Navigate to Study Program screen */ }
                )
                Spacer(modifier = Modifier.width(5.dp))
                HomeContentCard(
                    modifier = Modifier.weight(1f),
                    borderColor = HomeCardBorder.copy(alpha = 0.5f),
                    imagePainter = painterResource(DrawableResources.home_nursery_class),
                    text = "Nursery Class",
                    onClick = { /* TODO: Navigate to Nursery Class screen */ }
                )
                Spacer(modifier = Modifier.width(5.dp))
                HomeContentCard(
                    modifier = Modifier.weight(1f),
                    borderColor = HomeCardBorder.copy(alpha = 0.5f),
                    imagePainter = painterResource(DrawableResources.home_research_task),
                    text = "Research Task",
                    onClick = { /* TODO: Navigate to Research Task screen */ }
                )
            }
        }
    }
}




