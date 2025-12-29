package com.wingspan.aimediahub.ui.theme.bottonpages


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.facebook.login.LoginManager
import com.wingspan.aimediahub.R
import com.wingspan.aimediahub.utils.AppTextStyles.MainHeadingBlack
import com.wingspan.aimediahub.MainActivity
import com.wingspan.aimediahub.models.SocialAccount
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.viewmodel.FacebookViewModel
import kotlin.io.encoding.ExperimentalEncodingApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import okhttp3.Call
import okhttp3.Callback

import okhttp3.Response
import java.io.IOException
@OptIn(ExperimentalEncodingApi::class)
@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen( bottomNavController: NavHostController,
                rootNavController: NavHostController,pref:Prefs,fbDeepLink:String ,twitterDeepLink:String,linkedInDeepLink:String,viewModel: FacebookViewModel =hiltViewModel()) {

    var context= LocalContext.current
    val isLoading by viewModel.fbLoading.collectAsState()
    var showWebView by remember { mutableStateOf(false) }
    var flatformDisConnect by remember { mutableStateOf("") }
    Log.d("deep link check","--->${fbDeepLink}...${twitterDeepLink}....${linkedInDeepLink}")

    var disconnectDialog by remember { mutableStateOf(false) }
    //facebook state
    val fbDisStatus by viewModel.fbDisStatus.collectAsState()
    val fbPages by viewModel.facebookaccounts.collectAsState()
    val fbPagesError by viewModel.facebookError.collectAsState()

    val twitterErrorState by viewModel.twitterError.collectAsState()

    LaunchedEffect(fbDisStatus) {
        fbDisStatus?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

        }
    }
    LaunchedEffect(fbPagesError) {
        fbPagesError?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(twitterErrorState) {
        twitterErrorState?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    //deep links
    LaunchedEffect(fbDeepLink) {
        if (fbDeepLink == "true") {
            // Normal flow: load from cache
            viewModel.getFbPages()
        }
    }
    LaunchedEffect(twitterDeepLink) {
        if (twitterDeepLink == "true") {
            // Normal flow: load from cache
            viewModel.twitterProfile()
        }
    }

    LaunchedEffect(linkedInDeepLink) {
        if (linkedInDeepLink == "true") {
            // Normal flow: load from cache
            viewModel.linkedinProfile()
        }
    }

    if (isLoading) {
        // Loader during API call
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    WindowInsets.statusBars
                        .union(WindowInsets.navigationBars)
                        .asPaddingValues()
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // -------------------- TITLE --------------------
                Row(
                    modifier = Modifier
                        .fillMaxWidth().clickable{
                            verifyTelegramChannel("@momlovesdaughter") { success, message ->
                                if (success) {
                                   Log.d("checking","${message}")
                                    sendTelegramMessage()
                                } else {
                                    Log.e("checking","${message}")
                                }
                            }

                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hello",
                        style = MainHeadingBlack
                    )


                }

                Spacer(Modifier.height(20.dp))

                // ---------------- CONNECTED & NON-CONNECTED ACCOUNTS ----------------
                Spacer(Modifier.height(20.dp))
                ConnectedAccountsSection(viewModel,context,pref, onFacebookConnect = {

                    showWebView = true

                }, onDisconnec = { platform->
                    Log.d("dis","--->${platform}")
                    flatformDisConnect=platform
                    disconnectDialog=true

                },OnConnectionclick={ platform, pageId ,username->
                    Log.d("OnConnectionClick", "Platform: $platform, PageId: $pageId")
                    if(platform.equals("Twitter")){
                        openTwitterProfile(context,username)
                    }else if(platform.equals("Facebook")){
                        openFacebookPage(context, pageId)
                    }

                })
                if (showWebView) {
                    connectFacebook(context,pref)
                }
                Spacer(Modifier.height(25.dp))


                // ---------------- QUICK STATS ----------------
                Text(
                    text = "Quick Stats",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    StatBox(title = "Posts", value = "25", color = Color.Blue)
                    StatBox(title = "Followers", value = "1.2k", color = Color(0xFF34A853))  // Green
                    StatBox(title = "Engagement", value = "8.4%", color = Color(0xFFFB8C00)) // Orange
                }

                // ---------------- upcomming POSTS ----------------
                Spacer(Modifier.height(12.dp))
                UpcomingPostsSection(onArrowClick = {
                    rootNavController.navigate("calendar") {
                        // optional: avoid multiple copies in back stack
                        launchSingleTop = true
                        restoreState = true
                    }
                })


                Spacer(Modifier.height(25.dp))

                // ---------------- QUICK ACTIONS ----------------
                Text(
                    text = "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)   // small space only
                ) {
                    ActionButton(
                        title = "Manual Post",
                        icon = R.drawable.ic_edit,
                        bgColor = Color(0xFFE3F2FF),
                        borderColor = Color(0xFF90CAF9),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                rootNavController.navigate("create_post_screen")

                            }

                    )
                    ActionButton(
                        title = "Automation Post",
                        icon = R.drawable.ic_settings,
                        bgColor = Color(0xFFFFF4D6),   // Light Yellow
                        borderColor = Color(0xFFFFE082),
                        modifier = Modifier.weight(1f)
                            .clickable{
                                rootNavController.navigate("autoposting_screen") }
                    )
                }

            }

        }
    }
    if(disconnectDialog){

        AlertDialog(
            onDismissRequest = { disconnectDialog = false },
            title = { Text("Disconnect ${flatformDisConnect}") },
            text = { Text("Are you sure you want to disconnect your ${flatformDisConnect} account?") },
            confirmButton = {
                Log.d("dis dialog","--${flatformDisConnect}")
                TextButton(
                    onClick = {
                        disconnectDialog = false
                        if(flatformDisConnect.equals("Facebook")){
                            viewModel.setFbDisconnected()
                        }else if(flatformDisConnect.equals("Twitter")){
                            viewModel.setTwitterDisconnected()
                        }else if(flatformDisConnect.equals("LinkedIn")){
                            viewModel.setLinkedDisconnected()
                        }

                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { disconnectDialog = false }) {
                    Text("No")
                }
            }
        )

    }


}
fun sendTelegramMessage() {
    //nirvan
    val botToken = "8568025768:AAGd_p5-Bn94cOTco5hMikyCkuC3AO9Tot4" // after revoke
    val chatId = "@momlovesdaughter"
    val message = "my first message"

    val encodedMessage = URLEncoder.encode(message, "UTF-8")

    val url =
        "https://api.telegram.org/bot$botToken/sendMessage" +
                "?chat_id=$chatId&text=$encodedMessage"

    val client = OkHttpClient()

    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : okhttp3.Callback {

        override fun onFailure(call: Call, e: IOException) {
            Log.e("Telegram", "Message failed", e)
        }

        override fun onResponse(call: Call, response: Response) {
            Log.d("Telegram", "Success: ${response.body?.string()}")
        }
    })
}
fun verifyTelegramChannel(
    channelUsername: String,
    onResult: (Boolean, String) -> Unit
) {
    val botToken = "8568025768:AAGd_p5-Bn94cOTco5hMikyCkuC3AO9Tot4" // local testing only
    val testMessage = "."

    val encodedMessage = URLEncoder.encode(testMessage, "UTF-8")

    val url =
        "https://api.telegram.org/bot$botToken/sendMessage" +
                "?chat_id=$channelUsername&text=$encodedMessage"

    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            onResult(false, "Network error")
        }

        override fun onResponse(call: Call, response: Response) {
            when (response.code) {
                200 -> onResult(true, "Bot is admin → Channel connected")
                403 -> onResult(false, "Bot is NOT admin")
                401 -> onResult(false, "Invalid bot token")
                400 -> onResult(false, "Invalid channel")
                else -> onResult(false, "Unknown error")
            }
        }
    })
}


