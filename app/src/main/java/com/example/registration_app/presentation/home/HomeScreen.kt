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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
        // Header with logo and university name
        HomeHeader()

        // Banner Image
        Image(
            painter = painterResource(id = DrawableResources.HomeBanner),
            contentDescription = "University Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop
        )

        // Khmer Text Section
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

        }
    }
}

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo
        Image(
            painter = painterResource(id = DrawableResources.Logo),
            contentDescription = "University Logo",
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // University Name
        Text(
            text = "Modern Science University",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = HomeTextDark,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

//@Composable
//fun HomeMenuCard(
//    title: String,
//    icon: ImageVector,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier
//            .shadow(
//                elevation = 4.dp,
//                shape = RoundedCornerShape(16.dp)
//            )
//            .clip(RoundedCornerShape(16.dp))
//            .background(HomeCardBackground)
//            .border(2.5.dp, HomeYellowBorder, RoundedCornerShape(16.dp))
//            .clickable { onClick() }
//            .padding(vertical = 20.dp, horizontal = 12.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Icon(
//            imageVector = icon,
//            contentDescription = title,
//            modifier = Modifier.size(56.dp),
//            tint = HomeYellowBorder
//        )
//        Spacer(modifier = Modifier.height(12.dp))
//        Text(
//            text = title,
//            fontSize = 13.sp,
//            fontWeight = FontWeight.SemiBold,
//            color = HomeTextDark,
//            textAlign = TextAlign.Center,
//            lineHeight = 16.sp
//        )
//    }
//}
//
//@Composable
//fun HomeMenuCardWide(
//    title: String,
//    icon: ImageVector,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier
//            .shadow(
//                elevation = 4.dp,
//                shape = RoundedCornerShape(16.dp)
//            )
//            .clip(RoundedCornerShape(16.dp))
//            .background(HomeCardBackground)
//            .border(2.5.dp, HomeYellowBorder, RoundedCornerShape(16.dp))
//            .clickable { onClick() }
//            .padding(vertical = 18.dp, horizontal = 20.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(
//            imageVector = icon,
//            contentDescription = title,
//            modifier = Modifier.size(56.dp),
//            tint = HomeYellowBorder
//        )
//        Spacer(modifier = Modifier.width(16.dp))
//        Text(
//            text = title,
//            fontSize = 15.sp,
//            fontWeight = FontWeight.SemiBold,
//            color = HomeTextDark,
//            style = MaterialTheme.typography.titleMedium
//        )
//    }
//}
