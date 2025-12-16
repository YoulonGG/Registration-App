package com.example.registration_app.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registration_app.ui.theme.SplashDarkTeal
import com.example.registration_app.ui.theme.SplashGoldenYellow
import com.example.registration_app.ui.theme.SplashWhite
import com.example.registration_app.util.DrawableResources

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SplashContent(
        onGetStartClick = {
            viewModel.completeOnboarding()
            if (state.isAuthenticated) {
                onNavigateToHome()
            } else {
                onNavigateToLogin()
            }
        }
    )
}

@Composable
fun SplashContent(
    onGetStartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashDarkTeal),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "WELCOME",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SplashWhite,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = DrawableResources.Logo),
                contentDescription = "University Logo",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Modern Science",
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = SplashWhite,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "University",
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = SplashWhite,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onGetStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SplashGoldenYellow
                )
            ) {
                Text(
                    text = "GET STARTED",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SplashWhite,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