@Composable
fun UpcomingPostsSection(onArrowClick:()->Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(1.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column {

            // 🔹 Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(color = Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming posts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "View all",
                    tint = Color.Blue,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable { onArrowClick() }
                )
            }

            // Divider below header
            Divider(
                thickness = 0.6.dp,
                color = Color(0xFFE0E0E0)
            )

            // 📌 Upcoming Posts List
            UpcomingPostItemFlat(
                postText = "Hi facebook",
                date = "17/12/2025",
                time = "14:00",
                platformIcon = R.drawable.ic_fb
            )

            UpcomingPostItemFlat(
                postText = "Hi twitter",
                date = "17/12/2025",
                time = "12:00",
                platformIcon = R.drawable.ic_twitter
            )
        }
    }
}



@Composable
fun SocialIcon(
    icon: Int,
    label: String,
    connected: Boolean,
    showDisconnectOnly: Boolean = false,
    onConnectClick: () -> Unit,    // ✅ Only connect
    onDisconnectClick: () -> Unit // ✅ Only disconnect
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF5F5F5))
            .clickable(enabled = !connected) {  // ✅ DISABLED WHEN CONNECTED
                onConnectClick()
            }
            .padding(8.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {

            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = when {
                    showDisconnectOnly -> Color(0xFF1877F2) // blue
                    connected -> Color(0xFF1877F2)
                    else -> Color(0xFF9E9E9E) // grey
                },
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (connected || showDisconnectOnly) Color.Black else Color(0xFF9E9E9E)
            )
        }

        // ✅ ❌ Disconnect Badge
        if (showDisconnectOnly) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
                    .clickable {
                        onDisconnectClick()  // ✅ ONLY ❌ disconnects
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Disconnect",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}






@Composable
fun StatBox(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = title, fontSize = 14.sp, color = color)
    }
}
@Composable
fun UpcomingPostItemFlat(
    postText: String,
    date: String,
    time: String,
    platformIcon: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Platform Icon
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(Color(0xFFE7F0FF)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = platformIcon),
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Post Text + Status
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = postText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Time & Date
        Column(
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = date,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = time,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )

        }
    }


}




