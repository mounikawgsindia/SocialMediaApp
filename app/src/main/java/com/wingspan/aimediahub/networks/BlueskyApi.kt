package com.wingspan.aimediahub.networks

import com.wingspan.aimediahub.models.BlueskyLoginRequest
import com.wingspan.aimediahub.models.BlueskyLoginResponse
import com.wingspan.aimediahub.models.BlueskyPostRequest
import com.wingspan.aimediahub.models.BlueskyPostResponse
import com.wingspan.aimediahub.models.BlueskyProfileResponse
import com.wingspan.aimediahub.models.BlueskyUploadResponse
import com.wingspan.aimediahub.models.CreateRecordRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface BlueskyApi {

    // LOGIN (Auth server)
    @POST("xrpc/com.atproto.server.createSession")
    suspend fun createSession(
        @Body body: BlueskyLoginRequest
    ): Response<BlueskyLoginResponse>

    // PROFILE (PDS)
    @GET("xrpc/app.bsky.actor.getProfile")
    suspend fun getProfile(
        @Query("actor") actorDid: String,
        @Header("Authorization") authorization: String
    ): Response<BlueskyProfileResponse>

    // ✅ CREATE POST (CORRECT)
    @POST("xrpc/com.atproto.repo.createRecord")
    suspend fun createPost(
        @Header("Authorization") authorization: String,
        @Body body: CreateRecordRequest
    ): Response<BlueskyPostResponse>

    // ✅ UPLOAD IMAGE (RAW BYTES)
    @POST("xrpc/com.atproto.repo.uploadBlob")
    suspend fun uploadBlob(
        @Header("Authorization") authorization: String,
        @Body body: RequestBody
    ): Response<BlueskyUploadResponse>
}

