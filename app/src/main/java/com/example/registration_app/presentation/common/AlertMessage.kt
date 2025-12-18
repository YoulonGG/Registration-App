package com.example.registration_app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AlertType {
    SUCCESS,
    WARNING,
    ERROR,
    INFORMATION
}

data class AlertColors(
    val background: Color,
    val border: Color,
    val iconBackground: Color,
    val textColor: Color
)

@Composable
fun getAlertColors(type: AlertType): AlertColors {
    return when (type) {
        AlertType.SUCCESS -> AlertColors(
            background = Color(0xFFE6FAE6),
            border = Color(0xFF4CAF50),
            iconBackground = Color(0xFF4CAF50),
            textColor = Color(0xFF2E7D32)
        )
        AlertType.WARNING -> AlertColors(
            background = Color(0xFFFFFBE6),
            border = Color(0xFFFFC107),
            iconBackground = Color(0xFFFFC107),
            textColor = Color(0xFFF57C00)
        )
        AlertType.ERROR -> AlertColors(
            background = Color(0xFFFAE6E6),
            border = Color(0xFFF44336),
            iconBackground = Color(0xFFF44336),
            textColor = Color(0xFFC62828)
        )
        AlertType.INFORMATION -> AlertColors(
            background = Color(0xFFE6F3FA),
            border = Color(0xFF2196F3),
            iconBackground = Color(0xFF2196F3),
            textColor = Color(0xFF1565C0)
        )
    }
}

@Composable
fun getAlertIcon(type: AlertType): ImageVector {
    return when (type) {
        AlertType.SUCCESS -> Icons.Default.CheckCircle
        AlertType.WARNING -> Icons.Default.Warning
        AlertType.ERROR -> Icons.Default.Error
        AlertType.INFORMATION -> Icons.Default.Info
    }
}

@Composable
fun AlertMessage(
    type: AlertType,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = getAlertColors(type)
    val icon = getAlertIcon(type)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colors.background,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = colors.border,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Icon on the left
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title and message column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Dismiss button on the right
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = colors.textColor,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onDismiss)
            )
        }
    }
}
