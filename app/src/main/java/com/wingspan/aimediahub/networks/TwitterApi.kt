package com.wingspan.aimediahub.networks

import com.wingspan.aimediahub.models.TweetRequest
import com.wingspan.aimediahub.models.TweetResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TwitterApi1 {

    //twitter
    @POST("tweets")
    suspend fun postTweet(
        @Header("Authorization") bearer: String,
        @Body body: TweetRequest
    ): TweetResponse
}