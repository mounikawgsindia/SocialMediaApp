package com.wingspan.aimediahub.utils


import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey




object SecureStorage {

    private const val PREFS_NAME = "secure_prefs"
    private const val TOKEN_KEY = "access_token"

    // Lazily initialize MasterKey using new builder
    private fun getMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM) // Modern key scheme
            .build()
    }

    private fun getEncryptedPrefs(context: Context): EncryptedSharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            getMasterKey(context),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    fun saveToken(context: Context, token: String) {
        getEncryptedPrefs(context).edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(context: Context): String? {
        return getEncryptedPrefs(context).getString(TOKEN_KEY, null)
    }

    fun clearToken(context: Context) {
        getEncryptedPrefs(context).edit().remove(TOKEN_KEY).apply()
    }
}
