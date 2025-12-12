package com.wingspan.aimediahub.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import java.time.LocalDateTime
import java.util.Locale
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.wingspan.aimediahub.models.ScheduledPost
import com.wingspan.aimediahub.R
import com.wingspan.aimediahub.models.SocialAccount1
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.viewmodel.FacebookViewModel
import java.time.format.DateTimeFormatter

@SuppressLint("MutableCollectionMutableState")
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreatePostScreen(navController: NavHostController,viewModel: FacebookViewModel= hiltViewModel()) {

    var showSheet by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var postText by remember { mutableStateOf("") }
    var context= LocalContext.current
    val prefs = remember { Prefs(context) }
    var currentfbPages by remember { mutableStateOf(prefs.getFacebookPages().toMutableList()) }
    var twitterProfile by remember {mutableStateOf(prefs.getTwitterAccount())}
    var instaProfile by remember {mutableStateOf(prefs.getInstaAccount())}

    val postfbId by viewModel.fbpostStatus.collectAsState()
    val postTwitterId by viewModel.twitterpostStatus.collectAsState()

    LaunchedEffect(postfbId) {
        if (!postfbId.isNullOrEmpty()) {
            Toast.makeText(
                context,
                "Facebook Post successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
        postText=""
    }
    LaunchedEffect(postTwitterId) {
        if (!postTwitterId.isNullOrEmpty()) {
            Toast.makeText(
                context,
                "Twitter Post successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
        postText=""
    }

    // ✅ Image Picker Launcher
    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }
    // ✅ Permission Launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                imagePickerLauncher.launch("image/*")
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()         // ✅ prevents status/navigation overlap
            .background(Color.White)
    ) {

        // ✅ TOP BAR
        NewPostTopBar(
            onClose = { navController.popBackStack() },
            onContinue = {
                val message = postText
                if(!message.isEmpty()){
                    if (currentfbPages.isNotEmpty()) {
                        currentfbPages.forEach { page ->

                            viewModel.postToFacebook(
                                pageId = page.id,
                                pageToken = page.accessToken,
                                message = message
                            )

                        }

                    }
                    if(twitterProfile!=null){
                        var accessToken="1998625018910326784-B4LG6Zus5ZZp5VcdQJxMmnWGkldIWb"
                        viewModel.postTweet(accessToken,"5f05HDr3SA95rnegFQjqwCJeJAafq4bcW4G2zT6nyvyUy",message)
                    }
                    if(instaProfile!=null){
                        val longlivedToken = "IGAATWB2zHKD1BZAGJlYUp2akY1TG1fa3pUeU80MUJHbC1XNFY5aWRabWdEaV9RRDZATbDRkeDNEa0NET19JR0dkSkNReTZARYjhVMnhqU1NkYVB2VUNGZAjZAXUHFEcEs4WXVhcjRqTkJJYXJGQTlvb2Y1dlBtbnpIdjdWbnRMY1RXawZDZD"
                        viewModel.fetchUserMedia(longlivedToken)
                    }
                    if(!currentfbPages.isNotEmpty() && twitterProfile==null && instaProfile==null) {
                        Toast.makeText(context, "No profiles connected", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "No Message to continue", Toast.LENGTH_SHORT).show()
                }

            }
        )

        // ✅ DATE & PLATFORM ROW
        DateAndPlatformRow(
            currentPages = currentfbPages,twitterPage=twitterProfile,instagramPage=instaProfile,
            onPagesChange = { updatedList ->
                currentfbPages = updatedList
            }, context = context,
            onRemoveTwitter = {
                twitterProfile = null
            },onRemoveInstagram={
                instaProfile=null
            }
        )

        Divider(color = Color(0xFFE0E0E0))

        // ✅ CONTENT FORM
        PostContentSection(   text = postText,
            onTextChange = { postText = it },  onAddMediaClick = { showSheet = true })
        // ✅ BOTTOM SHEET
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                containerColor = Color.White
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    BottomSheetItem(
                        icon = Icons.Default.Photo,
                        title = "Add Images"
                    ) {
                        showSheet = false
                        val permission =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                android.Manifest.permission.READ_MEDIA_IMAGES
                            else
                                android.Manifest.permission.READ_EXTERNAL_STORAGE

                        if (ContextCompat.checkSelfPermission(
                                context,
                                permission
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            imagePickerLauncher.launch("image/*")
                        } else {
                            permissionLauncher.launch(permission)
                        }
                    }

                    BottomSheetItem(
                        icon = Icons.Default.Videocam,
                        title = "Add Video"
                    ) {
                        showSheet = false
                        // TODO: Open video picker
                    }

                    BottomSheetItem(
                        icon = Icons.Default.CameraAlt,
                        title = "Open Camera"
                    ) {
                        showSheet = false
                        // TODO: Open camera
                    }
                }
            }
        }


    }
}


@Composable
fun BottomSheetItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF1976D2))
        Spacer(Modifier.width(12.dp))
        Text(title, fontSize = 16.sp)
    }
}

