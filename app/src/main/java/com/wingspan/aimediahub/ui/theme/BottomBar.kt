package com.wingspan.aimediahub.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.wingspan.aimediahub.utils.BottomNavItem

@Composable
fun BottomBar (navController: NavHostController){

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Create,
        BottomNavItem.Calendar,
        BottomNavItem.Analytics,
        BottomNavItem.Profile
    )
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color.White) {

        items.forEach { item ->

            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                // ✅ NO RIPPLE EFFECT
                interactionSource = remember { MutableInteractionSource() },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                ),

                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // ✅ Blue indicator for selected
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(if (selected) 22.dp else 0.dp)
                                .background(Color(0xFF2979FF), RoundedCornerShape(10.dp))
                        )
                        Spacer(Modifier.height(5.dp))
                        // ✅ Icons keep their own colors
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = item.title,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )




                    }
                },
                label = {
                    Text(
                        text = item.title,

                        // ✅ IMAGE STYLE COLORS
                        color = when (item.route) {
                            "home" ->  Color(0xFF2563EB)
                            "create" -> Color(0xFF22C55E)
                            "calendar" ->  Color(0xFF22C55E)
                            "analytics" ->  Color(0xFF8B5CF6)
                            "profile" -> Color(0xFF60A5FA)
                            else -> Color.Gray
                        },
                        fontSize = 12.sp
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}