package com.wingspan.aimediahub.networks

import com.google.gson.JsonObject
import com.wingspan.aimediahub.models.LongTokenResponse
import com.wingspan.aimediahub.models.PageResponse
import retrofit2.Response
import retrofit2.http.*

interface FacebookApi {

    //post in fb page
    @FormUrlEncoded
    @POST("{pageId}/feed")
    suspend fun postToPage(
        @Path("pageId") pageId: String,
        @Field("message") message: String,
        @Field("access_token") accessToken: String
    ): Response<JsonObject>

// facebook pages data
    @GET("me/accounts")
    suspend fun getPages(
        @Query("fields") fields: String = "id,name,picture.type(large)",
        @Query("access_token") userToken: String
    ): Response<PageResponse>

    // long lived token
    @GET("oauth/access_token")
    suspend fun getLongLivedToken(
        @Query("grant_type") grantType: String = "fb_exchange_token",
        @Query("client_id") appId: String,
        @Query("client_secret") appSecret: String,
        @Query("fb_exchange_token") shortToken: String
    ): Response<LongTokenResponse>
}