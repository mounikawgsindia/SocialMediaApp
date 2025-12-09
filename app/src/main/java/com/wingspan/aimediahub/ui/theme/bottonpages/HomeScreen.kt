package com.wingspan.aimediahub.ui.theme.bottonpages


import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.CookieManager
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.postDelayed
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.facebook.login.LoginManager
import com.wingspan.aimediahub.R
import com.wingspan.aimediahub.utils.AppTextStyles.MainHeadingBlack
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.wingspan.aimediahub.MainActivity
import com.wingspan.aimediahub.models.SocialAccount
import com.wingspan.aimediahub.utils.AppTextStyles
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.viewmodel.FacebookViewModel
import com.wingspan.aimediahub.viewmodel.InstagramViewModel

import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen( bottomNavController: NavHostController,
                rootNavController: NavHostController, viewModel: FacebookViewModel =hiltViewModel(),instamodel: InstagramViewModel=hiltViewModel()) {




    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars
                .union(WindowInsets.navigationBars)
                .asPaddingValues()),
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
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hello",
                    style = MainHeadingBlack
                )

                Text(
                    text = "Logout",
                    style = MainHeadingBlack,
                    modifier = Modifier.clickable {
                        logoutFacebook()

                    }
                )
            }

            Spacer(Modifier.height(20.dp))

            // ---------------- CONNECTED & NON-CONNECTED ACCOUNTS ----------------
            Spacer(Modifier.height(20.dp))
            ConnectedAccountsSection(viewModel)
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

            // ---------------- RECENT POSTS ----------------
            Text(
                text = "Recent Posts",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            RecentPostItem()
            RecentPostItem()

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
                modifier = Modifier.weight(1f).clickable {
                    bottomNavController.navigate("create") {
                        // optional: avoid multiple copies in back stack
                        launchSingleTop = true
                        restoreState = true
                    }


                }   // Equal width

                )
                ActionButton(
                    title = "Automation Post",
                    icon = R.drawable.ic_settings,
                    bgColor = Color(0xFFFFF4D6),   // Light Yellow
                    borderColor = Color(0xFFFFE082),
                    modifier = Modifier.weight(1f)
                )
            }

        }

    }

}
fun logoutFacebook() {
    // 1️⃣ Clear SDK token & session
    LoginManager.getInstance().logOut()

    // 2️⃣ Clear WebView cookies (used by Facebook Login)
    CookieManager.getInstance().removeAllCookies(null)
    CookieManager.getInstance().flush()

    Log.d("FB_LOGOUT", "User logged out completely")
}


