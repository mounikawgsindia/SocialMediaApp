package com.wingspan.aimediahub.models

import android.graphics.Picture
import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName

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
data class HourSlot(
    val hour: Int,
    val posts: List<String> = emptyList()
)


data class LongLivedTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Long
)

data class InstagramUserResponse(
    val id: String,
    val username: String,
    val account_type: String
)

// User media/posts
data class InstagramMediaResponse(
    val data: List<InstagramMedia>
)

data class InstagramMedia(
    val id: String,
    val caption: String?,
    val media_type: String,
    val media_url: String,
    val permalink: String,
    val timestamp: String
)
data class InstagramBusinessResponse(
    @SerializedName("instagram_business_account")
    val instagramBusinessAccount: InstagramBusinessAccount?,

    @SerializedName("id")
    val id: String?
)

data class InstagramBusinessAccount(
    @SerializedName("id")
    val id: String
)