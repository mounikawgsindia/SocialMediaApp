package com.wingspan.aimediahub.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.wingspan.aimediahub.R
import com.wingspan.aimediahub.models.Post
import com.wingspan.aimediahub.models.SocialAccount1
import com.wingspan.aimediahub.utils.Prefs
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewPostPageScreen(
    selectedDate: LocalDate,
    posts: List<Post>,
    prefs: Prefs,
    rootNavController: NavHostController
) {

    val pages = remember { prefs.getFacebookPages() }
    val pageMap = remember { pages.associateBy { it.id } }
    val isNavigatingBack = remember { androidx.compose.runtime.mutableStateOf(false) }


    // 🔹 Group posts by pageId
    val groupedPosts = remember(posts) {
        posts.groupBy { it.pageId }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars
                .union(WindowInsets.navigationBars)
                .asPaddingValues()),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    color = Color(0xFFF2F2F2),   // light grey background
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    isNavigatingBack.value = true
                    rootNavController.navigateUp()
                }
                .padding(10.dp)                // inner padding
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_icon),
                contentDescription = "Back",
                tint = Color.Black, modifier = Modifier.size(24.dp).padding(3.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {

            // 📅 Date Header
            Text(
                text = selectedDate.format(
                    DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")
                ),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No posts scheduled for this date",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn {
                    groupedPosts.forEach { (pageId, pagePosts) ->

                        val page = pageMap[pageId]

                        // 🔹 Page Header (shown once)
                        item {
                            PageHeader(page = page)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // 🔹 Posts under that page
                        items(pagePosts.size) { index ->
                            var data=pagePosts[index]
                            PostItem(data)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostItem(post: Post) {

    val zonedDateTime = remember(post.createdAt) {
        Instant.parse(post.createdAt)
            .atZone(ZoneId.systemDefault())
    }

    val dateTimeText = remember(post.createdAt) {
        zonedDateTime.format(
            DateTimeFormatter.ofPattern("dd MMM yyyy • hh:mm a")
        )
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {

        Column {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dateTimeText,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                StatusChip(post.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = post.message,
                fontSize = 15.sp,
                maxLines = 3
            )

            if (!post.mediaUrl.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = post.mediaUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

// ---------------- CARD ----------------
@Composable
fun PageHeader(page: SocialAccount1?) {

    val brandColor = when (page?.platform?.lowercase()) {
        "facebook" -> Color(0xFF1877F2)
        "instagram" -> Color(0xFFE1306C)
        "twitter" -> Color(0xFF1DA1F2)
        else -> Color.Gray
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7F7F7), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {

        AsyncImage(
            model = page?.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(50)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = page?.name ?: "Unknown Page",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = brandColor
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostCard(
    post: Post,
    page: SocialAccount1?
) {

    val brandColor = when (page?.platform?.lowercase()) {
        "facebook" -> Color(0xFF1877F2)
        "instagram" -> Color(0xFFE1306C)
        "twitter" -> Color(0xFF1DA1F2)
        else -> Color.Gray
    }

    val time = remember(post.createdAt) {
        Instant.parse(post.createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
            .format(DateTimeFormatter.ofPattern("hh:mm a"))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(12.dp))
    ) {

        // Brand indicator
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(brandColor)
        )

        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {

            // 🔝 Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    AsyncImage(
                        model = page?.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(50)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = page?.name ?: "Unknown Page",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = brandColor
                    )
                }

                StatusChip(post.status)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = time,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.message,
                fontSize = 15.sp,
                maxLines = 3
            )

            // Small thumbnail only
            if (!post.mediaUrl.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = post.mediaUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

// ---------------- STATUS CHIP ----------------

@Composable
fun StatusChip(status: String) {
    val bgColor = when (status.lowercase()) {
        "posted" -> Color(0xFFE8F5E9)
        "scheduled" -> Color(0xFFFFF8E1)
        else -> Color(0xFFF0F0F0)
    }

    val textColor = when (status.lowercase()) {
        "posted" -> Color(0xFF2E7D32)
        "scheduled" -> Color(0xFFF9A825)
        else -> Color.DarkGray
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

// ---------------- PAGE STYLE ----------------

fun pageStyle(pageId: String): Pair<Color, String> {
    return when (pageId) {
        "943802612142922" -> Color(0xFF1877F2) to "Facebook"
        "INSTAGRAM_ID" -> Color(0xFFE1306C) to "Instagram"
        "TWITTER_ID" -> Color(0xFF1DA1F2) to "Twitter"
        else -> Color.Gray to "Other"
    }
}