package com.wingspan.aimediahub.repository

import com.wingspan.aimediahub.networks.FacebookApi
import javax.inject.Inject

class FacebookRepository @Inject constructor(
    private val api: FacebookApi
) {
    suspend fun postMessage(pageId: String, message: String, pageToken: String) =
        api.postToPage(pageId, message, pageToken)

    suspend fun getFbPageData(userToken:String) =
        api.getPages(userToken = userToken)


    suspend fun getLongLiveToken(
        appId: String,
        secretKey: String,
        shortToken: String
    ) = api.getLongLivedToken(
        appId = appId,
        appSecret = secretKey,
        shortToken = shortToken
    )
}