@Composable
fun ActionButton(title: String, icon: Int,
                 bgColor: Color,
                 borderColor: Color,modifier: Modifier) {
    Box(
        modifier = modifier
            .height(55.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = Color.Black
            )
            Spacer(Modifier.width(6.dp))
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}
@SuppressLint("ContextCastToActivity")
@Composable
fun ConnectedAccountsSection(
    viewModel: FacebookViewModel = hiltViewModel(),
    context: Context, pref: Prefs, onFacebookConnect: () -> Unit,onDisconnec:(String)->Unit,OnConnectionclick:(String,String,String)->Unit
) {

    val accounts = viewModel.facebookaccounts.collectAsState()
    val twitterData=viewModel.twitterProfile.collectAsState()
    val instagramData =viewModel.instaProfile.collectAsState()
    val linkedInData=viewModel.linkedInProfile.collectAsState()


    Log.d("data","---${accounts}....${twitterData}")
    val activity = LocalContext.current as MainActivity
    val callbackManager = activity.callbackManager

    // ---------------- CREATE LIST OF ACCOUNTS ----------------
    // Facebook pages -> multiple connected accounts

    var facebookAccounts = accounts.value.map { page ->
        SocialAccount(
            id= page.id.toString(),
            platform = "Facebook",
            connected = true,
            imageUrl = page.imageUrl,
            accessToken = page.accessToken
        )
    }
    val twitterAccount = twitterData.value?.let {
        Log.d("data","---${it.accessToken}....${it.imageUrl}")
        SocialAccount(
            id= it.id.toString(),
            platform = "Twitter",
            connected = true,
            imageUrl = it.imageUrl,
            accessToken = it.accessToken
        )
    }
    val instagramAccount = instagramData.value?.let {
        Log.d("data","---${it.accessToken}....${it.imageUrl}")
        SocialAccount(
            id= it.id.toString(),
            platform = "Instagram",
            connected = true,
            imageUrl = it.imageUrl,
            accessToken = it.accessToken
        )
    }

    val linkedInAccount = linkedInData.value?.let {
        Log.d("data","---${it.accessToken}....${it.imageUrl}")
        SocialAccount(
            id= it.id.toString(),
            platform = "LinkedIn",
            connected = true,
            imageUrl = it.imageUrl,
            accessToken = it.accessToken
        )
    }


    // 2️⃣ If no pages exist, show a placeholder to allow connection
    val facebookDisconnectOnly = if (facebookAccounts.isNotEmpty()) {
        listOf(
            SocialAccount(
                id= 0.toString(),
                platform = "Facebook",
                connected = true,
                showDisconnectOnly = true // ✅ special behavior
            )
        )
    } else {
        listOf(
            SocialAccount(
                id= 0.toString(),
                platform = "Facebook",
                connected = false
            )
        )
    }



    val twitterDisconnectOnly = if (twitterAccount!= null) {
        listOf(
            SocialAccount(
                id= 0.toString(),
                platform = "Twitter",
                connected = true,
                showDisconnectOnly = true // ✅ special behavior
            )
        )
    } else {
        listOf(
            SocialAccount(
                id= 0.toString(),
                platform = "Twitter",
                connected = false
            )
        )
    }


    val linkedInDisconnectOnly = if (linkedInAccount!= null) {
        listOf(
            SocialAccount(
                id= 0.toString(),
                platform = "LinkedIn",
                connected = true,
                showDisconnectOnly = true // ✅ special behavior
            )
        )
    } else {
        listOf(
            SocialAccount(
                id= 0.toString(),
                platform = "LinkedIn",
                connected = false
            )
        )
    }

    val instaDisconnectOnly = if (instagramAccount!= null) {
        listOf(
            SocialAccount(
                id= 0.toString(),
                platform = "Instagram",
                connected = true,
                showDisconnectOnly = true // ✅ special behavior
            )
        )
    } else {
        listOf(
            SocialAccount(
                id= 0.toString(),
                platform = "Instagram",
                connected = false
            )
        )
    }
    // Other social accounts (single entry)

    var otherAccounts = listOf(
        SocialAccount("YouTube")
    )

    val connectedAccounts = facebookAccounts +
            listOfNotNull(twitterAccount)+listOfNotNull(linkedInAccount)
            listOfNotNull(instagramAccount)

    val nonConnectedAccounts = facebookDisconnectOnly +twitterDisconnectOnly+instaDisconnectOnly+linkedInDisconnectOnly+ otherAccounts


    Column(modifier = Modifier.fillMaxWidth()) {
        // ---------------- CONNECTED ----------------
        if (connectedAccounts.isNotEmpty()) {

            Log.d("connected fb","---${connectedAccounts}")
            Text(
                "Connected Accounts",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(connectedAccounts.size) { index ->
                    var account = connectedAccounts[index]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE7F0FF))
                            .clickable{
                                Log.d("TAG", "Page ID: $account.id")
                                OnConnectionclick(account.platform,account.id,
                                    account.accessToken.toString()
                                )
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = getIcon(account.platform ?: "")),
                            contentDescription = account.platform,
                            tint = if (account.platform.lowercase() == "facebook") Color(0xFF1877F2) else Color.Unspecified,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(Modifier.width(8.dp))

                        if (!account.imageUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = account.imageUrl,
                                contentDescription = account.platform,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                }
            }

        }
        Spacer(Modifier.height(16.dp))

        // ---------------- NOT CONNECTED ----------------
        if (nonConnectedAccounts.isNotEmpty()) {
            Text("Accounts to Connect", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                nonConnectedAccounts.forEach { account ->
                    SocialIcon(
                        icon = getIcon(account.platform),
                        label = account.platform,
                        connected = false,
                        showDisconnectOnly =account.showDisconnectOnly ,
                        onDisconnectClick = {
                            // ✅ ONLY for connected ❌
                            if (account.platform == "Facebook") {
                                onDisconnec(account.platform.toString())

                            }else if(account.platform == "Twitter"){
                               onDisconnec(account.platform.toString())
                            }else if(account.platform=="Instagram"){
                                logoutInstagram(viewModel)
                            }else if(account.platform=="LinkedIn"){
                                onDisconnec(account.platform.toString())
                            }
                        },
                        onConnectClick = {
                            // Start login/connect flow
                            if (account.platform == "Facebook") {
                               onFacebookConnect()
                            }
                            if(account.platform=="Instagram"){
                                val userId = pref.getUserID()
                                val loginUrl = "https://automatedpostingbackend.onrender.com/auth/twitter?userId=${userId}&platform=android"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(loginUrl))
                                context.startActivity(intent)
                            }
                            if(account.platform=="Twitter"){
                                val userId = pref.getUserID()
                                val loginUrl = "https://automatedpostingbackend.onrender.com/auth/twitter?userId=${userId}&platform=android"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(loginUrl))
                                context.startActivity(intent)

                            }
                            if(account.platform=="LinkedIn"){
                                val userId = pref.getUserID()
                                val loginUrl = "https://automatedpostingbackend.onrender.com/auth/linkedin?userId=${userId}&platform=android"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(loginUrl))
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }
        }
    }
}

fun connectFacebook(context: Context, pref: Prefs) {
    val userId = pref.getUserID()
    Log.d("my id data","---${userId}")
    val loginUrl = "https://www.facebook.com/v20.0/dialog/oauth?" +
            "client_id=4196581700605802" +
            "&redirect_uri=https%3A%2F%2Fautomatedpostingbackend.onrender.com%2Fsocial%2Ffacebook%2Fcallback" +
            "&state=${userId}%3Aandroid" +  // inject userId here
            "&scope=pages_read_engagement,pages_manage_posts,pages_show_list,public_profile,email"

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(loginUrl))
    context.startActivity(intent)
}
fun getIcon(platform: String): Int {
    return when (platform.lowercase()) {
        "facebook" -> R.drawable.ic_fb
        "instagram" -> R.drawable.ic_instagram
        "linkedin" -> R.drawable.ic_linkedin
        "twitter" -> R.drawable.ic_twitter
        "youtube" -> R.drawable.ic_youtube
        else -> R.drawable.ic_launcher_foreground // fallback icon
    }
}




