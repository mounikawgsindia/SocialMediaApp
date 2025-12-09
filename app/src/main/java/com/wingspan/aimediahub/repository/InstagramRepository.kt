package com.wingspan.aimediahub.repository

import com.wingspan.aimediahub.models.InstagramMedia
import com.wingspan.aimediahub.models.InstagramUserResponse
import com.wingspan.aimediahub.networks.InstagramApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstagramRepository @Inject constructor(
    private val api: InstagramApi
) {

    suspend fun getLongLivedToken(shortLivedToken: String, clientSecret: String): String {
        val response = api.getLongLivedToken(
            clientSecret = clientSecret,
            shortLivedToken = shortLivedToken
        )
        return response.accessToken
    }

    suspend fun getProfile(accessToken: String): InstagramUserResponse {
        return api.getUserProfile(accessToken = accessToken)
    }

    suspend fun getPosts(accessToken: String): List<InstagramMedia> {
        return api.getUserMedia(accessToken = accessToken).data
    }
}
