package com.wingspan.aimediahub.ui.theme.bottonpages

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wingspan.aimediahub.ui.theme.SoftLavender
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wingspan.aimediahub.models.Post
import com.wingspan.aimediahub.ui.theme.DailyCalendarGrid
import com.wingspan.aimediahub.ui.theme.WeeklyCalendarGrid
import com.wingspan.aimediahub.utils.NetworkUtils
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.viewmodel.FacebookViewModel
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    bottomNavController: NavHostController,
    rootNavController: NavHostController,
    prefs: Prefs,viewmodel: FacebookViewModel= hiltViewModel()
) {

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    var context= LocalContext.current
    val postData by viewmodel.fbgetPostStatus.collectAsState()
    var selectedPosts by remember { mutableStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedDayPosts by remember { mutableStateOf<List<Post>>(emptyList()) }

    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }
    var showViewModeSheet by remember { mutableStateOf(false) }
    var weekStart by remember {
        mutableStateOf(LocalDate.now().with(DayOfWeek.MONDAY))
    }
    // This is your list stored for calendar usage
    val calendarPostList = remember(postData) {
        postData
    }

    //count date
    val postCountByDate = remember(postData) {
        postData
            ?.groupBy {
                Instant.parse(it.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
            ?.mapValues { it.value.size }
            ?: emptyMap()
    }


    Log.d("calender list","--->${calendarPostList}")
    val daysOfWeek = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

    val sampleDailyPosts = mapOf(
        9 to "Morning Post",
        12 to "Lunch Campaign",
        15 to "Product Reel",
        18 to "Evening Story"
    )

    LaunchedEffect(Unit) {
        if(NetworkUtils.isNetworkAvailable(context =context )){
            viewmodel.getPostedApi()
        }else{
            Toast.makeText(context,"Check your internet connection", Toast.LENGTH_SHORT).show()
        }

    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    rootNavController.navigate("create_post_screen")
                },
                containerColor = SoftLavender
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            CalendarTopBar()
            // ✅ Replace CalendarTopBar with MonthSelector

            MemberRow()

            MonthSelector(
                currentMonth = currentMonth,
                weekStart = weekStart,
                selectedDay = selectedDay ?: LocalDate.now(),
                viewMode = viewMode,
                onMonthChange = { currentMonth = it },
                onWeekChange = { weekStart = it },
                onDayChange = { selectedDay = it },
                onViewModeClick = { showViewModeSheet = true }
            )





            Spacer(modifier = Modifier.height(12.dp))
            when (viewMode) {
                CalendarViewMode.MONTH -> {

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        daysOfWeek.forEach {
                            Text(
                                text = it,
                                fontWeight = FontWeight.Medium,
                                color = if (it == "Sa" || it == "Su") Color.Gray else Color(0xFF00C2A8)
                            )
                        }
                    }
                    CalendarGrid(currentMonth, postCountByDate = postCountByDate,onDayLongPress = { day, posts ->

                        selectedDay = LocalDate.of(currentMonth.year, currentMonth.month, day)
                        selectedPosts = posts
                        selectedDayPosts = postData
                            ?.filter {
                                Instant.parse(it.createdAt)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate() == selectedDay
                            } ?: emptyList()
                        showBottomSheet = true
                    })
                }

                CalendarViewMode.WEEK -> {
                    val samplePosts = mapOf(
                        LocalDate.of(2025, 12, 15) to listOf(9 to "Meeting", 14 to "Call"),
                        LocalDate.of(2025, 12, 16) to listOf(11 to "Design Review")
                    )

                    WeeklyCalendarGrid(
                        weekStart = weekStart,
                        postsPerDay = samplePosts,
                        onPostClick = { date, hour, title -> }
                    )

                }

                CalendarViewMode.DAY -> {
                    selectedDay?.let { day ->
                        DailyCalendarGrid(
                            selectedDay = day,
                            postsPerHour = sampleDailyPosts,
                            onPostClick = { date, hour, title ->
                                println("Clicked $title on $date at $hour")
                            }
                        )
                    }
                }

                CalendarViewMode.LIST -> {
                    CalendarListView()
                }
            }

        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            CalendarBottomSheet(
                posts = selectedPosts,
                onViewPosts = {
                    showBottomSheet = false
                    rootNavController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("day_posts", selectedDayPosts)
                    rootNavController.navigate("view_posts/$selectedDay")
                },
                onCreatePost = {
                    showBottomSheet = false
                    rootNavController.navigate("create_post_screen")
                }
            )
        }
    }

    if (showViewModeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showViewModeSheet = false }
        ) {
            CalendarViewModeSheet(
                onSelect = { mode ->
                    viewMode = mode
                    showViewModeSheet = false
                }
            )
        }
    }


}






@Composable
fun CalendarListView() {
    Text(
        text = "List View",
        modifier = Modifier.padding(16.dp),
        fontWeight = FontWeight.Bold
    )
}


