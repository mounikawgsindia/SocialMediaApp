package com.wingspan.aimediahub.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wingspan.aimediahub.models.BlueskyBlob
import com.wingspan.aimediahub.models.BlueskyBlobRef
import com.wingspan.aimediahub.models.BlueskyEmbed
import com.wingspan.aimediahub.models.BlueskyImage
import com.wingspan.aimediahub.models.BlueskyLoginRequest
import com.wingspan.aimediahub.models.BlueskyPostRecord
import com.wingspan.aimediahub.models.BlueskyPostRequest
import com.wingspan.aimediahub.models.CreateRecordRequest
import com.wingspan.aimediahub.networks.BlueskyApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.Instant
import javax.inject.Inject


@HiltViewModel
class BlueskyViewModel @Inject constructor(
    private val blueskyApi: BlueskyApi
) : ViewModel() {

    private val TAG = "BlueskyVM"
    var accessJwt ="eyJ0eXAiOiJhdCtqd3QiLCJhbGciOiJFUzI1NksifQ.eyJzY29wZSI6ImNvbS5hdHByb3RvLmFjY2VzcyIsInN1YiI6ImRpZDpwbGM6b3RxazQ1dGZvdWU1MnozbHl1Z2Z4MmliIiwiaWF0IjoxNzY3MTc0NjIxLCJleHAiOjE3NjcxODE4MjEsImF1ZCI6ImRpZDp3ZWI6ZW50b2xvbWEudXMtd2VzdC5ob3N0LmJza3kubmV0d29yayJ9.RdMr6OeQFoNinBkoPrBs59kFnqV2A96gMDSLND6Gjkrk_pH7E4x3-2hh2njW8bUVnWBOZpScZTig3hZU1cL9_w"
    var actorDid="did:plc:otqk45tfoue52z3lyugfx2ib"
    fun login() {
        val username = "mounika90.bsky.social"
        val password = "Mounika@1990"
        viewModelScope.launch {
            try {
                // 1️⃣ LOGIN
                val loginResponse = blueskyApi.createSession(
                    BlueskyLoginRequest(username, password)
                )

                if (!loginResponse.isSuccessful) {
                    Log.e(TAG, "Login failed: ${loginResponse.code()}")
                    return@launch
                }

                val session = loginResponse.body() ?: return@launch
                Log.d(TAG, "Login success: ${session.handle}")

                // 2️⃣ GET PROFILE
                val profileResponse = blueskyApi.getProfile(
                    actorDid = session.did,
                    authorization = "Bearer ${session.accessJwt}"

                )
                accessJwt=session.accessJwt
                Log.d(TAG, "f token ${accessJwt}...${session.did}")
                if (profileResponse.isSuccessful) {
                    val profile = profileResponse.body()
                    Log.d(TAG, "Profile Loaded ✅")
                    Log.d(TAG, "Name: ${profile?.displayName}")
                    Log.d(TAG, "Handle: ${profile?.handle}")
                    Log.d(TAG, "Bio: ${profile?.description}")
                    Log.d(TAG, "Avatar: ${profile?.avatar}")
                } else {
                    Log.e(TAG, "Profile failed: ${profileResponse.code()}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.localizedMessage}", e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createPostWithImage(
        context: Context,
        text: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                if (imageUri == null) {
                    // Text-only post
                    createPost(text, null)
                    return@launch
                }

                // 1️⃣ Convert Uri → File
                val file = uriToFile(context, imageUri)
                if (file == null) {
                    Log.e(TAG, "File conversion failed")
                    return@launch
                }

                // 2️⃣ Upload image → get BlueskyBlob
                val uploadedBlob = uploadImage(file)
                if (uploadedBlob == null) {
                    Log.e(TAG, "Image upload failed")
                    return@launch
                }

                // 3️⃣ Build embed with proper blob ref
                val blobRef = BlueskyBlobRef(link = uploadedBlob.ref.link)
                val image = BlueskyImage(image = blobRef, alt = "Image")
                val embed = BlueskyEmbed(type = "app.bsky.embed.images", images = listOf(image))

                // 4️⃣ Pass embed (BlueskyEmbed), not blob
                createPost(text, embed)

            } catch (e: Exception) {
                Log.e(TAG, "createPostWithImage error", e)
            }
        }
    }

    /**
     * Upload image to Bluesky → returns CID
     */
    private suspend fun uploadImage(file: File): BlueskyBlob? {
        val requestBody = file.asRequestBody("image/jpeg".toMediaType())

        val response = blueskyApi.uploadBlob(
            authorization = "Bearer $accessJwt",
            body = requestBody
        )

        return response.body()?.blob
    }


    /**
     * Uri → File
     */
    private fun uriToFile(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, "bsky_${System.currentTimeMillis()}.jpg")

        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createPost(text: String, embed: BlueskyEmbed? = null) {
        viewModelScope.launch {
            try {
                val record = BlueskyPostRecord(
                    text = text,
                    createdAt = Instant.now().toString(),
                    embed = embed
                )

                val request = CreateRecordRequest(
                    repo = actorDid,
                    collection = "app.bsky.feed.post",
                    record = record
                )

                val response = blueskyApi.createPost(
                    authorization = "Bearer $accessJwt",
                    body = request
                )

                if (response.isSuccessful) {
                    Log.d(TAG, "Post created ✅ URI: ${response.body()?.uri}")
                } else {
                    Log.e(TAG, "Post failed ❌ Code: ${response.code()}")
                    Log.e(TAG, "Error: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Create post exception ❌", e)
            }
        }
    }



}
