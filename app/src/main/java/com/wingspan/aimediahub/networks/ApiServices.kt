package com.wingspan.aimediahub.networks

import com.wingspan.aimediahub.models.AITextRequest
import com.wingspan.aimediahub.models.ChatMessage
import com.wingspan.aimediahub.models.DisconnectRequest
import com.wingspan.aimediahub.models.LinkedInProfileResponse
import com.wingspan.aimediahub.models.LoginRequest
import com.wingspan.aimediahub.models.LoginResponse
import com.wingspan.aimediahub.models.OtpVerifyRequest
import com.wingspan.aimediahub.models.PageResponse
import com.wingspan.aimediahub.models.PostBodyRequest
import com.wingspan.aimediahub.models.PostsResponse
import com.wingspan.aimediahub.models.PublishPostResponse
import com.wingspan.aimediahub.models.RegisterRequest
import com.wingspan.aimediahub.models.ResponseData
import com.wingspan.aimediahub.models.TwitterConnectResponse
import com.wingspan.aimediahub.models.TwitterPostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServices {

    //login
    @POST("user/login")
    suspend fun login(@Body request:LoginRequest): Response<LoginResponse>

    //registration
    @POST("user/register")
    suspend fun registration(@Body request:RegisterRequest): Response<ResponseData>

    //verify otp
    @POST("user/verify-otp")
    suspend fun verifyOtp(@Body request:OtpVerifyRequest): Response<ResponseData>

    //Ai text
    @POST("social/ai-generate")
    suspend fun sendMessage(@Body request:AITextRequest): Response<ChatMessage>

    //facebook api
    @GET("social/pages/{id}")
    suspend fun getFacebookPages(@Path("id") id:String): Response<PageResponse>

    @POST("social/facebook/disconnect")
    suspend fun fbDisconnect(@Body request:DisconnectRequest): Response<ResponseData>


//
//    @Multipart
//    @POST("social/publish/facebook")
//    suspend fun publishFacebookPost(
//        @Part("pageId") pageId: RequestBody,
//        @Part("userId") userId: RequestBody?,
//        @Part("message") message: RequestBody,
//        @Part("scheduleTime") scheduleTime: RequestBody?, // optional
//        @Part media: MultipartBody.Part?                   // image/file
//    ): Response<PublishPostResponse>




    @Multipart
    @POST("automation/publish")
    suspend fun publishFacebookPost(
        @Part("platform") platform: RequestBody,
        @Part("userId") userId: RequestBody,
        @Part("message") message: RequestBody,
        @Part("pageIds") pageIds: RequestBody,   // JSON array as String
        @Part("times") times: RequestBody,       // JSON array as String
        @Part("startDate") startDate: RequestBody,
        @Part("endDate") endDate: RequestBody,
        @Part media: MultipartBody.Part?          // image/file
    ): Response<PublishPostResponse>


    @GET("social/posts/{userId}")
    suspend fun getPostedApi(
        @Path("userId") userId: String
    ): Response<PostsResponse>


    //twitter apis
    //get twitterprofile
    @GET("api/twitter/profile")
    suspend fun twitterProfile(
        @Query("userId") id: String
    ): Response<TwitterConnectResponse> 

    @POST("api/twitter/post")
    suspend fun twitterPost(@Body request:PostBodyRequest): Response<TwitterPostResponse>

    //disconnect twitter
    @DELETE("api/twitter/disconnect")
    suspend fun twitterDisconnect(@Body request:DisconnectRequest): Response<ResponseData>

    //linked in apis
    @GET("api/linkedin/profile")
    suspend fun linkedInProfile(
        @Query("userId") id: String
    ): Response<LinkedInProfileResponse>

    @POST("social/linked/disconnect")
    suspend fun linkedInDisconnect(@Body request:DisconnectRequest): Response<ResponseData>

    @POST("api/linkedin/post")
    suspend fun linkedInPost(@Body request:PostBodyRequest): Response<TwitterPostResponse>
}