fun logoutInstagram(viewModel: FacebookViewModel){
    viewModel.setInstagramDisconnected()
}






fun openFacebookPage(context: Context, pageId: String) {
    val TAG = "FacebookOpen"

    val webUrl = "https://www.facebook.com/profile.php?id=$pageId"

    Log.d(TAG, "Opening Facebook Page")
    Log.d(TAG, "Page ID: $pageId")
    Log.d(TAG, "URL: $webUrl")

    try {
        // Check if Facebook app is installed
        context.packageManager.getPackageInfo("com.facebook.katana", 0)

        Log.d(TAG, "Facebook app installed → opening inside app")

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
            setPackage("com.facebook.katana")   // 🔥 Force Facebook App
        }
        context.startActivity(intent)

    } catch (e: Exception) {
        Log.d(TAG, "Facebook app not installed → opening browser")
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)))
    }
}
fun openTwitterProfile(context: Context, userName: String) {
    val TAG = "TwitterOpen"

    // Web URL fallback
    val webUrl = "https://twitter.com/$userName"

    Log.d(TAG, "Opening Twitter Profile")
    Log.d(TAG, "Username: $userName")
    Log.d(TAG, "URL: $webUrl")

    try {
        // Check if Twitter (X) app is installed
        context.packageManager.getPackageInfo("com.twitter.android", 0)

        Log.d(TAG, "Twitter app installed → opening inside app")

        // Twitter deep link
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("twitter://user?screen_name=$userName")
        ).apply {
            setPackage("com.twitter.android") // 🔥 Force Twitter App
        }

        context.startActivity(intent)

    } catch (e: Exception) {
        Log.d(TAG, "Twitter app not installed → opening browser")
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)))
    }
}


