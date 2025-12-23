package com.example.registration_app.presentation.adminhome

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.registration_app.presentation.studentregistration.components.MajorCard
import com.example.registration_app.ui.theme.HomeTextDark
import com.example.registration_app.ui.theme.LoginTealGreen
import com.example.registration_app.ui.theme.LoginWhite
import com.example.registration_app.util.DrawableResources

@Composable
fun AdminSubjectListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToStudentList: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginTealGreen)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AdminSubjectListHeader(
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
                            painter = painterResource(id = DrawableResources.student_registration_banner),
                            contentDescription = "Subject Selection Illustration",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Text(
                            text = "Please Choose The Subject",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = HomeTextDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MajorCard(
                                    modifier = Modifier.weight(1f),
                                    imagePainter = painterResource(DrawableResources.computer_science_icon),
                                    text = "Computer Science",
                                    onClick = { onNavigateToStudentList("Computer Science") }
                                )
                                MajorCard(
                                    modifier = Modifier.weight(1f),
                                    imagePainter = painterResource(DrawableResources.finance_icon),
                                    text = "IT Finance",
                                    onClick = { onNavigateToStudentList("IT Finance") }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MajorCard(
                                    modifier = Modifier.weight(1f),
                                    imagePainter = painterResource(DrawableResources.business_it),
                                    text = "Business IT",
                                    onClick = { onNavigateToStudentList("Business IT") }
                                )
                                MajorCard(
                                    modifier = Modifier.weight(1f),
                                    imagePainter = painterResource(DrawableResources.account_icon),
                                    text = "Accountant",
                                    onClick = { onNavigateToStudentList("Accountant") }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MajorCard(
                                    modifier = Modifier.weight(1f),
                                    imagePainter = painterResource(DrawableResources.fintech_icon),
                                    text = "Fintech",
                                    onClick = { onNavigateToStudentList("Fintech") }
                                )
                                MajorCard(
                                    modifier = Modifier.weight(1f),
                                    imagePainter = painterResource(DrawableResources.data_science_icon),
                                    text = "Data Science",
                                    onClick = { onNavigateToStudentList("Data Science") }
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
fun AdminSubjectListHeader(
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
                text = "List Student",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = LoginWhite
            )

            Spacer(modifier = Modifier.weight(1f))
            
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

class RoundedTopLeftShape(private val radius: androidx.compose.ui.unit.Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): Outline {
        val radiusPx = with(density) { radius.toPx() }
        val path = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, radiusPx)
            cubicTo(
                x1 = 0f, y1 = radiusPx * 0.55f,
                x2 = radiusPx * 0.45f, y2 = 0f,
                x3 = radiusPx, y3 = 0f
            )
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}
