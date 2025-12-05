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
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import com.facebook.CallbackManager
import com.wingspan.aimediahub.models.OnBoardModel
import com.wingspan.aimediahub.ui.theme.AiMediaHubTheme
import com.wingspan.aimediahub.ui.theme.AppNavigation
import com.wingspan.aimediahub.ui.theme.AutoOnBoardScreen
import com.wingspan.aimediahub.ui.theme.LoginScreen
import com.wingspan.aimediahub.ui.theme.RegistrationScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    internal lateinit var callbackManager: CallbackManager
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Make content draw behind system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        callbackManager = CallbackManager.Factory.create()
        setContent {
            AiMediaHubTheme {

                   // MainApp()
                MainApp()
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("FB_ACTIVITY", "Activity result received")

        callbackManager.onActivityResult(requestCode, resultCode, data)  // ✅ FORWARD
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainApp() {
    val navController = rememberNavController()
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

    NavHost(navController=navController, startDestination = "main"){
    //onboarding screen
        composable("onboarding") {
            AutoOnBoardScreen(
                onBoardList = onBoardList,
                onSkip = { navController.navigate("login") },
                onFinish = { navController.navigate("login") }
            )
        }
        // LOGIN SCREEN
        composable("login") {
            LoginScreen(navController)
        }

        // registration
        composable("registration") {
            RegistrationScreen(navController)
        }

        // ✅ Main App Screen (Bottom Nav starts here)
        composable("main") {
            AppNavigation()
        }

//
//        //homescreen
//        composable("homescreen") {
//            HomeScreen(navController)
//        }


    }


}