@Composable
fun NewPostTopBar(
    onClose: () -> Unit,
    onContinue: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = "Close",
            tint = Color.Black,
            modifier = Modifier
                .size(22.dp)
                .clickable { onClose() }
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = "New post",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "CONTINUE",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E88E5),
            modifier = Modifier.clickable { onContinue() }
        )
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateAndPlatformRow(
    currentPages: MutableList<SocialAccount1>,
    onPagesChange: (MutableList<SocialAccount1>) -> Unit,
    twitterPage: SocialAccount1?,instagramPage :SocialAccount1?,
    context: Context, onRemoveTwitter: () -> Unit,onRemoveInstagram:()-> Unit

) {

    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy  h:mm a", Locale.ENGLISH)
    val formattedDate = currentDateTime.format(formatter)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
    ) {

        // ---------------------- DATE ----------------------
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(text = formattedDate, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            // ---------- FACEBOOK PAGES ----------
            items(currentPages.size) { index ->
                var page =currentPages[index]
                SocialChip(
                    icon = R.drawable.ic_fb,
                    iconTint = Color(0xFF1877F2),
                    imageUrl = page.imageUrl,
                    label = page.name,
                    onRemove = {
                        val updated = currentPages.filter { it.id != page.id }.toMutableList()
                        onPagesChange(updated)
                    }
                )
            }

            // ---------- TWITTER ----------
            twitterPage?.let { tp ->
                item {
                    SocialChip(
                        icon = R.drawable.ic_twitter,
                        iconTint = Color(0xFF1DA1F2),
                        imageUrl = tp.imageUrl,
                        label = tp.name,
                        onRemove = {
                            onRemoveTwitter()
                        }
                    )
                }
            }

            // ---------- INSTAGRAM ----------
            instagramPage?.let { ip ->
                item {
                    SocialChip(
                        icon = R.drawable.ic_instagram,
                        iconTint = Color(0xFFDD2A7B), // IG color
                        imageUrl = ip.imageUrl,
                        label = ip.name,
                        onRemove = {
                            onRemoveInstagram()
                        }
                    )
                }
            }
        }

    }
}


@Composable
fun SocialChip(
    @DrawableRes icon: Int,
    iconTint: Color,
    imageUrl: String?,
    label: String,
    onRemove: () -> Unit
) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE7F0FF))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            // Platform Icon
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(36.dp)
            )

            Spacer(Modifier.width(10.dp))

            // Profile Image + Close Button
            Box(modifier = Modifier.size(34.dp)) {

                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = label,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }

                // ----------- BEAUTIFUL MODERN CROSS BUTTON -----------
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .background(Color.Black, CircleShape)
                        .shadow(4.dp, CircleShape)
                        .clickable { onRemove() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}



@Composable
fun PostContentSection(    text: String,
                           onTextChange: (String) -> Unit, onAddMediaClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = "Post content",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(Modifier.weight(1f))

            Icon(
                painter = painterResource(R.drawable.ic_arrow_up),
                contentDescription = "Collapse",
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Content Type Row
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                painter = painterResource(R.drawable.ic_fb),
                contentDescription = "",
                modifier = Modifier.size(22.dp)
            )

            Spacer(Modifier.width(8.dp))

            Text("Content type", fontSize = 14.sp)

            Spacer(Modifier.weight(1f))

            Icon(
                painter = painterResource(R.drawable.ic_grid),
                contentDescription = "",
                modifier = Modifier.size(18.dp)
            )

            Spacer(Modifier.width(6.dp))

            Text("Post", color = Color.Gray)
        }

        Spacer(Modifier.height(18.dp))

        // Text Area
        BasicTextField(
            value = text,
            onValueChange = { onTextChange(it) },
            modifier = Modifier.fillMaxWidth()
                .height(200.dp)
                .padding(12.dp),
            decorationBox = { inner ->
                Box {
                    if (text.isEmpty()) {
                        Text("Write here...", color = Color.Gray)
                    }
                    inner()
                }
            }
        )
        // Bottom Counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(Modifier.weight(1f))

            Text("${text.length} / 16192", color = Color.Gray)

            Spacer(Modifier.width(6.dp))

            Icon(
                painter = painterResource(R.drawable.ic_fb),
                contentDescription = "",
                modifier = Modifier.size(18.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {onAddMediaClick() }
        ) {
            Icon(
                imageVector = Icons.Default.Image, // or your custom icon
                contentDescription = "Add",
                tint = Color(0xFF1976D2), // Blue color
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Add Multimedia",
                color = Color(0xFF1976D2),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }


    }
}



// ---------- Sample Content ----------
fun samplePosts(): List<ScheduledPost> = listOf(
    ScheduledPost(1, "9:15 AM", "Facebook", "Good morning everyone! Here's today's update and a quick tip to start your day.", Accent1),
    ScheduledPost(2, "10:00 AM", "Instagram", "New carousel: 5 ways to improve productivity at work. Swipe to see!", Color(0xFF42A5F5)),
    ScheduledPost(3, "1:30 PM", "LinkedIn", "Hiring update: We're looking for an Android developer experienced in Jetpack Compose.", Color(0xFFAB47BC)),
    ScheduledPost(4, "5:00 PM", "Twitter", "Short tip: Use WorkManager for scheduled background posts.", Color(0xFF26A69A))
)



