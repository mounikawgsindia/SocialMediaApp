package com.wingspan.aimediahub.networks

import com.wingspan.aimediahub.models.InstagramMediaResponse
import com.wingspan.aimediahub.models.InstagramUserResponse
import com.wingspan.aimediahub.models.LongLivedTokenResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface InstagramApi {

    // Exchange short-lived token for long-lived token
    @GET("access_token")
    suspend fun getLongLivedToken(
        @Query("grant_type") grantType: String = "ig_exchange_token",
        @Query("client_secret") clientSecret: String,
        @Query("access_token") shortLivedToken: String
    ): LongLivedTokenResponse

    // Get user profile info
    @GET("me")
    suspend fun getUserProfile(
        @Query("fields") fields: String = "id,username,account_type",
        @Query("access_token") accessToken: String
    ): InstagramUserResponse


    // 3. Get posts/media
    @GET("me/media")
    suspend fun getUserMedia(
        @Query("fields") fields: String = "id,caption,media_type,media_url,permalink,timestamp",
        @Query("access_token") accessToken: String
    ): InstagramMediaResponse



}