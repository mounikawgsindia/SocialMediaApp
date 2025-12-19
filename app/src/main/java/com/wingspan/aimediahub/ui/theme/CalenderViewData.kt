package com.wingspan.aimediahub.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CalenderViewData() {
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyCalendarGrid(
    weekStart: LocalDate,
    postsPerDay: Map<LocalDate, List<Pair<Int, String>>> = emptyMap(),
    onPostClick: (LocalDate, Int, String) -> Unit = { _, _, _ -> }
) {
    val daysOfWeek = remember(weekStart) {
        (0..6).map { weekStart.plusDays(it.toLong()) }
    }

    // 🔑 KEYED scroll states (IMPORTANT)
    val verticalScrollState = remember(weekStart) { ScrollState(0) }
    val horizontalScrollState = remember { ScrollState(0) }

    Row(modifier = Modifier.fillMaxSize()) {

        /* HOURS COLUMN */
        Column(
            modifier = Modifier
                .width(50.dp)
                .verticalScroll(verticalScrollState)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            for (hour in 0..23) {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "$hour:00",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        /* DAYS GRID */
        Row(
            modifier = Modifier.horizontalScroll(horizontalScrollState)
        ) {
            daysOfWeek.forEach { date ->

                Column(
                    modifier = Modifier
                        .width(80.dp)
                        .verticalScroll(verticalScrollState)
                        .border(0.5.dp, Color.LightGray)
                ) {

                    // HEADER
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("E d")),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // HOURS
                    for (hour in 0..23) {
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth()
                                .border(0.5.dp, Color.LightGray)
                        ) {
                            postsPerDay[date]
                                ?.firstOrNull { it.first == hour }
                                ?.let { post ->

                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF00C2A8)
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxWidth()
                                            .clickable {
                                                onPostClick(date, hour, post.second)
                                            }
                                    ) {
                                        Text(
                                            text = post.second,
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                }
                        }
                    }
                }
            }
        }
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyCalendarGrid(
    selectedDay: LocalDate,
    postsPerHour: Map<Int, String> = emptyMap(),
    onPostClick: (LocalDate, Int, String) -> Unit = { _, _, _ -> }
) {
    // 🔑 Reset scroll when day changes
    val verticalScrollState = remember(selectedDay) { ScrollState(0) }

    Row(modifier = Modifier.fillMaxSize()) {

        /* HOURS COLUMN */
        Column(
            modifier = Modifier
                .width(50.dp)
                .verticalScroll(verticalScrollState)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            for (hour in 0..23) {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "$hour:00",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        /* DAY COLUMN */
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(verticalScrollState)
                .border(0.5.dp, Color.LightGray)
        ) {

            // DAY HEADER
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedDay.format(
                        DateTimeFormatter.ofPattern("EEEE, d MMM")
                    ),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // HOURS GRID
            for (hour in 0..23) {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .border(0.5.dp, Color.LightGray)
                ) {

                    postsPerHour[hour]?.let { title ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF00C2A8)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .clickable {
                                    onPostClick(selectedDay, hour, title)
                                }
                        ) {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}