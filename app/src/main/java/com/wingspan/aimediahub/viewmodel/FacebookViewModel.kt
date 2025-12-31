package com.wingspan.aimediahub.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wingspan.aimediahub.models.DisconnectRequest
import com.wingspan.aimediahub.models.InstagramMediaResponse
import com.wingspan.aimediahub.models.InstagramUserResponse
import com.wingspan.aimediahub.models.LongLivedTokenResponse
import com.wingspan.aimediahub.models.Post
import com.wingspan.aimediahub.models.PostBodyRequest
import com.wingspan.aimediahub.models.SocialAccount1
import com.wingspan.aimediahub.models.TelegramRequest
import com.wingspan.aimediahub.models.TweetResponse
import com.wingspan.aimediahub.repository.FacebookRepository
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.utils.Resource
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDate
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class FacebookViewModel @Inject constructor(@ApplicationContext private val  context: Context, var repository:FacebookRepository, var prefs: Prefs): ViewModel() {


    //facebook
    private val _fbLoading = MutableStateFlow(false)
    val fbLoading: StateFlow<Boolean> = _fbLoading

    private val _facebookaccounts = MutableStateFlow<List<SocialAccount1>>(emptyList())
    val facebookaccounts: StateFlow<List<SocialAccount1>> = _facebookaccounts

    private val _fbDisStatus = MutableStateFlow<String?>(null)
    val fbDisStatus: StateFlow<String?> = _fbDisStatus

    // StateFlow for errors
    private val _facebookError = MutableStateFlow<String?>(null)
    val facebookError: StateFlow<String?> = _facebookError

    private val _fbpostStatus = MutableStateFlow<Boolean?>(null)
    val fbpostStatus: StateFlow<Boolean?> = _fbpostStatus

    private val _fbgetPostStatus =MutableStateFlow<List<Post>?>(null)
    val fbgetPostStatus: StateFlow<List<Post>?> = _fbgetPostStatus

    //twitter

    private val _twitterpostStatus = MutableStateFlow<String?>(null)
    val twitterpostStatus: StateFlow<String?> = _twitterpostStatus

    //twitter
    private val _twitterProfile = MutableStateFlow<SocialAccount1?>(null)
    val twitterProfile: StateFlow<SocialAccount1?> = _twitterProfile

    // Holds error messages
    private val _twitterError = MutableStateFlow<String?>(null)
    val twitterError: StateFlow<String?> = _twitterError

    private val _twitterDisStatus = MutableStateFlow<String?>(null)
    val twitterDisStatus: StateFlow<String?> = _twitterDisStatus

    //instagram
    private val _instaProfile = MutableStateFlow<SocialAccount1?>(null)
    val instaProfile: StateFlow<SocialAccount1?> = _instaProfile

    //linkedIn
    private val _linkedInProfile = MutableStateFlow<SocialAccount1?>(null)
    val linkedInProfile: StateFlow<SocialAccount1?> = _linkedInProfile

    private val _linkedDisStatus = MutableStateFlow<String?>(null)
    val linkedDisStatus: StateFlow<String?> = _linkedDisStatus

    private val _linkedInError = MutableStateFlow<String?>(null)
    val linkedInError: StateFlow<String?> = _linkedInError


    private val _linkedInpostStatus = MutableStateFlow<Boolean?>(null)
    val linkedInpostStatus: StateFlow<Boolean?> = _linkedInpostStatus

    private val _mediaInstaLiveData = MutableLiveData<InstagramMediaResponse>()
    val mediaInstaLiveData: LiveData<InstagramMediaResponse> = _mediaInstaLiveData

    val userProfileLiveData = MutableLiveData<InstagramUserResponse>()
    val errorLiveData = MutableLiveData<String>()

    //linkedIn
    private val _telegramProfile = MutableStateFlow<SocialAccount1?>(null)
    val telegramProfile: StateFlow<SocialAccount1?> = _telegramProfile
    private val _telegramError = MutableStateFlow<String?>(null)
    val telegramError: StateFlow<String?> = _telegramError

    init {
        _facebookaccounts.value = prefs.getFacebookPages()
        _twitterProfile.value = prefs.getTwitterAccount()
        _linkedInProfile.value=prefs.getLinkedInAccount()
        _telegramProfile.value=prefs.getTelegramAccount()
        _instaProfile.value = prefs.getInstaAccount()
        Log.d("twitter viewmodel", "${prefs.getTwitterAccount()}")
    }


    //facebook api

    fun getFbPages() {
        viewModelScope.launch {
            repository.getFbPageData().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Optional: show loading
                    }
                    is Resource.Success -> {
                        val pageResponse = resource.data
                        Log.d("FB_PAGE", "response : viewmodel..$pageResponse")

                        pageResponse?.pages?.let { pageList ->
                            val socialAccounts = pageList.map { page ->
                                SocialAccount1(
                                    id = page.meta.id,
                                    name = page.meta.name,
                                    accessToken = page.accessToken,
                                    imageUrl = page.meta.picture,
                                    platform = "facebook"
                                )
                            }

                            Log.d("FB_PAGE", "pages : viewmodel..$socialAccounts")

                            // Save all pages in Prefs
                            prefs.saveFacebookPages(socialAccounts)

                            // Update StateFlow
                            _facebookaccounts.value = socialAccounts
                        }

                        _facebookError.value = null // clear previous errors
                    }
                    is Resource.Error -> {
                        val errorMsg = resource.message ?: "Unknown error"
                        _facebookError.value = errorMsg
                        Log.e("FB_PAGE_ERROR", errorMsg)
                    }
                }
            }
        }
    }




    fun setFbDisconnected() {
        viewModelScope.launch {
            repository.fbDisconnect(DisconnectRequest(prefs.getUserID().toString())).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                      //  _fbLoading.value = true
                    }
                    is Resource.Success -> {
                        _fbDisStatus.value = resource.data?.msg
                        _facebookaccounts.value = emptyList()
                        prefs.clearAllFacebookAccounts() // clear saved tokens/page IDs
                    }
                    is Resource.Error -> {
                        // Error already comes from repository
                        _fbDisStatus.value = resource.message // show error to UI
                        Log.e("FB_DISCONNECT_ERROR", resource.message ?: "Unknown error")
                    }
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun publishFacebookPost(
        pageIds: List<String>,        // multiple pages support
        message: String,
        times: List<String>,          // multiple schedule times
        startDate: String,            // required
        endDate: String,              // required
        imageUri: Uri?
    ) {
        viewModelScope.launch {

            val TAG = "PublishFBPost"

            // reset before API so old success doesn't re-trigger
            _fbpostStatus.value = null
            Log.d(TAG, "API call started")

            // Convert Uri → File (only here)
            val imageFile: File? = imageUri?.let {
                uriToFile(context, it)
            }

            Log.d(
                TAG,
                "Request data → pageIds=$pageIds, message=$message, times=$times, " +
                        "startDate=$startDate, endDate=$endDate, " +
                        "imageFile=${imageFile?.absolutePath ?: "NO_IMAGE"}"
            )

            // Safety defaults (backend requires these)
            val safeStartDate = startDate.ifBlank {
                LocalDate.now().toString()
            }

            val safeEndDate = endDate.ifBlank {
                LocalDate.now().plusDays(1).toString()
            }

            Log.d(TAG, "Final dates → startDate=$safeStartDate, endDate=$safeEndDate")

            repository.publishFacebookPost(
                pageIds = pageIds,
                message = message,
                times = times,
                startDate = safeStartDate,
                endDate = safeEndDate,
                mediaFile = imageFile
            ).collect { resource ->
                when (resource) {

                    is Resource.Loading -> {
                        Log.d(TAG, "Loading...")
                    }

                    is Resource.Success -> {
                        Log.d(
                            TAG,
                            "Success → response=${resource.data}"
                        )
                        _fbpostStatus.value = resource.data?.success == true
                    }

                    is Resource.Error -> {
                        Log.e(
                            TAG,
                            "Error → ${resource.message}"
                        )
                        _fbpostStatus.value = false
                    }
                }
            }
        }
    }




    //get posts
    fun getPostedApi(){
        viewModelScope.launch {
            Log.d("facebook get  posts","--${prefs.getUserID()}")
            repository.getPostedApi().collect {result ->


                when (result) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        var responce=result.data?.posts
                        _fbgetPostStatus.value=responce ?: emptyList()
                        Log.d("facebook get  posts",
                            (responce ?: "Unknown error").toString()
                        )
                    }
                    is Resource.Error -> {
                        _facebookError.value=result.message ?: "Unknown error"
                        Log.e("facebook get posts profile", result.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    //twitter
    @SuppressLint("SuspiciousIndentation")
    fun twitterProfile() {
        Log.d("twitter profile",
          "twitterProfile"
        )
        viewModelScope.launch {
            repository.twitterProfile().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {

                        resource.data?.profile?.let{
                          val socialAccounts= SocialAccount1(
                              id = it?.id.toString(),
                              name =it?.name.toString(),
                              accessToken = it?.username.toString(),
                              imageUrl =it?.profileImageUrl ,
                              platform ="twitter"
                          )
                            Log.d("twitter profile",
                                resource.data?.profile?.profileImageUrl ?: ""
                            )
                            Log.d("twitter profile",
                                prefs.getUserID().toString()
                            )
                            _twitterProfile.value=socialAccounts
                            prefs.saveTwitterAccounts(socialAccounts)
                            Log.d("twitter profile", "${socialAccounts}")
                        }?:run{
                            _twitterError.value="Issue with twitter Login and Profile data"
                        }
                    }
                    is Resource.Error -> {
                        // Error already comes from repository
                        _twitterError.value = resource.message ?: "Unknown error"
                        Log.e("twitter profile", resource.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun setTwitterDisconnected() {
        viewModelScope.launch {
            repository.twitterDisconnect(DisconnectRequest(prefs.getUserID().toString())).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        //  _fbLoading.value = true
                    }
                    is Resource.Success -> {
                        _twitterDisStatus.value = resource.data?.msg
                        prefs.clearTwitterAccounts()
                        _twitterProfile.value = null
                    }
                    is Resource.Error -> {
                        // Error already comes from repository
                        _twitterError.value = resource.message // show error to UI
                        Log.e("FB_DISCONNECT_ERROR", resource.message ?: "Unknown error")
                    }
                }
            }
        }

    }

    // linked in profile
    fun linkedinProfile() {
        viewModelScope.launch {
            repository.linkedInProfile().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {

                        resource.data?.profile?.let{
                            val socialAccounts= SocialAccount1(
                                id = it.linkedinId.toString(),
                                name =it.name.toString(),
                                accessToken = it.accessToken.toString(),
                                imageUrl =it.profileImage ,
                                platform ="linkedin"
                            )
                            _linkedInProfile.value=socialAccounts
                            prefs.saveLinkedInAccounts(socialAccounts)
                        }
                        Log.d("linked in profile", resource.data?.profile!!.profileImage.toString())
                    }
                    is Resource.Error -> {
                        // Error already comes from repository
                        _linkedInError.value = resource.message ?: "Unknown error"
                        Log.e("linked in profile", resource.message ?: "Unknown error")
                    }
                }
            }
        }
    }



    fun setLinkedDisconnected() {
        viewModelScope.launch {
            repository.linkedDisconnect(DisconnectRequest(prefs.getUserID().toString())).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        //  _fbLoading.value = true
                    }
                    is Resource.Success -> {
                        _linkedDisStatus.value = resource.data?.msg
                        prefs.clearLinkedInAccounts()
                        _linkedInProfile.value = null
                    }
                    is Resource.Error -> {
                        // Error already comes from repository
                        _linkedInError.value = resource.message // show error to UI
                        Log.e("FB_DISCONNECT_ERROR", resource.message ?: "Unknown error")
                    }
                }
            }
        }

    }
    fun setTelegramDisconnected () {

        prefs.clearTelegramAccounts()
        _telegramProfile.value = null

    }

        //telegram

    fun telegramProfile(chatName:String) {
        var request= TelegramRequest(prefs.getUserID(),chatName)
        Log.d("telegram in request", "$request")
        viewModelScope.launch {
            repository.telegramProfile(request).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {

                        resource.data?.data?.let{
                            val socialAccounts= SocialAccount1(
                                id = "",
                                name =it.meta.username.toString(),
                                accessToken = it.accessToken.toString(),
                                imageUrl ="" ,
                                platform ="telegram"
                            )
                            _telegramProfile.value=socialAccounts
                            prefs.saveTelegramAccounts(socialAccounts)
                        }
                        Log.d("telegram in profile", resource.data?.data?.meta!!.username)
                    }
                    is Resource.Error -> {
                        // Error already comes from repository
                        _telegramError.value = resource.message?: "Unknown error"
                        Log.e("telegram in profile", resource.message ?: "Unknown error")
                    }
                }
            }
        }
    }


    fun setInstagramDisconnected() {
        prefs.clearTwitterAccounts()
        _instaProfile.value = null
    }

    fun clearFbPostStatus() {
        _fbpostStatus.value = null
    }

    fun uriToFile(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val fileName = "upload_${System.currentTimeMillis()}.jpg"
        val tempFile = File(context.cacheDir, fileName)

        contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }


}