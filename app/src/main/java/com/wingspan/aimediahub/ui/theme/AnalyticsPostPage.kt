package com.wingspan.aimediahub.ui.theme

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.navigation.NavHostController
import com.wingspan.aimediahub.utils.Prefs

@Composable
fun AnalyticsPostPage(rootNavController: NavHostController, prefs: Prefs) {

   var pageName: String = "Instagram Page"
   var period: String = "Last 30 Days"
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Header
        item {
            AnalyticsPostHeader(pageName, period)
        }

        // KPI Cards
        item {
            Spacer(modifier = Modifier.height(16.dp))
            AnalyticsOverviewGrid()
        }

        // Chart
        item {
            Spacer(modifier = Modifier.height(24.dp))
            AnalyticsChartSection()
        }

        // Posts List
        item {
            Spacer(modifier = Modifier.height(24.dp))
            AnalyticsPostsList()
        }
    }
}

@Composable
fun AnalyticsPostsList() {
    Column {
        Text(
            text = "Posts",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        repeat(5) {
            AnalyticsPostItem()
        }
    }
}

@Composable
fun AnalyticsPostHeader(pageName: String, period: String) {
    Column {
        Text(
            text = pageName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = period,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
@Composable
fun AnalyticsOverviewGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(220.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { AnalyticsKpiCard("Clicks", "1.2K", "+12%") }
        item { AnalyticsKpiCard("Likes", "3.4K", "+8%") }
        item { AnalyticsKpiCard("Followers", "980", "+45") }
        item { AnalyticsKpiCard("Content Viewed", "12.6K", "+18%") }
    }
}

@Composable
fun AnalyticsKpiCard(
    title: String,
    value: String,
    change: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 14.sp, color = Color.Gray)
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(
                text = change,
                fontSize = 12.sp,
                color = Color(0xFF2E7D32)
            )
        }
    }
}
@Composable
fun AnalyticsChartSection() {
    Column {
        Text(
            text = "Performance",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Chart View")
        }
    }
}
@Composable
fun AnalyticsPostItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Thumbnail
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Post caption or title",
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    AnalyticsMetric("❤️", "520")
                    Spacer(modifier = Modifier.width(12.dp))
                    AnalyticsMetric("👁", "2.3K")
                    Spacer(modifier = Modifier.width(12.dp))
                    AnalyticsMetric("🔗", "120")
                }
            }
        }
    }
}
@Composable
fun AnalyticsMetric(icon: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon)
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, fontSize = 12.sp)
    }
}
