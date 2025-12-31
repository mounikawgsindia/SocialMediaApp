package com.wingspan.aimediahub.ui.theme

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import java.time.LocalDateTime
import java.util.Locale
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.wingspan.aimediahub.ui.theme.nestedcompose.DateAndPlatformRow
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.viewmodel.BlueskyViewModel
import com.wingspan.aimediahub.viewmodel.FacebookViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient


import okhttp3.Response


import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SuppressLint("MutableCollectionMutableState")
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreatePostScreen(navController: NavHostController,prefs:Prefs,viewModel: FacebookViewModel= hiltViewModel(),
                     blueskyViewmodel: BlueskyViewModel=hiltViewModel()) {

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle


//    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
//    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    val selectedTimes = remember { mutableStateListOf<LocalTime>() }



    var showSheet by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }

    var context= LocalContext.current

    var currentfbPages by remember { mutableStateOf(prefs.getFacebookPages().toMutableList()) }
    var twitterProfile by remember {mutableStateOf(prefs.getTwitterAccount())}
    var linkedInProfile by remember {mutableStateOf(prefs.getLinkedInAccount())}
    var instaProfile by remember {mutableStateOf(prefs.getInstaAccount())}

    val postfbId by viewModel.fbpostStatus.collectAsState()
    val postTwitterId by viewModel.twitterpostStatus.collectAsState()
    var aiDialog by remember{mutableStateOf(false)}

    val aiText = savedStateHandle?.getStateFlow("ai_text", "")?.collectAsState(initial = "")?.value.orEmpty()

    var postText by remember { mutableStateOf("") }

    LaunchedEffect(aiText) {
        if (postText.isBlank() && aiText.isNotBlank()) {
            postText = aiText
        }
    }

    LaunchedEffect(postfbId) {
        if (postfbId == true) {
            Toast.makeText(
                context,
                "Facebook Post successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
        postText=""
        selectedImageUri=null
        viewModel.clearFbPostStatus()
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

    val videoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedVideoUri = uri
        }

    // ✅ Permission Launcher
    val mediaPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val imageGranted =
                permissions[android.Manifest.permission.READ_MEDIA_IMAGES] == true ||
                        permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] == true

            val videoGranted =
                permissions[android.Manifest.permission.READ_MEDIA_VIDEO] == true ||
                        permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] == true

            when {
                imageGranted -> imagePickerLauncher.launch("image/*")
                videoGranted -> videoPickerLauncher.launch("video/*")
                else -> Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
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
                blueskyViewmodel.createPostWithImage(context,message, selectedImageUri)
                if(!message.isEmpty()){
                    if (currentfbPages.isNotEmpty()) {
                        val uri = selectedImageUri
                        if (uri != null) {
                            val imageFile = convertUriToFile(context, uri)
                            sendTelegramPhoto(imageFile)
                        } else {
                            Log.e("Telegram", "Selected image Uri is null")
                        }

                        viewModel.publishFacebookPost(
                            pageIds = currentfbPages.map { it.id.toString() },
                            message = postText,
                            times = selectedTimes.map {
                                it.format(DateTimeFormatter.ofPattern("HH:mm"))
                            },
                            startDate = startDate.toString(),
                            endDate = endDate.toString(),
                            imageUri = selectedImageUri
                        )



                    }
                    if(twitterProfile!=null){

                    }
                    if(instaProfile!=null){

                    }
                    if(linkedInProfile!=null){

                    }
                    if(!currentfbPages.isNotEmpty() && twitterProfile==null && instaProfile==null && linkedInProfile==null) {
                        Toast.makeText(context, "No profiles connected", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "No Message to continue", Toast.LENGTH_SHORT).show()
                }

            }
        )

        // ✅ DATE & PLATFORM ROW
        DateAndPlatformRow(
            currentPages = currentfbPages,twitterPage=twitterProfile, linkedInPage = linkedInProfile, instagramPage=instaProfile,
            onPagesChange = { updatedList ->
                currentfbPages = updatedList
            }, context = context,
            onRemoveTwitter = {
                twitterProfile = null
            },onRemoveInstagram={
                instaProfile=null
            },

        )
        ScheduleSection(
            startDate = startDate,
            endDate = endDate,
            times = selectedTimes,
            onStartDateSelect = { startDate = it },
            onEndDateSelect = { endDate = it },
            onAddTime = { selectedTimes.add(it) },
            onRemoveTime = { selectedTimes.remove(it) }
        )

        Divider(color = Color(0xFFE0E0E0))

        // ✅ CONTENT FORM
        PostContentSection(   text = postText, selectedImageUri = selectedImageUri,selectedVideoUri,
            onTextChange = { postText = it },  onAddMediaClick = { showSheet = true }, onRemoveImage = { selectedImageUri = null }, onRemoveVideo = {

            }, onAddAIText = {
                aiDialog=true
            })

        GenerateAIDialog(
            showDialog = aiDialog,
            onDismiss = { aiDialog = false },
            onGenerate = { topic ->
                navController.navigate("aichatpage/$topic")
            }
        )
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

                        val permissions =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES)
                            } else {
                                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            }

                        mediaPermissionLauncher.launch(permissions)

                    }

                    BottomSheetItem(
                        icon = Icons.Default.Videocam,
                        title = "Add Video"
                    ) {
                        showSheet = false

                        val permissions =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                arrayOf(android.Manifest.permission.READ_MEDIA_VIDEO)
                            } else {
                                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            }

                        mediaPermissionLauncher.launch(permissions)
                    }

                    BottomSheetItem(
                        icon = Icons.Default.CameraAlt,
                        title = "Open Camera"
                    ) {
                        showSheet = false

                    }
                }
            }
        }

    }
}



