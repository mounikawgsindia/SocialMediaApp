package com.wingspan.aimediahub.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wingspan.aimediahub.models.InstagramMedia
import com.wingspan.aimediahub.models.InstagramUserResponse
import com.wingspan.aimediahub.models.LongLivedTokenResponse
import com.wingspan.aimediahub.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstagramViewModel @Inject constructor(private val repository: InstagramRepository) : ViewModel(){

    private val _profile = MutableLiveData<InstagramUserResponse>()
    val profile: LiveData<InstagramUserResponse> = _profile

    private val _posts = MutableLiveData<List<InstagramMedia>>()
    val posts: LiveData<List<InstagramMedia>> = _posts

    private val _tokenResponse = MutableLiveData<LongLivedTokenResponse>()
    val tokenResponse: LiveData<LongLivedTokenResponse> = _tokenResponse

    fun loadInstagramData(shortLivedToken: String, clientSecret: String) {
        viewModelScope.launch {
            try {
                val longLivedToken = repository.getLongLivedToken(shortLivedToken, clientSecret)

                Log.d("INSTAGRAM", "Long Token: $longLivedToken")
                _tokenResponse.value = longLivedToken as LongLivedTokenResponse?

                val profileData = repository.getProfile(longLivedToken.accessToken)
                Log.d("INSTAGRAM", "Profile: $profileData")
                _profile.value = profileData

                val postData = repository.getPosts(longLivedToken.accessToken)
                Log.d("INSTAGRAM", "Posts: $postData")
                _posts.value = postData

            } catch (e: Exception) {
                Log.e("INSTAGRAM_ERROR", e.message.toString())
            }
        }
    }
}