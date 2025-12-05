package com.wingspan.aimediahub.models

import android.graphics.Picture
import androidx.compose.ui.graphics.Color

data class OnBoardModel(
    val image: Int,
    val title: String,
    val description: String
)
data class LoginRequest(var email:String?,var password:String?)
data class ResponseData(var message:String?,var error:String?, var success:Boolean)
data class PageResponse( val data: List<PageItem>)
data class PageItem(
    val id: String,
    val name: String,
    val access_token: String,
    val picture: Picture1?          // NEW
)
data class Picture1(val data: PictureData)
data class PictureData(val url: String)
data class LongTokenResponse(
    val access_token: String,
    val token_type: String?,
    val expires_in: Long?
)

data class SocialAccount(
    val platform: String,       // "Facebook", "Instagram", etc.
    val connected: Boolean = false,
    val imageUrl: String? = null,
    val accessToken: String? = null, // store token for connected accounts
)

// ---------- Sample data models ----------
data class ScheduledPost(
    val id: Int,
    val time: String,
    val platform: String,
    val content: String,
    val color: Color
)
