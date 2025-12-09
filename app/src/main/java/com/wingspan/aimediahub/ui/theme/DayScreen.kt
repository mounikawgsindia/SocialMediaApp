package com.wingspan.aimediahub.ui.theme

import androidx.compose.runtime.Composable
import com.wingspan.aimediahub.models.HourSlot

// Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider

// Material 3
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

// UI
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.wingspan.aimediahub.R
import com.wingspan.aimediahub.ui.theme.bottonpages.getIcon
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.viewmodel.FacebookViewModel

// Utilities
import java.util.Calendar

// ================================

@Composable
fun DayScreen(navController: NavHostController,viewModel: FacebookViewModel= hiltViewModel()) {

    val hours = generate24Hours()
    val prefs = viewModel.prefs
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val fbImageUrl = prefs.getFbPageImage() ?: ""
    Column(  modifier = Modifier
        .fillMaxSize()
        .padding(WindowInsets.statusBars.asPaddingValues())) {

        // ✅ TOP BAR
        TopBarForDay(navController,prefs)

        //social media acooutn
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE7F0FF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    // Social icon fully colored for Facebook
                    Icon(
                        painter = painterResource(id =R.drawable.ic_fb),
                        contentDescription = "fb",
                        tint =  Color(0xFF1877F2) ,
                        modifier = Modifier.size(30.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    // Show page image on the right side if exists
                    if (fbImageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = fbImageUrl,
                            contentDescription = "Facebook Account",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

        }
        // ✅ TIME LINE
        LazyColumn {
            items(hours) { hour ->
                HourRow(
                    hour = hour.hour,
                    isSelected = hour.hour == currentHour,
                    isNow = hour.hour == currentHour,
                    currentHour = currentHour,
                    posts = if (hour.hour == 10)
                        listOf("Hello everyone", "Hi facebook users")
                    else emptyList(),
                    onAddClick = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun TopBarForDay(navController: NavHostController, prefs: Prefs) {

    val fbImageUrl = prefs.getFbPageImage() ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically   // ✅ Correct alignment
    ) {

        // ✅ BACK BUTTON
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFF2F2F2),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { navController.popBackStack() }
                .padding(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_icon),
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // ✅ DAY TEXT
        Text(
            text = "Day",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )

        // ✅ PROFILE IMAGE
        if (fbImageUrl.isNotEmpty()) {
            AsyncImage(
                model = fbImageUrl,
                contentDescription = "Facebook Account",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
        }
    }
}


// ================================

@Composable
fun HourRow(
    hour: Int,
    isSelected: Boolean,
    isNow: Boolean = false,
    currentHour: Int,
    posts: List<String> = emptyList(),
    onAddClick: () -> Unit = {}
) {

    val isPast = hour < currentHour
    val shouldShowRow = !isPast || posts.isNotEmpty()
    // ❌ Hide empty past hours
    if (!shouldShowRow) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .padding(16.dp)
    ) {

        // ✅ LEFT TIME BOX
        Box(
            modifier = Modifier
                .width(80.dp)
                .background(
                    when {
                        isPast -> Color(0xFFE0E0E0)
                        isSelected -> Color(0xFFDDEBFF)
                        else -> Color(0xFFEEF3FF)
                    },
                    RoundedCornerShape(12.dp)
                )
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatHour(hour),
                fontWeight = FontWeight.Medium,
                color = if (isPast) Color.Gray else Color(0xFF1A237E)
            )
        }

        Spacer(Modifier.width(8.dp))

        // ✅ CENTER CONTENT
        Column(modifier = Modifier.weight(1f)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Divider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color.Gray
                )

                if (isNow) {
                    Spacer(Modifier.width(6.dp))
                    NowTag()
                }
            }

            Spacer(Modifier.height(8.dp))

            // ✅ Post list
            Column {
                posts.forEach { post ->
                    PostItem(post)
                }
            }
        }

        // ✅ PLUS BUTTON ONLY FOR NOW & FUTURE HOURS
        if (hour >= currentHour) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color(0xFF1976D2),
                modifier = Modifier
                    .size(28.dp)
                    .padding(6.dp)
                    .clickable { onAddClick() }
            )
        }
    }
}

// ================================

@Composable
fun NowTag() {
    Box(
        modifier = Modifier
            .background(Color(0xFF4CAF50), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            "NOW",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

// ================================

fun formatHour(hour: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val formatted = if (hour % 12 == 0) 12 else hour % 12
    return "$formatted:00 $amPm"
}

// ================================

@Composable
fun PostItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .background(Color(0xFF80CBC4), RoundedCornerShape(4.dp))
        )

        Spacer(Modifier.width(8.dp))

        Column {
            Text(text, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ================================

fun generate24Hours(): List<HourSlot> {
    return (0..23).map { HourSlot(hour = it) }
}
