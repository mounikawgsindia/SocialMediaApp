package com.wingspan.aimediahub.repository

import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.wingspan.aimediahub.models.ApiResponse
import com.wingspan.aimediahub.models.InstagramMediaResponse
import com.wingspan.aimediahub.networks.FacebookApi
import com.wingspan.aimediahub.networks.InstagramApi
import com.wingspan.aimediahub.networks.TwitterApi1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class FacebookRepository @Inject constructor(
    private val api: FacebookApi,private val twitterApi:TwitterApi1,private val apiService: InstagramApi
) {
    var consumerKey="Mfq8g5XhT912JGh44zaZuG8jV"
    var consumerSecret="DiePtD8K8siK6nQWo0d1mu0PJug2ysGr4uX9FKw4tvJymtm1AN"


    suspend fun postMessage(pageId: String, message: String, pageToken: String) =
        api.postToPage(pageId, message, pageToken)

    suspend fun getFbPageData(userToken:String) =
        api.getPages(userToken = userToken)


    suspend fun getLongLiveToken(
        appId: String,
        secretKey: String,
        shortToken: String
    ) = api.getLongLivedToken(
        appId = appId,
        appSecret = secretKey,
        shortToken = shortToken
    )

    //twitter
//    suspend fun getUserProfile(
//        accessToken: String,
//        accessTokenSecret: String
//    ): String {
//        return withContext(Dispatchers.IO) {
//
//            val service = ServiceBuilder(consumerKey)
//                .apiSecret(consumerSecret)
//                .build(TwitterApi.instance())
//
//            val token = OAuth1AccessToken(accessToken, accessTokenSecret)
//
//            val request = OAuthRequest(
//                Verb.GET,
//                "https://api.twitter.com/1.1/account/verify_credentials.json"
//            )
//
//            service.signRequest(token, request)
//            val response = service.execute(request)
//
//            response.body ?: ""
//        }
//    }

    suspend fun getUserProfile(
        accessToken: String,
        accessTokenSecret: String
    ): String {
        return withContext(Dispatchers.IO) {

            val service = ServiceBuilder(consumerKey)
                .apiSecret(consumerSecret)
                .build(TwitterApi.instance())

            val token = OAuth1AccessToken(accessToken, accessTokenSecret)

            // New V2 endpoint
            val request = OAuthRequest(
                Verb.GET,
                "https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true"
            )

            service.signRequest(token, request)
            val response = service.execute(request)

            response.body ?: ""
        }
    }

    suspend fun postTweet(
        accessToken: String,
        accessSecret: String,
        text: String
    ): String {
        return withContext(Dispatchers.IO) {

            val service = ServiceBuilder(consumerKey)
                .apiSecret(consumerSecret)
                .build(TwitterApi.instance())

            val token = OAuth1AccessToken(accessToken, accessSecret)

            val request = OAuthRequest(
                Verb.POST,
                "https://api.twitter.com/2/tweets"
            )

            // v2 requires JSON body
            val jsonBody = """{ "text": "$text" }"""
            request.setPayload(jsonBody)
            request.addHeader("Content-Type", "application/json")

            service.signRequest(token, request)
            val response = service.execute(request)

            response.body ?: ""
        }
    }

    //instagram
    // Exchange short-lived token for long-lived token
//    suspend fun getLongLivedToken(
//        clientSecret: String,
//        shortLivedToken: String
//    ): LongLivedTokenResponse
//    {
//        return apiService.getLongLivedToken(
//            clientSecret = clientSecret,
//            shortLivedToken = shortLivedToken
//        )
//    }

    // instagram
    suspend fun getUserProfile(accessToken: String) =
        apiService.getUserProfile(accessToken = accessToken)

    // 🔹 Fetch Instagram Media (Posts)

    suspend fun postToInstagram(
        imageFile: File,
        caption: String
    ): ApiResponse {
        return try {

            // Convert image file → MultipartBody
            val requestImage = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestImage
            )

            // Caption text → RequestBody
            val captionBody = caption.toRequestBody("text/plain".toMediaTypeOrNull())

            // Call API
            apiService.postToInstagram(
                image = multipartBody,
                caption = captionBody
            )

        } catch (e: Exception) {
            ApiResponse(false, e.message ?: "Something went wrong")
        }
    }

    //all post data
    suspend fun getUserMedia(accessToken: String): InstagramMediaResponse {
        return apiService.getUserMedia(accessToken = accessToken)
    }

    data class TwitterUser(
        val id: String?,
        val screenName: String?,
        val name: String?,
        val profileImageUrl: String?
    )

}
