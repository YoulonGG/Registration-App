package com.example.registration_app.presentation.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.registration_app.ui.theme.LoginGoldenYellow
import com.example.registration_app.ui.theme.LoginWhite
import com.example.registration_app.util.DrawableResources
import kotlinx.coroutines.launch

// Different background colors for each onboarding page
val OnboardingColor1 = Color(0xFF0D7377) // Teal green
val OnboardingColor2 = Color(0xFF1A237E) // Deep blue
val OnboardingColor3 = Color(0xFF6A1B9A) // Purple

data class OnboardingPage(
    val title: String,
    val description: String,
    val backgroundColor: Color,
    val icon: ImageVector? = null,
    val imageRes: Int = DrawableResources.Logo
)

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    onSkip: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to Modern Science University",
            description = "Your gateway to seamless student registration and academic management. Get started with our intuitive platform designed for students and administrators.",
            backgroundColor = OnboardingColor1,
            icon = Icons.Default.School
        ),
        OnboardingPage(
            title = "Easy Registration Process",
            description = "Register for courses and majors with just a few taps. Our streamlined process makes it easy to manage your academic journey.",
            backgroundColor = OnboardingColor2,
            icon = Icons.Default.Assignment
        ),
        OnboardingPage(
            title = "Stay Connected",
            description = "Access your profile, track registrations, and stay updated with all university information in one convenient place.",
            backgroundColor = OnboardingColor3,
            icon = Icons.Default.Notifications
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    
    // Observe current page changes
    var currentPage by remember { mutableIntStateOf(pagerState.currentPage) }
    
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            currentPage = page
        }
    }
    
    // Animate background color based on current page
    val targetColor = pages[currentPage].backgroundColor

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(targetColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button with better visibility
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, end = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Skip",
                        color = LoginWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                OnboardingPageContent(
                    page = pages[pageIndex],
                    isActive = pageIndex == currentPage,
                    isLastPage = pageIndex == pages.size - 1
                )
            }

            // Page indicators
            Row(
                modifier = Modifier
                    .padding(bottom = if (currentPage == pages.size - 1) 24.dp else 40.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.forEachIndexed { index, _ ->
                    val isSelected = currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) LoginGoldenYellow else LoginWhite.copy(alpha = 0.6f)
                            )
                    )
                }
            }

            // Get Started button (only on last page)
            if (currentPage == pages.size - 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 40.dp)
                ) {
                    Button(
                        onClick = onGetStarted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = Color.Black.copy(alpha = 0.4f),
                                ambientColor = Color.Black.copy(alpha = 0.3f)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LoginGoldenYellow,
                            contentColor = LoginWhite
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Text(
                            text = "Get Started",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = LoginWhite,
                            letterSpacing = 1.sp
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    isActive: Boolean,
    isLastPage: Boolean = false
) {
    // Animation for content appearance
    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.6f,
        animationSpec = tween(durationMillis = 300),
        label = "alpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.9f,
        animationSpec = tween(durationMillis = 300),
        label = "scale"
    )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 20.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        // Icon or Image with better styling
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(scale)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    spotColor = Color.Black.copy(alpha = 0.2f),
                    ambientColor = Color.Black.copy(alpha = 0.1f)
                )
                .background(
                    color = LoginWhite.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (page.icon != null) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = LoginWhite
                )
            } else {
                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(160.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = page.title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = LoginWhite,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = page.description,
            fontSize = 16.sp,
            color = LoginWhite.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        
        // Add extra spacing on last page to ensure button is visible
        if (isLastPage) {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
