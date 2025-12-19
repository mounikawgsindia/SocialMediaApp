package com.wingspan.aimediahub.models

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName
import com.wingspan.aimediahub.ui.theme.Sender

data class OnBoardModel(
    val image: Int,
    val title: String,
    val description: String
)
data class LoginRequest(var email:String?,var password:String?)
data class ResponseData(var msg:String?,var error:String?, var success:Boolean)
data class PageResponse( val data: List<PageItem>)
data class FacebookPostResponse(
    val id: String
)

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
    val showDisconnectOnly: Boolean = false
)
data class SocialAccount1(
    val id: String,           // Facebook Page ID
    val name: String,         // Page name
    val accessToken: String,  // Page access token
    val imageUrl: String?,
   val platform:String?
    // Page profile image URL
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
    val account_type: String,
    val profile_picture_url:String,var followers_count:String,var follows_count :String,var media_count:String
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
data class TweetRequest(
    val text: String,
    val media: MediaRequest? = null
)
data class MediaRequest(
    val media_ids: List<String>
)

data class TweetResponse(
    val data: String?
)

data class TweetData(
    val id: String,
    val text: String
)
data class ApiResponse(
    val success: Boolean,
    val message: String?
)


data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)

data class OtpVerifyRequest(val email:String?,var otp:String)
data class AITextRequest(val prompt:String?)
data class LoginResponse(
    val msg: String,
    val token: String?,
    val userId: String?,
    val username:String?,
    val email:String?,
    val mobile:String?,
    val success: Boolean
)

data class ChatMessage(
    val text: String,
    val sender: Sender
)
