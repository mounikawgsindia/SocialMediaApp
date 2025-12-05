package com.wingspan.aimediahub.networks

import com.wingspan.aimediahub.models.LoginRequest
import com.wingspan.aimediahub.models.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServices {

    //login
    @POST("user/login")
    suspend fun login(@Body request:LoginRequest): Response<ResponseData>
}