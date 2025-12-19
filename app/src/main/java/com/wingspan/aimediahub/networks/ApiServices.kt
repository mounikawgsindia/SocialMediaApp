package com.wingspan.aimediahub.networks

import com.wingspan.aimediahub.models.AITextRequest
import com.wingspan.aimediahub.models.ChatMessage
import com.wingspan.aimediahub.models.LoginRequest
import com.wingspan.aimediahub.models.LoginResponse
import com.wingspan.aimediahub.models.OtpVerifyRequest
import com.wingspan.aimediahub.models.RegisterRequest
import com.wingspan.aimediahub.models.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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
}