@Composable
fun SocialIcon(
    icon: Int,
    label: String,
    connected: Boolean = false,
    imageUrl: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.clickable { onClick() }) {
        Box {
            if (connected && imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = label,
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(10.dp))
                )
            } else {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    tint = if (connected) Color.Unspecified else Color.Gray,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Small tick if connected
            if (connected) {
                Icon(
                    painter = painterResource(R.drawable.ic_tick), // small green tick icon
                    contentDescription = "Connected",
                    tint = Color(0xFF34A853),
                    modifier = Modifier.size(16.dp).align(Alignment.TopEnd)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
fun RecentPostItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.LightGray.copy(alpha = 0.4f))
        )
        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE4C8FF))
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFD0B3FA))
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
    viewModel: FacebookViewModel = hiltViewModel(),instamodel: InstagramViewModel=hiltViewModel()
) {
    val fbConnected = viewModel.fbConnected.collectAsState()
    val fbPageImage = viewModel.fbPageImage.collectAsState()
    var context=LocalContext.current
    val prefs = remember { Prefs(context) }
    // Define all social platforms
    val allAccounts = listOf(
        SocialAccount("Facebook", fbConnected.value, fbPageImage.value, "fbToken"),
        SocialAccount("Instagram"),
        SocialAccount("LinkedIn"),
        SocialAccount("Twitter"),
        SocialAccount("YouTube")
    )
    val activity = LocalContext.current as MainActivity
    val callbackManager = activity.callbackManager
    val connectedAccounts = allAccounts.filter { it.connected }
    val nonConnectedAccounts = allAccounts.filter { !it.connected }

    Column(modifier = Modifier.fillMaxWidth()) {
        // ---------------- CONNECTED ----------------
        if (connectedAccounts.isNotEmpty()) {
            Text(
                "Connected Accounts",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                connectedAccounts.forEach { account ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE7F0FF))
                            .clickable{
                                if (account.platform == "Facebook") {
                                    logoutFacebook()
                                    viewModel.setFbDisconnected()
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        // Social icon fully colored for Facebook
                        Icon(
                            painter = painterResource(id = getIcon(account.platform)),
                            contentDescription = account.platform,
                            tint = if (account.platform.lowercase() == "facebook") Color(0xFF1877F2) else Color.Unspecified,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(Modifier.width(8.dp))

                        // Show page image on the right side if exists
                        if (!account.imageUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = account.imageUrl,
                                contentDescription = account.platform,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(15.dp))
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
                        onClick = {
                            // Start login/connect flow
                            if (account.platform == "Facebook") {

                                // logoutFacebook()

                                LoginManager.getInstance().logInWithReadPermissions(
                                    activity,
                                    listOf(
                                        "public_profile",
                                        "email",
                                        "pages_show_list",
                                        "pages_manage_posts",
                                        "pages_read_engagement"
                                    )
                                )
                                // Register callback once
                                LoginManager.getInstance().registerCallback(
                                    callbackManager,
                                    object : FacebookCallback<LoginResult> {
                                        override fun onSuccess(result: LoginResult) {

                                            Log.d("FB_LOGIN", "Login success callback hit")
                                            val permissions = result.accessToken?.permissions ?: emptySet()
                                            Log.d("FB_LOGIN", "Permissions = $permissions")

                                            // ✅ Request publish only if missing
                                            if (!permissions.contains("pages_manage_posts")) {

                                                // ❗IMPORTANT: delay helps avoid Facebook SDK crash
                                                Handler(Looper.getMainLooper()).postDelayed({

                                                    LoginManager.getInstance().logInWithPublishPermissions(
                                                        activity,
                                                        listOf("pages_manage_posts")
                                                    )

                                                }, 800)

                                                return
                                            }
                                            val token = result.accessToken?.token

                                            //get longlikev token and call getfbpages fun in viewmodel
                                            //own account
                                            viewModel.getLongLiveToken("1109850424381007","a8d1e54105ae4e089cc97e5d72e4df53",
                                                token.toString()
                                            )

                                            //company account
//                                            viewModel.getLongLiveToken("1157175713250946","04f63b558e0932cea6df31a46b876ad6",
//                                                token.toString()
//                                            )

                                            Log.d("FB_LOGIN", "Token = $token")
                                        }

                                        override fun onCancel() {
                                            Log.d("FB_LOGIN", "Login canceled")
                                        }

                                        override fun onError(error: FacebookException) {
                                            Log.e("FB_LOGIN", "Error: ${error.message}")
                                        }
                                    })


                                //viewModel.getfbResponse()
                            }
                            if(account.platform=="Instagram"){
                                val shortToken = "IGAATWB2zHKD1BZAGJNbS1MTUZAUSVVlLTVOM1FoWXh4MWZAKN3dKTzI1TXdzclByMFdLZAXAzVkg2Ym5xOS1SOG8tMUZAjVXpwX2NCY2p3YmpTMVNXWGowdHM0R0RIX3pLSzJIeHRaYTNxa1pIbmE2LXV1dlBtWEVGS2lkclJNOWRQNAZDZD"



                            }
                        }
                    )
                }
            }
        }
    }
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



@Composable
fun CalendarScreen() = ScreenBase("Calendar Screen")

@Composable
fun AnalyticsScreen() = ScreenBase("Analytics Screen")

@Composable
fun ProfileScreen() = ScreenBase("Profile Screen")


@Composable
fun ScreenBase(title: String) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, fontSize = 22.sp)
    }
}