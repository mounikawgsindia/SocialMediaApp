package com.wingspan.aimediahub.repository

import android.content.Context
import android.util.Log
import com.wingspan.aimediahub.models.LoginRequest
import com.wingspan.aimediahub.models.LoginResponse
import com.wingspan.aimediahub.models.OtpVerifyRequest
import com.wingspan.aimediahub.models.RegisterRequest
import com.wingspan.aimediahub.models.ResponseData
import com.wingspan.aimediahub.networks.ApiServices
import com.wingspan.aimediahub.utils.Resource

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

import javax.inject.Inject
import kotlin.io.readText
import kotlin.let
import kotlin.toString

 class AuthRepository @Inject constructor(
    private val apiService: ApiServices
) {

     /**
      * Common API call handler
      */

         private inline fun <reified T : Any> safeApiCall(
             crossinline apiCall: suspend () -> retrofit2.Response<T>,
             errorKey: String = "message"
         ): Flow<Resource<T>> = flow {

         emit(Resource.Loading())

         val response = apiCall()

         if (response.isSuccessful) {
             val body = response.body()
             if (body != null) {
                 emit(Resource.Success(body))
             } else {
                 emit(Resource.Error("Empty response body"))
             }
         } else {
             val errorMsg = response.errorBody()?.let {
                 val errorStr = it.charStream().readText()
                 Log.e("API_ERROR", errorStr)
                 try {
                     JSONObject(errorStr).optString(errorKey, "Something went wrong")
                 } catch (e: Exception) {
                     "Something went wrong"
                 }
             } ?: "Something went wrong"

             emit(Resource.Error(errorMsg))
         }

     }.catch { e ->
         val errorMsg = when (e) {
             is SocketTimeoutException ->
                 "Request timed out. Please try again."
             is UnknownHostException ->
                 "No internet connection. Please check your network."
             is IOException ->
                 "Network error. Please check your connection."
             else ->
                 e.message ?: "Unexpected error occurred"
         }

         Log.e("API_CATCH", errorMsg)
         emit(Resource.Error(errorMsg))

     }.flowOn(Dispatchers.IO)

     // 🔹 LOGIN
     fun login(request: LoginRequest): Flow<Resource<LoginResponse>> =
         safeApiCall(
             apiCall = { apiService.login(request) },
             errorKey = "message"
         )

     // 🔹 REGISTRATION
     fun registration(request: RegisterRequest): Flow<Resource<ResponseData>> =
         safeApiCall(
             apiCall = { apiService.registration(request) },
             errorKey = "msg"
         )

     // 🔹 OTP VERIFY
     fun otpVerify(request: OtpVerifyRequest): Flow<Resource<ResponseData>> =
         safeApiCall(
             apiCall = { apiService.verifyOtp(request) },
             errorKey = "msg"
         )
 }