@Composable
fun CalendarViewModeSheet(
    onSelect: (CalendarViewMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        SheetItem("Monthly") { onSelect(CalendarViewMode.MONTH) }
        SheetItem("Weekly") { onSelect(CalendarViewMode.WEEK) }
        SheetItem("Daily") { onSelect(CalendarViewMode.DAY) }
        SheetItem("List") { onSelect(CalendarViewMode.LIST) }
    }
}

@Composable
fun CalendarBottomSheet(
    posts: Int,
    onViewPosts: () -> Unit,
    onCreatePost: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {

        if (posts > 0) {
            SheetItem("View Posts", onViewPosts)
            Spacer(modifier = Modifier.height(16.dp))
        }

        SheetItem("Create Post", onCreatePost)
    }
}
@Composable
fun SheetItem(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    )
}

@Composable
fun CalendarTopBar() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Calendar",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Row {
            Icon(Icons.Default.NotificationsNone, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Icon(Icons.Default.FilterList, contentDescription = null)
        }
    }
}
@Composable
fun MemberRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MemberAvatar("Mounika")
        Spacer(modifier = Modifier.width(12.dp))
        MemberAvatar("Nirvan")
    }
}

@Composable
fun MemberAvatar(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAEAEA)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = name.first().toString(), fontWeight = FontWeight.Bold)
        }
        Text(text = name, fontSize = 12.sp)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthSelector(
    currentMonth: YearMonth,
    weekStart: LocalDate,
    selectedDay: LocalDate?,
    viewMode: CalendarViewMode,
    onMonthChange: (YearMonth) -> Unit,
    onWeekChange: (LocalDate) -> Unit,
    onDayChange: (LocalDate) -> Unit,
    onViewModeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous arrow
        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = "Previous",
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    when (viewMode) {
                        CalendarViewMode.MONTH -> onMonthChange(currentMonth.minusMonths(1))
                        CalendarViewMode.WEEK -> {

                            val newWeekStart = weekStart.minusWeeks(1).toWeekStart()
                            onWeekChange(newWeekStart)
                            onDayChange(newWeekStart) // optional but recommended
                        }

                        CalendarViewMode.DAY -> selectedDay?.let { onDayChange(it.minusDays(1)) }
                        else -> onMonthChange(currentMonth.minusMonths(1))
                    }
                }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Center text
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val displayText = when (viewMode) {
                CalendarViewMode.MONTH -> currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                CalendarViewMode.WEEK -> {
                    val weekEnd = weekStart.plusDays(6)
                    val formatter = DateTimeFormatter.ofPattern("MMM d")
                    "${weekStart.format(formatter)} - ${weekEnd.format(formatter)}"
                }
                CalendarViewMode.DAY -> selectedDay?.format(DateTimeFormatter.ofPattern("d MMM, yyyy")) ?: ""
                CalendarViewMode.LIST -> "Scheduled posts"
            }

            Text(
                text = displayText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // View mode button
        Icon(
            imageVector = Icons.Default.ViewAgenda,
            contentDescription = "Change View",
            modifier = Modifier
                .size(28.dp)
                .clickable { onViewModeClick() }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Next arrow
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Next",
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    when (viewMode) {
                        CalendarViewMode.MONTH -> onMonthChange(currentMonth.plusMonths(1))
                        CalendarViewMode.WEEK -> {
                            val newWeekStart = weekStart.plusWeeks(1).toWeekStart()
                            onWeekChange(newWeekStart)
                            onDayChange(newWeekStart) // optional
                        }

                        CalendarViewMode.DAY -> selectedDay?.let { onDayChange(it.plusDays(1)) }
                        else -> onMonthChange(currentMonth.plusMonths(1))
                    }
                }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.toWeekStart(): LocalDate =
    this.with(DayOfWeek.MONDAY)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarGrid(currentMonth: YearMonth,
                 postCountByDate: Map<LocalDate, Int>,onDayLongPress: (Int, Int) -> Unit) {

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value // 1 = Monday

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(42) { index ->
            val dayNumber = index - (firstDayOfWeek - 1) + 1

            val posts = if (dayNumber in 1..daysInMonth) {
                val date = LocalDate.of(
                    currentMonth.year,
                    currentMonth.month,
                    dayNumber
                )
                postCountByDate[date] ?: 0
            } else 0

            CalendarDay(
                day = if (dayNumber in 1..daysInMonth) dayNumber else null,
                posts = posts,
                onLongPress = {
                    if (dayNumber in 1..daysInMonth) {
                        onDayLongPress(dayNumber, posts)
                    }
                }
            )
        }
    }
}

@Composable
fun CalendarDay(day: Int?, posts: Int,onLongPress: () -> Unit) {
    Box(
        modifier = Modifier
            .height(100.dp) // <-- fixed height for each cell
            .padding(1.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(
                width = 0.5.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(0.dp) // square grid look
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() }
                )
            },
        contentAlignment = Alignment.TopCenter
    ) {
        if (day != null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = day.toString(),
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                if (posts > 0) {
                    Text(
                        text = "$posts posts",
                        color = Color(0xFF00C2A8),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}



enum class CalendarViewMode { MONTH, WEEK, DAY, LIST }