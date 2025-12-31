package com.wingspan.aimediahub.ui.theme.nestedcompose

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wingspan.aimediahub.R
import com.wingspan.aimediahub.models.Post
import com.wingspan.aimediahub.models.SocialAccount1
import com.wingspan.aimediahub.ui.theme.GradientDateTimePickerBox
import com.wingspan.aimediahub.ui.theme.LightSkyBlue
import com.wingspan.aimediahub.ui.theme.SocialChip
import com.wingspan.aimediahub.ui.theme.SoftLavender
import com.wingspan.aimediahub.ui.theme.bottonpages.hourGradient
import com.wingspan.aimediahub.ui.theme.formatHour
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalenderViewData() {
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyCalendarGrid(
    weekStart: LocalDate,
    postsPerDay: Map<LocalDate, Map<Int, List<Post>>>,
    onPostClick: (LocalDate, Int, String) -> Unit = { _, _, _ -> },
    onPostLongPress: (LocalDate, Int, List<Post>) -> Unit
) {
    val daysOfWeek = remember(weekStart) {
        (0..6).map { weekStart.plusDays(it.toLong()) }
    }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Row(modifier = Modifier.fillMaxSize()) {

        /* ================= LEFT HOURS COLUMN ================= */
        Column(
            modifier = Modifier.width(70.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Column(modifier = Modifier.verticalScroll(verticalScrollState)) {
                for (hour in 0..23) {
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = formatHour(hour), // ✅ AM / PM
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }
            }
        }

        /* ================= DAYS GRID ================= */
        Row(
            modifier = Modifier.horizontalScroll(horizontalScrollState)
        ) {
            daysOfWeek.forEach { date ->

                Column(
                    modifier = Modifier
                        .width(60.dp)
                        .border(0.5.dp, Color.LightGray)
                ) {

                    /* 🔹 HEADER */
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

                    /* 🔹 SCROLLABLE HOURS */
                    Column(
                        modifier = Modifier.verticalScroll(verticalScrollState)
                    ) {
                        for (hour in 0..23) {
                            val hourPosts =
                                postsPerDay[date]?.get(hour).orEmpty()

                            Box(
                                modifier = Modifier
                                    .height(40.dp)
                                    .fillMaxWidth()
                                    .border(0.5.dp, Color.LightGray)
                            ) {
                                if (hourPosts.isNotEmpty()) {
                                    Card(
                                        colors = CardDefaults.cardColors(),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxWidth()
                                            .combinedClickable(
                                                onClick = {
                                                    onPostClick(
                                                        date,
                                                        hour,
                                                        hourPosts.first().message
                                                    )
                                                },
                                                onLongClick = {
                                                    onPostLongPress(
                                                        date,
                                                        hour,
                                                        hourPosts
                                                    )
                                                }
                                            )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(postCardGradient) // ✅ GRADIENT HERE
                                        ) {
                                            Text(
                                                text = "${hourPosts.size} posts",
                                                fontSize = 10.sp,
                                                color = Color.Black, // better contrast
                                                modifier = Modifier.padding(3.dp)
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
    }
}
val postCardGradient = Brush.horizontalGradient(
    listOf(LightSkyBlue, SoftLavender)
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateAndPlatformRow(
    currentPages: MutableList<SocialAccount1>,
    onPagesChange: (MutableList<SocialAccount1>) -> Unit,
    twitterPage: SocialAccount1?,
    linkedInPage: SocialAccount1?,
    instagramPage: SocialAccount1?,
    context: Context,
    onRemoveTwitter: () -> Unit,
    onRemoveInstagram: () -> Unit,

    ) {

    var selectedDateTime by remember {
        mutableStateOf(LocalDateTime.now())
    }

    val formatter =
        DateTimeFormatter.ofPattern("MMM d, yyyy  h:mm a", Locale.ENGLISH)

    val formattedDate = selectedDateTime.format(formatter)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
    ) {



        // ---------------------- DATE & TIME PICKER ----------------------
        GradientDateTimePickerBox(
            formattedDate = formattedDate,
            selectedDateTime = selectedDateTime,

            modifier = Modifier.fillMaxWidth() // optional, set width as needed
        )



        Spacer(Modifier.height(12.dp))

        // ---------------------- PLATFORMS ----------------------
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            // FACEBOOK PAGES
            items(currentPages.size) { index ->
                val page = currentPages[index]
                SocialChip(
                    icon = R.drawable.ic_fb,
                    iconTint = Color(0xFF1877F2),
                    imageUrl = page.imageUrl,
                    label = page.name.toString(),
                    onRemove = {
                        val updated =
                            currentPages.filter { it.id != page.id }.toMutableList()
                        onPagesChange(updated)
                    }
                )
            }

            // TWITTER
            twitterPage?.let { tp ->
                item {
                    SocialChip(
                        icon = R.drawable.ic_twitter,
                        iconTint = Color(0xFF1DA1F2),
                        imageUrl = tp.imageUrl,
                        label = tp.name.toString(),
                        onRemove = onRemoveTwitter
                    )
                }
            }
            linkedInPage?.let {tp->
                item {
                    SocialChip(
                        icon = R.drawable.ic_linkedin,
                        iconTint = Color(0xFF1DA1F2),
                        imageUrl = tp.imageUrl,
                        label = tp.name.toString(),
                        onRemove = onRemoveTwitter
                    )
                }
            }

            // INSTAGRAM
            instagramPage?.let { ip ->
                item {
                    SocialChip(
                        icon = R.drawable.ic_instagram,
                        iconTint = Color(0xFFDD2A7B),
                        imageUrl = ip.imageUrl,
                        label = ip.name.toString(),
                        onRemove = onRemoveInstagram
                    )
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyCalendarGrid(
    selectedDay: LocalDate,
    postsPerHour: Map<Int, List<Post>> = emptyMap(),
    onPostClick: (LocalDate, Int, List<Post>) -> Unit = { _, _, _ -> },
    onPostLongPress: (LocalDate, Int, List<Post>) -> Unit = { _, _, _ -> }
) {
    val verticalScrollState = rememberScrollState()

    Row(modifier = Modifier.fillMaxSize()) {

        /* ================= LEFT TIME COLUMN ================= */
        Column(
            modifier = Modifier.width(70.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp)) // header space

            Column(modifier = Modifier.verticalScroll(verticalScrollState)) {
                for (hour in 0..23) {
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = formatHour(hour),   // ✅ AM/PM format
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }
            }
        }

        /* ================= RIGHT DAY GRID ================= */
        Column(
            modifier = Modifier
                .weight(1f)
                .border(0.5.dp, Color.LightGray)
        ) {

            /* 🔹 FIXED HEADER */
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

            /* 🔹 SCROLLABLE HOURS GRID */
            Column(
                modifier = Modifier.verticalScroll(verticalScrollState)
            ) {
                for (hour in 0..23) {
                    val hourPosts = postsPerHour[hour].orEmpty()

                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                            .background(hourGradient(hour))
                            .border(0.5.dp, Color.LightGray)
                    ) {
                        if (hourPosts.isNotEmpty()) {
                            Card(

                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            onPostClick(selectedDay, hour, hourPosts)
                                        },
                                        onLongClick = {
                                            onPostLongPress(selectedDay, hour, hourPosts)
                                        }
                                    )
                            ) {
                                Text(
                                    text = "${hourPosts.size} posts",
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


