package com.wingspan.aimediahub.networks

import com.wingspan.aimediahub.models.ApiResponse
import com.wingspan.aimediahub.models.InstagramMediaResponse
import com.wingspan.aimediahub.models.InstagramUserResponse
import com.wingspan.aimediahub.models.LongLivedTokenResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface InstagramApi {

    // Exchange short-lived token for long-lived token


    @GET("access_token")
    suspend fun getLongLivedToken(
        @Query("grant_type") grantType: String = "ig_exchange_token",
        @Query("client_secret") clientSecret: String,
        @Query("access_token") shortLivedToken: String
    ): LongLivedTokenResponse


    // Get user profile info with more fields
    @GET("me")
    suspend fun getUserProfile(
        @Query(
            "fields"
        ) fields: String = "id,username,account_type,profile_picture_url,biography,followers_count,follows_count,media_count,name",
        @Query("access_token") accessToken: String
    ): InstagramUserResponse



    // 3. Get posts/media
    @GET("me/media")
    suspend fun getUserMedia(
        @Query("fields") fields: String = "id,caption,media_type,media_url,permalink,timestamp",
        @Query("access_token") accessToken: String
    ): InstagramMediaResponse


    @Multipart
    @POST("instagram/post")
    suspend fun postToInstagram(
        @Part image: MultipartBody.Part,
        @Part("caption") caption: RequestBody
    ): ApiResponse


}