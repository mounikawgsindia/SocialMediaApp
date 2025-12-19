package com.wingspan.aimediahub.ui.theme.bottonpages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wingspan.aimediahub.utils.BottomNavItem
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wingspan.aimediahub.utils.Prefs

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(rootNavController: NavHostController, prefs: Prefs) {

    val childNavController  = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(childNavController) }
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
                    rootNavController = rootNavController,prefs
                )
            }
//            composable(BottomNavItem.Create.route) {
//                CreateScreen(
//                    bottomNavController = childNavController,
//                    rootNavController = rootNavController
//                )
//            }
            composable(BottomNavItem.Calendar.route) {
                CalendarScreen( bottomNavController = childNavController,
                    rootNavController = rootNavController,prefs)
            }
            composable(BottomNavItem.Analytics.route) {
                AnalyticsScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(bottomNavController = childNavController,
                    rootNavController = rootNavController,prefs)
            }
        }
    }

}
