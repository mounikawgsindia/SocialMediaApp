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



    fun login(request: LoginRequest): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())
        Log.d("login","--")
        val response = apiService.login(request)
        if (response.isSuccessful) {
            Log.d("login",response.body().toString())
            if(response.body()!=null){
                emit(Resource.Success(response.body()!!))
            }else{
                emit(Resource.Error("Empty response body"))
            }

        } else {
            val errorBody = response.errorBody()
            val errorMsg = errorBody?.let {
                val errorStr = it.charStream().readText()
                Log.e("login", errorStr) // log raw JSON once
                try {
                    val errorJson = JSONObject(errorStr)
                    errorJson.optString("message", "Something went wrong")
                } catch (e: Exception) {
                    "Something went wrong"
                }
            } ?: "Something went wrong"
            Log.e("login","--${errorMsg}")
            emit(Resource.Error(errorMsg))
        }
    }.catch { e ->
        Log.e("login catch", "--${e.message}")

        val errorMsg = when (e) {
            is SocketTimeoutException -> "Request timed out. Please try again."
            is UnknownHostException -> "No internet connection. Please check your network."
            is IOException -> "Network error. Please check your connection."
            else -> e.message ?: "Unexpected error occurred"
        }

        emit(Resource.Error(errorMsg))
    }.flowOn(Dispatchers.IO)



     fun registration(request: RegisterRequest): Flow<Resource<ResponseData>> = flow {
         emit(Resource.Loading())
         Log.d("registration","--")
         val response = apiService.registration(request)
         if (response.isSuccessful) {
             Log.d("registration",response.body().toString())
             if(response.body()!=null){
                 emit(Resource.Success(response.body()!!))
             }else{
                 emit(Resource.Error("Empty response body"))
             }

         } else {
             val errorBody = response.errorBody()
             val errorMsg = errorBody?.let {
                 val errorStr = it.charStream().readText()
                 Log.e("registration", errorStr) // log raw JSON once
                 try {
                     val errorJson = JSONObject(errorStr)
                     errorJson.optString("msg", "Something went wrong")
                 } catch (e: Exception) {
                     "Something went wrong"
                 }
             } ?: "Something went wrong"
             Log.e("registration","--${errorMsg}")
             emit(Resource.Error(errorMsg))
         }
     }.catch { e ->
         Log.e("registration catch", "--${e.message}")

         val errorMsg = when (e) {
             is SocketTimeoutException -> "Request timed out. Please try again."
             is UnknownHostException -> "No internet connection. Please check your network."
             is IOException -> "Network error. Please check your connection."
             else -> e.message ?: "Unexpected error occurred"
         }

         emit(Resource.Error(errorMsg))
     }.flowOn(Dispatchers.IO)


     fun otpVerify(request: OtpVerifyRequest): Flow<Resource<ResponseData>> = flow {
         emit(Resource.Loading())
         Log.d("otpVerify","--")
         val response = apiService.verifyOtp(request)
         if (response.isSuccessful) {
             Log.d("otpVerify",response.body().toString())
             if(response.body()!=null){
                 emit(Resource.Success(response.body()!!))
             }else{
                 emit(Resource.Error("Empty response body"))
             }

         } else {
             val errorBody = response.errorBody()
             val errorMsg = errorBody?.let {
                 val errorStr = it.charStream().readText()
                 Log.e("otpVerify", errorStr) // log raw JSON once
                 try {
                     val errorJson = JSONObject(errorStr)
                     errorJson.optString("msg", "Something went wrong")
                 } catch (e: Exception) {
                     "Something went wrong"
                 }
             } ?: "Something went wrong"
             Log.e("otpVerify","--${errorMsg}")
             emit(Resource.Error(errorMsg))
         }
     }.catch { e ->
         Log.e("otpVerify catch", "--${e.message}")

         val errorMsg = when (e) {
             is SocketTimeoutException -> "Request timed out. Please try again."
             is UnknownHostException -> "No internet connection. Please check your network."
             is IOException -> "Network error. Please check your connection."
             else -> e.message ?: "Unexpected error occurred"
         }

         emit(Resource.Error(errorMsg))
     }.flowOn(Dispatchers.IO)



 }