@Composable
fun GenerateAIDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onGenerate: (String) -> Unit
) {
    var topic by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Write topic",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter the topic to generate AI content",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        placeholder = { Text("Eg: Benefits of AI in Healthcare") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines  = 3,
                        maxLines=5
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = topic.isNotBlank(),
                    onClick = {
                        onGenerate(topic)
                        onDismiss()
                    }
                ) {
                    Text("Generate")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
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
fun PostContentSection(text: String, selectedImageUri: Uri?,selectedVideoUri:Uri?,
                           onTextChange: (String) -> Unit, onRemoveImage: () -> Unit, onRemoveVideo: () -> Unit, onAddMediaClick: () -> Unit,onAddAIText:()-> Unit) {
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

//        Spacer(Modifier.height(24.dp))
//
//        // Content Type Row
//        Row(verticalAlignment = Alignment.CenterVertically) {
//
//            Icon(
//                painter = painterResource(R.drawable.ic_fb),
//                contentDescription = "",
//                modifier = Modifier.size(22.dp)
//            )
//
//            Spacer(Modifier.width(8.dp))
//
//            Text("Content type", fontSize = 14.sp)
//
//            Spacer(Modifier.weight(1f))
//
//            Icon(
//                painter = painterResource(R.drawable.ic_grid),
//                contentDescription = "",
//                modifier = Modifier.size(18.dp)
//            )
//
//            Spacer(Modifier.width(6.dp))
//
//            Text("Post", color = Color.Gray)
//        }

        Spacer(Modifier.height(18.dp))

        // Text Area
        BasicTextField(
            value = text,
            onValueChange = { onTextChange(it) },
            modifier = Modifier.fillMaxWidth()
                .height(200.dp)
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



        // ---------- Selected Image in Card ----------
        selectedImageUri?.let { uri ->
            MediaCard(
                onRemove = onRemoveImage
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        //--- video------
        selectedVideoUri?.let { uri ->
            VideoPreviewCard(
                videoUri = uri,
                onRemove = onRemoveVideo
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {onAddAIText() }
        ) {
            Icon(
                imageVector = Icons.Default.Image, // or your custom icon
                contentDescription = "AI",
                tint = Color(0xFF1976D2), // Blue color
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Add AI Generated Text",
                color = Color(0xFF1976D2),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }


    }
}
@Composable
internal fun MediaCard(
    onRemove: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .size(150.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            content()

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Remove",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GradientDateTimePickerBox(
    formattedDate: String,
    selectedDateTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val LightSkyBlue = Color(0xFF87CEFA)
    val SoftLavender = Color(0xFFE6E6FA)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(LightSkyBlue, SoftLavender)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = formattedDate,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleSection(
    startDate: LocalDate?,
    endDate: LocalDate?,
    times: List<LocalTime>,
    onStartDateSelect: (LocalDate) -> Unit,
    onEndDateSelect: (LocalDate) -> Unit,
    onAddTime: (LocalTime) -> Unit,
    onRemoveTime: (LocalTime) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Column(modifier = Modifier.padding(14.dp)) {

        Text("Schedule", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        Spacer(Modifier.height(12.dp))

        // -------- START DATE --------
        ScheduleDateBox(
            label = "Start Date",
            value = startDate?.format(dateFormatter) ?: "Select",
            onClick = {
                DatePickerDialog(
                    context,
                    { _, y, m, d -> onStartDateSelect(LocalDate.of(y, m + 1, d)) },
                    LocalDate.now().year,
                    LocalDate.now().monthValue - 1,
                    LocalDate.now().dayOfMonth
                ).show()
            }
        )

        Spacer(Modifier.height(8.dp))

        // -------- END DATE --------
        ScheduleDateBox(
            label = "End Date",
            value = endDate?.format(dateFormatter) ?: "Select",
            onClick = {
                DatePickerDialog(
                    context,
                    { _, y, m, d -> onEndDateSelect(LocalDate.of(y, m + 1, d)) },
                    LocalDate.now().year,
                    LocalDate.now().monthValue - 1,
                    LocalDate.now().dayOfMonth
                ).show()
            }
        )

        Spacer(Modifier.height(12.dp))

        // -------- TIMES --------
        Text("Times", fontWeight = FontWeight.Medium)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(times.size) { index ->
                TimeChip(
                    time = times[index].format(timeFormatter),
                    onRemove = { onRemoveTime(times[index]) }
                )
            }

            item {
                AddTimeChip {
                    TimePickerDialog(
                        context,
                        { _, h, m -> onAddTime(LocalTime.of(h, m)) },
                        10,
                        0,
                        true
                    ).show()
                }
            }
        }
    }
}


@Composable
fun ScheduleDateBox(label: String, value: String, onClick: () -> Unit) {
    Column {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(12.dp)
        ) {
            Text(value)
        }
    }
}

@Composable
fun TimeChip(time: String, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .background(Color(0xFFE7F0FF), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(time)
            Spacer(Modifier.width(6.dp))
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier
                    .size(14.dp)
                    .clickable { onRemove() }
            )
        }
    }
}

@Composable
fun AddTimeChip(onAdd: () -> Unit) {
    Box(
        modifier = Modifier
            .border(1.dp, Color(0xFF1976D2), RoundedCornerShape(20.dp))
            .clickable { onAdd() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text("+ Add Time", color = Color(0xFF1976D2))
    }
}
fun convertUriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Cannot open input stream from URI")

    val file = File(
        context.cacheDir,
        "telegram_${System.currentTimeMillis()}.jpg"
    )

    file.outputStream().use { output ->
        inputStream.use { input ->
            input.copyTo(output)
        }
    }

    return file
}


fun sendTelegramPhoto(imageFile: File) {

    val botToken = "8568025768:AAGd_p5-Bn94cOTco5hMikyCkuC3AO9Tot4" // after revoke
    val chatId = "@mounikaheshu"
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("chat_id", chatId)
        .addFormDataPart("caption", "Hello with image")
        .addFormDataPart(
            "photo",
            imageFile.name,
            imageFile.asRequestBody("image/*".toMediaType())
        )
        .build()

    val request = Request.Builder()
        .url("https://api.telegram.org/bot$botToken/sendPhoto")
        .post(requestBody)
        .build()
    val client = OkHttpClient()

    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            Log.e("Telegram", "Failed", e)
        }

        override fun onResponse(call: Call, response: Response) {
            Log.d("Telegram", response.body?.string() ?: "")
        }
    })

}
