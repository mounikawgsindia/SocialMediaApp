package com.wingspan.aimediahub.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wingspan.aimediahub.models.SocialAccount
import com.wingspan.aimediahub.models.SocialAccount1
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

class Prefs @Inject constructor(@ApplicationContext context: Context) {

    private var prefs= context.getSharedPreferences("facebook_pref",Context.MODE_PRIVATE)
    fun saveLongToken(token: String) {
        prefs.edit().putString("LONG_TOKEN", token).apply()
    }
    private val gson = Gson()
    fun getLongToken(): String? {
        return prefs.getString("LONG_TOKEN", null)
    }

    // ----------------- Save all Facebook data -----------------
//    fun saveFacebookData(pageId: String, pageImage: String, access_token: String) {
//        prefs.edit().apply {
//            putString("FB_PAGE_ID", pageId)
//            putString("FB_PAGE_IMAGE", pageImage)
//            putString("FB_page_token",access_token)
//            apply()
//        }
//    }
    // ----------------- Save all Facebook pages -----------------
    fun saveFacebookPages(pages: List<SocialAccount1>) {
        val json = gson.toJson(pages)
        prefs.edit() { putString("FB_PAGES", json) }
    }
    fun saveTwitterAccounts(accounts: SocialAccount1) {
        val json = gson.toJson(accounts)
        prefs.edit() { putString("TWITTER_ACCOUNTS", json) }
    }

    fun saveInstagramAccounts(accounts: SocialAccount1) {
        val json = gson.toJson(accounts)
        prefs.edit() { putString("Insta_Account", json) }
    }


    fun getFacebookPages(): List<SocialAccount1> {
        val json = prefs.getString("FB_PAGES", null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<SocialAccount1>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun getTwitterAccount(): SocialAccount1? {
        val json = prefs.getString("TWITTER_ACCOUNTS", null)
        return if (!json.isNullOrEmpty()) gson.fromJson(json, SocialAccount1::class.java) else null
    }

    fun getInstaAccount(): SocialAccount1? {
        val json = prefs.getString("Insta_Account", null)
        return if (!json.isNullOrEmpty()) gson.fromJson(json, SocialAccount1::class.java) else null
    }
    // ----------------- Getters -----------------
    fun getfbpageToken(): String? = prefs.getString("FB_page_token", null)
    fun getFbPageId(): String? = prefs.getString("FB_PAGE_ID", null)
    fun getFbPageImage(): String? = prefs.getString("FB_PAGE_IMAGE", null)

    // ----------------- Clear Facebook data -----------------
    fun clearAllFacebookAccounts() {
        prefs.edit().apply {
            remove("FB_PAGE_ID")
            remove("FB_PAGE_IMAGE")
            remove("FB_page_token")
            remove("LONG_TOKEN")
            remove("FB_PAGES")   // ✅ MOST IMPORTANT LINE
            apply()
        }
    }
    fun clearTwitterAccounts() {
        prefs.edit().apply {
            remove("TWITTER_ACCOUNTS")
            apply()
        }
    }
    fun clearInstaAccounts() {
        prefs.edit().apply {
            remove("Insta_Account")
            apply()
        }
    }
}
