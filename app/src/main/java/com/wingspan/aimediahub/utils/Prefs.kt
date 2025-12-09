package com.wingspan.aimediahub.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Prefs @Inject constructor(@ApplicationContext context: Context) {

    private var prefs= context.getSharedPreferences("facebook_pref",Context.MODE_PRIVATE)
    fun saveLongToken(token: String) {
        prefs.edit().putString("LONG_TOKEN", token).apply()
    }

    fun getLongToken(): String? {
        return prefs.getString("LONG_TOKEN", null)
    }

    // ----------------- Save all Facebook data -----------------
    fun saveFacebookData(pageId: String, pageImage: String, access_token: String) {
        prefs.edit().apply {
            putString("FB_PAGE_ID", pageId)
            putString("FB_PAGE_IMAGE", pageImage)
            putString("FB_page_token",access_token)
            apply()
        }
    }
    // ----------------- Getters -----------------
    fun getfbpageToken(): String? = prefs.getString("FB_page_token", null)
    fun getFbPageId(): String? = prefs.getString("FB_PAGE_ID", null)
    fun getFbPageImage(): String? = prefs.getString("FB_PAGE_IMAGE", null)

    // ----------------- Clear Facebook data -----------------
    fun clearFacebookData() {
        prefs.edit().apply {
            remove("FB_PAGE_ID")
            remove("FB_PAGE_IMAGE")
            remove("LONG_TOKEN")
            apply()
        }
    }
}
