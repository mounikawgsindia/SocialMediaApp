package com.wingspan.aimediahub.ui.theme.bottonpages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wingspan.aimediahub.utils.BottomNavItem
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wingspan.aimediahub.ui.theme.AnalyticsScreen
import com.wingspan.aimediahub.utils.Prefs

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    rootNavController: NavHostController, prefs: Prefs,
    fbDeepLink: Boolean = false,
    twitterDeepLink: Boolean = false,
    linkedInDeepLink: Boolean
) {

    val childNavController  = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(childNavController) },
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->

        NavHost(
            navController = childNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Pass both NavControllers to bottom nav screens
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    bottomNavController = childNavController,
                    rootNavController = rootNavController,prefs,
                    fbDeepLink.toString(), twitterDeepLink.toString(),linkedInDeepLink.toString()
                )
            }

            composable(BottomNavItem.Calendar.route) {
                CalendarScreen( bottomNavController = childNavController,
                    rootNavController = rootNavController,prefs)
            }
            composable(BottomNavItem.Analytics.route) {
                AnalyticsScreen(
                    bottomNavController = childNavController,
                    rootNavController = rootNavController, prefs
                )
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(bottomNavController = childNavController,
                    rootNavController = rootNavController,prefs)
            }
        }
    }

}
