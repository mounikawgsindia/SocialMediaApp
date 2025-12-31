package com.wingspan.aimediahub.models

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName
import com.wingspan.aimediahub.ui.theme.Sender
import kotlinx.parcelize.Parcelize
import java.util.Date

data class OnBoardModel(
    val image: Int,
    val title: String,
    val description: String
)
data class LoginRequest(var email:String?,var password:String?)
data class ResponseData(var msg:String?,var error:String?, var success:Boolean)
data class PageResponse( val pages: List<PageItem>)


data class PageItem(
    val providerId: String,
    val accessToken: String,
    val meta: PageMeta
)

data class PageMeta(
    val id: String,
    val name: String,
    val picture: String?
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
    val showDisconnectOnly: Boolean = false,val id: String="0",
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
data class DisconnectRequest(var userId:String)

data class ChatMessage(
    val text: String,
    val sender: Sender
)

//twitter class

data class TwitterPostResponse(
    val success: Boolean,
    val tweetId: String?,
    val tweetUrl: String?,
    val message: String?,
    val username: String?
)
data class TwitterConnectResponse(
    val success: Boolean,
    val connected: Boolean,
    val message: String?,
    val profile: TwitterProfile?
)

data class LinkedInProfileResponse(
    val success: Boolean,
    val connected: Boolean,
    val profile: LinkedInProfile
)

data class LinkedInProfile(
    val accessToken: String?,
    val tokenExpiresAt: String?,
    val loginPlatform: String?,
    val name: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val profileImage: String?,
    val linkedinId: String?,
    val userId: String?
)



data class TwitterProfile(
    val username: String,
    val id: String,
    val name: String,
    val profileImageUrl: String
)
data class PostBodyRequest(val userId:String,val content: String)
data class PublishPostResponse(
    val success: Boolean,
    val platform: String
)

data class PublishResult(
    val id: String
)

data class SelectableAccount(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val platform: String,
    var isSelected: Boolean = false
)

data class SocialAccount1(
    val id: String?,           // Facebook Page ID
    val name: String?,         // Page name
    val accessToken: String?,  // Page access token
    val imageUrl: String?,
    val platform:String?
    // Page profile image URL
)

data class PostsResponse(
    val success: Boolean,
    val posts: List<Post>
)
@SuppressLint("ParcelCreator")
@Parcelize
data class Post(
    val _id: String,
    val user: String,
    val platform: String,
    val pageId: String,
    val message: String,
    val mediaUrl: String?,     // null when no media
    val mediaType: String?,    // null when no media
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
): Parcelable

data class BlueskyLoginRequest(
    val identifier: String,
    val password: String
)

data class BlueskyLoginResponse(
    val accessJwt: String,
    val refreshJwt: String,
    val did: String,
    val handle: String
)
data class BlueskyProfileResponse(
    val did: String,
    val handle: String,
    val displayName: String?,
    val description: String?,
    val avatar: String?
)
data class BlueskyPostRequest(
    val text: String,
    val createdAt: String? = null,
    val embed: BlueskyEmbed? = null
)
data class CreateRecordRequest(
    val repo: String,
    val collection: String,
    val record: BlueskyPostRecord
)
data class BlueskyPostRecord(
    val text: String,
    val createdAt: String,
    val embed: BlueskyEmbed? = null
)

data class BlueskyEmbed(
    @SerializedName("\$type") // Maps Kotlin property to "$type" in JSON
    val type: String,         // "app.bsky.embed.images" or "app.bsky.embed.record"
    val images: List<BlueskyImage>? = null
)

data class BlueskyImage(
    val image: BlueskyBlobRef,      // CID from uploadBlob
    val alt: String? = null
)



data class BlueskyPostResponse(
    val uri: String,
    val cid: String,
    val author: String
)

data class BlueskyUploadResponse(
    val blob: BlueskyBlob
)

data class BlueskyBlob(
    val ref: BlueskyBlobRef,
    val mimeType: String,
    val size: Int,
)

data class BlueskyBlobRef(
    @SerializedName("\$link")
    val link: String
)
data class TelegramRequest(var userId:String?,val chatName:String?)
data class TelegramResponse(
    val success: Boolean,
    val message: String,
    val data: TelegramData
)

data class TelegramData(
    @SerializedName("_id")
    val id: String,
    val providerId: String,
    val user: String,
    val platform: String,
    @SerializedName("__v")
    val v: Int,
    val accessToken: String,
    val connectedFrom: String,
    val createdAt: Date,
    val meta: Meta,
    val scopes: List<String>
)

data class Meta(
    val username: String,
    val isAdmin: Boolean,
    val verifiedAt: Date
)