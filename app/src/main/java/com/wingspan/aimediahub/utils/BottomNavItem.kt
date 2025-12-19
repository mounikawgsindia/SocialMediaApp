package com.wingspan.aimediahub.utils

import com.wingspan.aimediahub.R

sealed class BottomNavItem ( val route: String,
                             val title: String,
                             val icon: Int){

    object Home : BottomNavItem("home", "Home", R.drawable.ic_home)
//    object Create : BottomNavItem("create", "Create", R.drawable.ic_create)
    object Calendar : BottomNavItem("calendar", "Calendar", R.drawable.ic_calendar)
    object Analytics : BottomNavItem("analytics", "Analytics", R.drawable.ic_analytics)
    object Profile : BottomNavItem("profile", "Profile", R.drawable.ic_profile)
}