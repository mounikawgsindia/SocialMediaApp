package com.wingspan.aimediahub.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wingspan.aimediahub.models.InstagramMediaResponse
import com.wingspan.aimediahub.models.InstagramUserResponse
import com.wingspan.aimediahub.models.LongLivedTokenResponse
import com.wingspan.aimediahub.models.SocialAccount1
import com.wingspan.aimediahub.models.TweetResponse
import com.wingspan.aimediahub.repository.FacebookRepository
import com.wingspan.aimediahub.repository.FacebookRepository.TwitterUser
import com.wingspan.aimediahub.utils.Prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class FacebookViewModel @Inject constructor(var repository:FacebookRepository , var prefs: Prefs): ViewModel() {


    private val _facebookaccounts = MutableStateFlow<List<SocialAccount1>>(emptyList())
    val facebookaccounts: StateFlow<List<SocialAccount1>> = _facebookaccounts


    private val _fbpostStatus = MutableStateFlow<String?>(null)
    val fbpostStatus: StateFlow<String?> = _fbpostStatus

    private val _twitterpostStatus = MutableStateFlow<String?>(null)
    val twitterpostStatus: StateFlow<String?> = _twitterpostStatus

    //twitter
    private val _twitterProfile = MutableStateFlow<SocialAccount1?>(null)
    val twitterProfile: StateFlow<SocialAccount1?> = _twitterProfile


    //instagram
    private val _instaProfile = MutableStateFlow<SocialAccount1?>(null)
    val instaProfile: StateFlow<SocialAccount1?> = _instaProfile

    private val _mediaInstaLiveData = MutableLiveData<InstagramMediaResponse>()
    val mediaInstaLiveData: LiveData<InstagramMediaResponse> = _mediaInstaLiveData

    val userProfileLiveData = MutableLiveData<InstagramUserResponse>()
    val errorLiveData = MutableLiveData<String>()
    init {
        _facebookaccounts.value = prefs.getFacebookPages()
        _twitterProfile.value=prefs.getTwitterAccount()

        _instaProfile.value=prefs.getInstaAccount()
        Log.d("instagram", "${prefs.getInstaAccount()}")
    }



    fun postToFacebook(pageId: String, pageToken: String, message: String) {
        Log.d("FB_POST", "data: pageId  ${pageId}...pageToken ${pageToken}...messGE...${message}")
        viewModelScope.launch {
            try {
                val response = repository.postMessage(pageId, message, pageToken)
                if (response.isSuccessful) {
                    val postId = response.body()?.toString()
                    _fbpostStatus.value = postId
                    Log.d("FB_POST", "Success: ${response.body()}")
                } else {
                    Log.e("FB_POST", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FB_POST", "Exception: ${e.message}")
            }
        }
    }

    //facebook page list
    fun getfbResponse() {
        viewModelScope.launch {
            val response = repository.getFbPageData(prefs.getLongToken().toString())

            if (response.isSuccessful) {
                val pages = response.body()?.data
                Log.d("FB_PAGE","response : viewmodel..${pages}")
                if (!pages.isNullOrEmpty()) {
                    // After fetching pages from API
                    val socialAccounts = pages.map { page ->
                        SocialAccount1(
                            id = page.id,
                            name = page.name,
                            accessToken = page.access_token.orEmpty(),
                            imageUrl = page.picture?.data?.url, platform = "facebook"
                        )
                    }
                    Log.d("FB_PAGE","pages : viewmodel..${socialAccounts}")
                    // Save all pages in Prefs
                    prefs.saveFacebookPages(socialAccounts)

                // Update StateFlow
                    _facebookaccounts.value = socialAccounts

                } else {
                    Log.d("FB_PAGE","pages : ${pages}...${response}")

                }
            } else {

            }
        }
    }


    //long token
    fun getLongLiveToken( appId: String,
                          secretKey: String,
                          shortToken: String) {
        viewModelScope.launch {
            val response = repository.getLongLiveToken(  appId = appId, secretKey,shortToken)

            if (response.isSuccessful) {

                var longToken=response.body()?.access_token
                // 🔥 Store token
                prefs.saveLongToken(longToken.toString())

                // 🔥 Now fetch pages with long token
                getfbResponse()
                    Log.d("FB_PAGE", "Page: ${response.body()?.access_token}")


            } else {
                Log.e("FB_PAGE", "Error: ${response.errorBody()?.string()}")
            }
        }
    }
    //twitter
    fun fetchUserProfile(accessToken: String, accessTokenSecret: String) {
        viewModelScope.launch {
            try {
                Log.d("TwitterViewModel", "Fetching user profile...")
                val json = repository.getUserProfile(accessToken, accessTokenSecret)
                Log.d("TwitterViewModel", "Raw JSON response: $json")

                // Parse JSON manually or using Gson
                val gson = com.google.gson.JsonParser.parseString(json).asJsonObject
                val user = TwitterUser(
                    id = gson["id_str"].asString,
                    screenName = gson["screen_name"].asString,
                    name = gson["name"].asString,
                    profileImageUrl = gson["profile_image_url_https"].asString
                )
                val twitterAccount = SocialAccount1(
                    id = user.id.toString(),
                    platform = "twitter",
                    name = user.name.toString(),
                    imageUrl = user.profileImageUrl,
                    accessToken= user.name.toString()
                )
                prefs.saveTwitterAccounts(twitterAccount)
                _twitterProfile.value = twitterAccount
                Log.d("TwitterViewModel", "Parsed user: $user")
            } catch (e: Exception) {

                Log.e("TwitterViewModel", "Error fetching user profile", e)
            }
        }
    }



    fun postTweet(userToken: String, userSecret: String, message: String) {
        viewModelScope.launch {
            try {
                val maskedToken = if (userToken.length > 10) "${userToken.take(10)}..." else userToken
                Log.d("TwitterPost", "Posting tweet with token: $maskedToken and message: $message")

                // Use repository function (OAuth1.0a)
                val response: String = repository.postTweet(userToken, userSecret, message)

                if (response.isNotEmpty()) {
                    Log.d("TwitterPost", "Tweet posted successfully: $response")
                  //  _tweetResult.postValue(TweetResponse(response.toString())) // wrap String in TweetResponse if needed
                    var data= TweetResponse(response.toString())
                    _twitterpostStatus.value=data.data.toString()
                } else {
                    Log.w("TwitterPost", "Failed to post tweet: empty response")
                    var data= TweetResponse(response.toString())
                    _twitterpostStatus.value=data.data.toString()
                   // _tweetResult.postValue(TweetResponse("Failed to post tweet"))
                }

            } catch (e: Exception) {
                Log.e("TwitterPost", "Error posting tweet", e)
              //  _tweetResult.postValue(TweetResponse("Error posting tweet: ${e.message}"))
            }
        }
    }


    //instagram




    fun fetchUserProfileInsta(accessToken: String) {
        viewModelScope.launch {
            try {
                val response = repository.getUserProfile(accessToken)
                userProfileLiveData.postValue(response)

                // Log the user profile
                Log.d(
                    "InstagramProfile",
                    "ID: ${response.id}, Username: ${response.username}, Account Type: ${response.account_type}"
                )

                val instaAccount = SocialAccount1(
                    id = response.id.toString(),
                    platform = "instagram",
                    name = response.username.toString(),
                    imageUrl = response.profile_picture_url,
                    accessToken= response.media_count.toString()
                )
                prefs.saveInstagramAccounts(instaAccount)
                _instaProfile.value = instaAccount

            } catch (e: Exception) {
                errorLiveData.postValue(e.message)
                Log.e("InstagramProfile", "Error fetching profile: ${e.message}")
            }
        }
    }

    // 🔹 Fetch user posts
    fun fetchUserMedia(accessToken: String) {
        viewModelScope.launch {
            try {
                val response = repository.getUserMedia(accessToken)
                _mediaInstaLiveData.postValue(response)

                Log.d("InstagramMedia", "Fetched ${response.data} posts")
            } catch (e: Exception) {
                errorLiveData.postValue(e.message)
                Log.e("InstagramMedia", "Error fetching media: ${e.message}")
            }
        }
    }

    fun setFbDisconnected() {
        _facebookaccounts.value = emptyList()
        prefs.clearAllFacebookAccounts() // if you're saving token/page id
    }
    fun setTwitterDisconnected(){
        prefs.clearTwitterAccounts()
        _twitterProfile.value= null
    }
    fun setInstagramDisconnected(){
        prefs.clearTwitterAccounts()
        _instaProfile.value= null
    }
}
