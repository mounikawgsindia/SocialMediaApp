package com.wingspan.aimediahub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import com.facebook.CallbackManager
import com.wingspan.aimediahub.models.OnBoardModel
import com.wingspan.aimediahub.ui.theme.AiMediaHubTheme
import com.wingspan.aimediahub.ui.theme.bottonpages.AppNavigation
import com.wingspan.aimediahub.ui.theme.AutoOnBoardScreen
import com.wingspan.aimediahub.ui.theme.CreatePostScreen
import com.wingspan.aimediahub.ui.theme.DayScreen
import com.wingspan.aimediahub.ui.theme.auth.LoginScreen
import com.wingspan.aimediahub.ui.theme.auth.RegistrationScreen
import com.wingspan.aimediahub.ui.theme.AIChatPage
import com.wingspan.aimediahub.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    internal lateinit var callbackManager: CallbackManager
    @RequiresApi(Build.VERSION_CODES.O)

    @Inject
    lateinit var prefs : Prefs
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Make content draw behind system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        callbackManager = CallbackManager.Factory.create()
        handleDeepLink(intent)
        setContent {
            AiMediaHubTheme {

                   // MainApp()
                MainApp(prefs)
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("FB_ACTIVITY", "Activity result received")

        callbackManager.onActivityResult(requestCode, resultCode, data)  // ✅ FORWARD
    }
    // 🔹 ADD THIS FUNCTION HERE
    private fun handleDeepLink(intent: Intent?) {
        val uri = intent?.data ?: return

        if (uri.scheme == "com.wingspan.aimediahub" &&
            uri.host == "login-success") {

            val token = uri.getQueryParameter("token")

            Log.d("FB_CALLBACK", "JWT token = $token")

            if (!token.isNullOrEmpty()) {
               Log.d("token","---data${token}")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainApp(prefs: Prefs) {
    val rootNavController = rememberNavController() // root navController

    val startDestination = remember {
        when {
            prefs.isFirstTime() -> "onboarding"
            prefs.isLoggedIn() -> "main"
            else -> "login"
        }
    }

    val onBoardList = listOf(
        OnBoardModel(
            image = R.drawable.onb5,
            title = "Manage all your social accounts",
            description = "Faster posting, smarter automation — all in one place."
        ),
        OnBoardModel(
            image = R.drawable.onb2,
            title = "Schedule posts effortlessly",
            description = "Plan your content ahead of time and stay organized."
        ),
        OnBoardModel(
            image = R.drawable.onb3,
            title = "Get insights & analytics",
            description = "Boost your social media strategy with useful analytics."
        )
    )

    NavHost(navController=rootNavController, startDestination = startDestination){
    //onboarding screen
        composable("onboarding") {
            AutoOnBoardScreen(
                onBoardList = onBoardList,
                onSkip = {

                    prefs.setFirstTime()
                    rootNavController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                onFinish = {
                    prefs.setFirstTime()
                    rootNavController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        // LOGIN SCREEN
        composable("login") {
            LoginScreen(
                navController = rootNavController)
        }


        // registration
        composable("registration") {
            RegistrationScreen(rootNavController)
        }

        // ✅ Main App Screen (Bottom Nav starts here)
        composable("main") {
            AppNavigation(rootNavController,prefs)
        }
        // ✅ Full-screen Create Post page (outside Bottom Navigation)
        composable("create_post_screen") {
            CreatePostScreen(rootNavController,prefs)
        }

//        composable("dayscreen") {
//            DayScreen(rootNavController)
//        }

        composable("aichatpage/{topic}") { backStackEntry ->
            val topic = backStackEntry.arguments?.getString("topic") ?: ""
            AIChatPage(
                topic = topic,
                navController = rootNavController
            )
        }


    }


}

