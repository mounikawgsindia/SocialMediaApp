package com.wingspan.aimediahub.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.wingspan.aimediahub.utils.BottomNavItem
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wingspan.aimediahub.ui.theme.bottonpages.AnalyticsScreen
import com.wingspan.aimediahub.ui.theme.bottonpages.CalendarScreen
import com.wingspan.aimediahub.ui.theme.bottonpages.HomeScreen
import com.wingspan.aimediahub.ui.theme.bottonpages.ProfileScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable("home") { HomeScreen(navController) }
            composable("create") { CreateScreen(navController) }
            composable("calendar") { CalendarScreen() }
            composable("analytics") { AnalyticsScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}
