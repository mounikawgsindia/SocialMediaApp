package com.wingspan.aimediahub

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp :Application(){
    override fun onCreate() {
        super.onCreate()
        // Initialize Facebook SDK
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()
        AppEventsLogger.activateApp(this)
        //AppEventsLogger.activateApp(this)
    }
}