package com.wingspan.aimediahub.ui.theme

import android.os.Build
import androidx.compose.runtime.Composable


import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.wingspan.aimediahub.R
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.viewmodel.FacebookViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateScreen( bottomNavController: NavHostController,
                  rootNavController: NavHostController, viewModel: FacebookViewModel = hiltViewModel()) {

    var postText by remember { mutableStateOf("") }
    val prefs = viewModel.prefs
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 1️⃣ Top Bar
        TopBarWithFacebookImage(prefs)

        Spacer(modifier = Modifier.height(10.dp))
        InfoCardModern(
            icon = Icons.Default.CalendarToday,
            iconBackground = Color(0xFF42A5F5),
            title = "Day",
            content = "day",
            onClick = {
                rootNavController.navigate("dayscreen")
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        InfoCardModern(
            icon = Icons.Default.CalendarToday,
            iconBackground = Color(0xFF66BB6A),
            title = "Calendar",
            content ="calender",
            onClick = {
                rootNavController.navigate("dayscreen")
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        InfoCardModern(
            icon = Icons.Default.Add,
            iconBackground = Color(0xFFEF5350),
            title = "Create Post",
            content = "create post",
            onClick = {
                rootNavController.navigate("create_post_screen") // Navigate to full-screen post creation
            }
        )


    }
}

@Composable
fun InfoCardModern(
    icon: ImageVector,
    iconTint: Color = Color.White,
    iconBackground: Color = Color.Blue,
    title: String,
    content: String,
    contentColor: Color = Color.Black,
    onClick: (() -> Unit)? = null // optional click lambda
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // adjusts height based on content
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)) // light gray background
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Icon inside a circle
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title Icon",
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Texts
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = contentColor
                )

            }
        }
    }
}

@Composable
fun TopBarWithFacebookImage(prefs: Prefs) {
    // Get the Facebook page image URL from preferences
    val fbImageUrl = prefs.getFbPageImage() ?: "" // fallback to empty if not saved

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Connected Accounts",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Display Facebook page image
        if (fbImageUrl.isNotEmpty()) {
            AsyncImage(
                model = fbImageUrl,
                contentDescription = "Facebook Account",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable {
                        // Optional: Open account/profile
                    },
                contentScale = ContentScale.Crop
            )
        } else {
            // Fallback icon if image not available
            Icon(
                painter = painterResource(id = R.drawable.ic_fb), // static drawable
                contentDescription = "Facebook Account",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
        }
    }
}
