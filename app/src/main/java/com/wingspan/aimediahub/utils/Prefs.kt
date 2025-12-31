package com.wingspan.aimediahub.utils

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wingspan.aimediahub.models.SocialAccount
import com.wingspan.aimediahub.models.SocialAccount1
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit
import javax.inject.Singleton
import kotlin.toString
@Singleton
class Prefs @Inject constructor(@ApplicationContext private val  context: Context) {

    private var prefs= context.getSharedPreferences("facebook_pref",Context.MODE_PRIVATE)

    companion object {


        const val KEY_ID="keyid"
        const val KEY_EMAIL="email"
        const val KEY_NUMBER="number"
        const val KEY_USERNAME="username"
        const val IS_LOGGED_IN="isLoggedIn"

    }


    private val gson = Gson()


    fun isFirstTime(): Boolean{

        return prefs.getBoolean("KEY_IsFirstTime",true)
    }
    @SuppressLint("CommitPrefEdits")
    fun setFirstTime(){
        prefs.edit {
            putBoolean("KEY_IsFirstTime", false)
        }
    }

    fun saveLoginData(id:String,username:String,email:String,token:String,mobile:String){
        prefs.edit().apply(){
            putString(KEY_ID, id)
            putString(KEY_EMAIL, email)
            putString(KEY_USERNAME, username)
            putString(KEY_NUMBER, mobile)


            putBoolean(IS_LOGGED_IN, true)
            SecureStorage.saveToken(context,token.toString())
            apply()
        }
    }

    fun getUserID():String?=prefs.getString(KEY_ID, null)
    fun getUsername():String?=prefs.getString(KEY_USERNAME, null)
    fun getUserEmail():String?=prefs.getString(KEY_EMAIL, null)
    fun getUserNumber():String?=prefs.getString(KEY_NUMBER, null)


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

    fun saveLinkedInAccounts(accounts: SocialAccount1) {
        val json = gson.toJson(accounts)
        prefs.edit() { putString("LinkedIn_Account", json) }
    }

    fun saveTelegramAccounts(accounts: SocialAccount1) {
        val json = gson.toJson(accounts)
        prefs.edit() { putString("Telegram_Account", json) }
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

    fun getLinkedInAccount(): SocialAccount1? {
        val json = prefs.getString("LinkedIn_Account", null)
        return if (!json.isNullOrEmpty()) gson.fromJson(json, SocialAccount1::class.java) else null
    }
    fun getTelegramAccount(): SocialAccount1? {
        val json = prefs.getString("Telegram_Account", null)
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
    fun clearLinkedInAccounts() {
        prefs.edit().apply {
            remove("LinkedIn_Account")
            apply()
        }
    }
    fun clearTelegramAccounts() {
        prefs.edit().apply {
            remove("Telegram_Account")
            apply()
        }
    }
    fun clearInstaAccounts() {
        prefs.edit().apply {
            remove("Insta_Account")
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(IS_LOGGED_IN, false)
    @SuppressLint("CommitPrefEdits")
    fun logoutUser(){
        prefs.edit().apply {
            putBoolean(IS_LOGGED_IN, false)

            apply() // Commit changes
        }

    }
}
