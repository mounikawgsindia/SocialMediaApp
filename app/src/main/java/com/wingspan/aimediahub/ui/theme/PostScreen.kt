package com.wingspan.aimediahub.ui.theme

import android.os.Build
import androidx.compose.runtime.Composable


import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wingspan.aimediahub.models.ScheduledPost
import java.time.LocalDate
import java.time.format.DateTimeFormatter




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateScreen(navController: NavHostController) {
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }

    // sample posts
    val posts = remember { samplePosts() }

    Scaffold(
        topBar = { PlannerTopBar(selectedDate = selectedDate, onDateClick = { /* show date picker */ }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("New post") },
                onClick = { /* open composer */ },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                containerColor = GradientEnd,
                shape = RoundedCornerShape(24.dp)
            )
        },
        bottomBar = { PlannerBottomBar() }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {

            // Search + Filters
            SearchAndFilters()

            // Date strip
            DateStrip(selectedDate = selectedDate, onPrev = { selectedDate = selectedDate.minusDays(1) }, onNext = { selectedDate = selectedDate.plusDays(1) })

            // Timeline
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(posts) { post ->
                    TimeLineItem(post = post)
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp)) // bottom padding for FAB
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerTopBar(selectedDate: LocalDate, onDateClick: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(text = "Day", fontWeight = FontWeight.Bold)
                Text(text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")), fontSize = 12.sp, color = Color.Gray)
            }
        },
        navigationIcon = {
            IconButton(onClick = { /* open drawer */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = onDateClick) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Pick date")
            }
            IconButton(onClick = { /* open settings */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Brush.horizontalGradient(listOf(GradientStart, GradientEnd)).asColor())
    )
}

// helper to convert Brush to a Color for top appbar background
private fun Brush.asColor(): Color = Color(0xFFFFFFFF) // Workaround: TopAppBar accepts Color; we'll use Surface below if you need gradient top bar

@Composable
fun SearchAndFilters() {
    Row(modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            placeholder = { Text("Search") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .clickable { /* open filters */ }, contentAlignment = Alignment.Center) {
            Text("⚙", fontSize = 18.sp)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateStrip(selectedDate: LocalDate, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {

        IconButton(onClick = onPrev) {
            Icon(painter = painterResource(id = android.R.drawable.arrow_up_float), contentDescription = "Prev")
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = selectedDate.dayOfWeek.toString(), fontWeight = FontWeight.Medium, color = Color.DarkGray)
            Text(text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")), fontWeight = FontWeight.Bold)
        }

        IconButton(onClick = onNext) {
            Icon(painter = painterResource(id = android.R.drawable.arrow_down_float), contentDescription = "Next")
        }
    }
}

@Composable
fun TimeLineItem(post: ScheduledPost) {
    Card(modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(6.dp)) {

        Row(modifier = Modifier
            .background(
                Brush.horizontalGradient(listOf(post.color.copy(alpha = 0.12f), Color.White))
            )
            .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {

            // left time column
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp)) {
                Text(text = post.time, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(post.color))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // platform pill + overflow
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .background(post.color.copy(alpha = 0.18f), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(text = post.platform, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "\u2022 Scheduled", color = Color.Gray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = post.content, maxLines = 3, overflow = TextOverflow.Ellipsis)

                Spacer(modifier = Modifier.height(8.dp))

                // actions row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Edit", color = GradientStart, fontWeight = FontWeight.Medium)
                    Text(text = "Duplicate", color = GradientStart, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun PlannerBottomBar() {
    NavigationBar(containerColor = Color.White, tonalElevation = 4.dp) {
        NavigationBarItem(selected = false, onClick = { /* analytics */ }, label = { Text("Analytics") }, icon = { Icon(painterResource(id = android.R.drawable.ic_menu_info_details), contentDescription = "Analytics") })
        NavigationBarItem(selected = false, onClick = { /* inbox */ }, label = { Text("Inbox") }, icon = { Icon(painterResource(id = android.R.drawable.ic_dialog_email), contentDescription = "Inbox") })
        NavigationBarItem(selected = true, onClick = { /* planning */ }, label = { Text("Planning") }, icon = { Icon(painterResource(id = android.R.drawable.ic_menu_my_calendar), contentDescription = "Planning") })
        NavigationBarItem(selected = false, onClick = { /* settings */ }, label = { Text("Settings") }, icon = { Icon(painterResource(id = android.R.drawable.ic_menu_preferences), contentDescription = "Settings") })
    }
}

// ---------- Sample Content ----------
fun samplePosts(): List<ScheduledPost> = listOf(
    ScheduledPost(1, "9:15 AM", "Facebook", "Good morning everyone! Here's today's update and a quick tip to start your day.", Accent1),
    ScheduledPost(2, "10:00 AM", "Instagram", "New carousel: 5 ways to improve productivity at work. Swipe to see!", Color(0xFF42A5F5)),
    ScheduledPost(3, "1:30 PM", "LinkedIn", "Hiring update: We're looking for an Android developer experienced in Jetpack Compose.", Color(0xFFAB47BC)),
    ScheduledPost(4, "5:00 PM", "Twitter", "Short tip: Use WorkManager for scheduled background posts.", Color(0xFF26A69A))
)

