package com.wingspan.aimediahub.repository

import android.util.Log
import com.wingspan.aimediahub.models.DisconnectRequest
import com.wingspan.aimediahub.models.LinkedInProfileResponse
import com.wingspan.aimediahub.models.PageResponse
import com.wingspan.aimediahub.models.PostsResponse
import com.wingspan.aimediahub.models.PublishPostResponse
import com.wingspan.aimediahub.models.ResponseData
import com.wingspan.aimediahub.models.TwitterConnectResponse
import com.wingspan.aimediahub.networks.ApiServices
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class FacebookRepository @Inject constructor(private val apiCall:ApiServices,
   val pref:Prefs
) {

    //  FbPageData
    fun getFbPageData(): Flow<Resource<PageResponse>> =
        safeApiCall(
            apiCall = { apiCall.getFacebookPages(pref.getUserID().toString()) },
            errorKey = "message"
        )
    fun fbDisconnect(request: DisconnectRequest): Flow<Resource<ResponseData>> =
        safeApiCall(

            apiCall = { apiCall.fbDisconnect(request) },
            errorKey = "message"
        )
    fun publishFacebookPost(
        pageId: String,
        message: String,
        scheduleTime: String?, // optional
        mediaFile: File? = null
    ): Flow<Resource<PublishPostResponse>> = flow {

        emit(Resource.Loading())

        try {
            // Create RequestBody for text fields
            val pageIdBody = pageId.toRequestBody("text/plain".toMediaType())
            val userIdBody = pref.getUserID()?.toRequestBody("text/plain".toMediaType())
            val messageBody = message.toRequestBody("text/plain".toMediaType())
            val scheduleTimeBody = scheduleTime?.toRequestBody("text/plain".toMediaType())

            Log.d("PublishFBPost", "pageId: $pageId, userId: ${pref.getUserID()}, message: $message, scheduleTime: $scheduleTime")
            Log.d("PublishFBPost", "mediaFile: ${mediaFile?.absolutePath}")

            // Create MultipartBody.Part for media if exists
            val mediaPart = mediaFile?.let {
                val reqFile = it.asRequestBody("multipart/form-data".toMediaType())
                MultipartBody.Part.createFormData("media", it.name, reqFile)
            }

            val response = apiCall.publishFacebookPost(
                pageId = pageIdBody,
                userId = userIdBody,
                message = messageBody,
                scheduleTime = scheduleTimeBody,
                media = mediaPart
            )

            Log.d("PublishFBPost", "response code: ${response.code()}, response body: ${response.body()}")

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Log.d("PublishFBPost", "error: $errorMsg")
                emit(Resource.Error(errorMsg))
            }

        } catch (e: Exception) {
            Log.d("PublishFBPost", "exception: ${e.localizedMessage}")
            emit(Resource.Error(e.localizedMessage ?: "Something went wrong"))
        }

    }.flowOn(Dispatchers.IO)


    fun getPostedApi(): Flow<Resource<PostsResponse>> =
        safeApiCall(

            apiCall = { apiCall.getPostedApi(pref.getUserID().toString()) },
            errorKey = "message"
        )
    //twitter

    fun twitterProfile(): Flow<Resource<TwitterConnectResponse>> =
    safeApiCall(
        apiCall = { apiCall.twitterProfile(pref.getUserID().toString()) },
        errorKey = "message"
    )

    //linked
    fun linkedInProfile(): Flow<Resource<LinkedInProfileResponse>> =
        safeApiCall(
            apiCall = { apiCall.linkedInProfile(pref.getUserID().toString()) },
            errorKey = "message"
        )


    fun linkedDisconnect(request: DisconnectRequest): Flow<Resource<ResponseData>> =
        safeApiCall(

            apiCall = { apiCall.linkedInDisconnect(request) },
            errorKey = "message"
        )




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
}



