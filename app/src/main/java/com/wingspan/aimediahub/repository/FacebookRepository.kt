package com.wingspan.aimediahub.repository

import android.util.Log
import com.google.gson.Gson
import com.wingspan.aimediahub.models.DisconnectRequest
import com.wingspan.aimediahub.models.LinkedInProfileResponse
import com.wingspan.aimediahub.models.PageResponse
import com.wingspan.aimediahub.models.PostsResponse
import com.wingspan.aimediahub.models.PublishPostResponse
import com.wingspan.aimediahub.models.ResponseData
import com.wingspan.aimediahub.models.TelegramRequest
import com.wingspan.aimediahub.models.TelegramResponse
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
import javax.inject.Named

class FacebookRepository @Inject constructor(@Named("Backend")private val apiCall:ApiServices,
                                             @Named("BackendV2")private val api2call: ApiServices,
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
        pageIds: List<String>,
        message: String,
        times: List<String>,
        startDate: String,
        endDate: String,
        mediaFile: File? = null
    ): Flow<Resource<PublishPostResponse>> = flow {

        emit(Resource.Loading())

        try {
            // Convert List -> JSON array string
            val pageIdsJson = Gson().toJson(pageIds)
            val timesJson = Gson().toJson(times)

            // RequestBody creation
            val platformBody =
                "facebook".toRequestBody("text/plain".toMediaType())

            val userIdBody =
                pref.getUserID()!!.toRequestBody("text/plain".toMediaType())

            val messageBody =
                message.toRequestBody("text/plain".toMediaType())

            val pageIdsBody =
                pageIdsJson.toRequestBody("text/plain".toMediaType())

            val timesBody =
                timesJson.toRequestBody("text/plain".toMediaType())

            val startDateBody =
                startDate.toRequestBody("text/plain".toMediaType())

            val endDateBody =
                endDate.toRequestBody("text/plain".toMediaType())

            Log.d("PublishFBPost", """
            platform: facebook
            userId: ${pref.getUserID()}
            message: $message
            pageIds: $pageIdsJson
            times: $timesJson
            startDate: $startDate
            endDate: $endDate
        """.trimIndent())

            // Media part
            val mediaPart = mediaFile?.let {
                val reqFile = it.asRequestBody("image/*".toMediaType())
                MultipartBody.Part.createFormData(
                    "media",
                    it.name,
                    reqFile
                )
            }

            val response = apiCall.publishFacebookPost(
                platform = platformBody,
                userId = userIdBody,
                message = messageBody,
                pageIds = pageIdsBody,
                times = timesBody,
                startDate = startDateBody,
                endDate = endDateBody,
                media = mediaPart
            )

            Log.d(
                "PublishFBPost",
                "response code: ${response.code()}, body: ${response.body()}"
            )

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                emit(Resource.Error(errorMsg))
            }

        } catch (e: Exception) {
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

    fun twitterDisconnect(request: DisconnectRequest): Flow<Resource<ResponseData>> =
        safeApiCall(

            apiCall = { apiCall.twitterDisconnect(request) },
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

    //telegram
    fun telegramProfile(request:TelegramRequest): Flow<Resource<TelegramResponse>> =
        safeApiCall(
            apiCall = { api2call.telegramProfile(request) },
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
                    val json = JSONObject(errorStr)

                    // Priority-based error resolution
                    when {
                        json.has("telegramError") -> json.optString("telegramError")
                        json.has("error") -> json.optString("error")
                        json.has(errorKey) -> json.optString(errorKey)
                        else -> "Something went wrong"
                    }
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



