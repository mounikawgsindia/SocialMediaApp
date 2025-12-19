package com.wingspan.aimediahub.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.RequiresPermission

object NetworkUtils {

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @SuppressLint("ServiceCast")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // For Android 10+ you can also use NetworkCapabilities (optional)
        return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
    }
}
