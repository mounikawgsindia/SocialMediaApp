package com.wingspan.aimediahub.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wingspan.aimediahub.models.SocialAccount
import com.wingspan.aimediahub.repository.FacebookRepository
import com.wingspan.aimediahub.utils.Prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FacebookViewModel @Inject constructor(var repository:FacebookRepository , var prefs: Prefs): ViewModel() {


    private val _accounts = MutableStateFlow<List<SocialAccount>>(emptyList())
    val accounts: StateFlow<List<SocialAccount>> = _accounts

    private val _fbConnected = MutableStateFlow(false)
    val fbConnected = _fbConnected.asStateFlow()

    // State for Facebook Page profile image URL
    private val _fbPageImage = MutableStateFlow<String?>(null)
    val fbPageImage: StateFlow<String?> = _fbPageImage

    fun setFbConnected(value: Boolean) {
        _fbConnected.value = value
    }

    init {
        restoreFacebookData()
    }

    private fun restoreFacebookData() {
        val pageImage = prefs.getFbPageImage()
        val longToken = prefs.getfbpageToken()
        _fbConnected.value = !longToken.isNullOrEmpty()  // connected if token exists
        _fbPageImage.value = pageImage
    }

    fun postToFacebook(pageId: String, pageToken: String, message: String) {
        Log.d("FB_POST", "data: pageId  ${pageId}...pageToken ${pageToken}...messGE...${message}")
        viewModelScope.launch {
            try {
                val response = repository.postMessage(pageId, message, pageToken)
                if (response.isSuccessful) {
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
                if (!pages.isNullOrEmpty()) {
                    _fbConnected.value = true
                    _fbPageImage.value = pages.first().picture?.data?.url


                    pages.forEach { page ->
                        Log.d("FB_PAGE","pages : ${page}")
                        Log.d("FB_PAGE", "Page: ${page.name}, ID: ${page.id}, Token: ${page.access_token}, Image: ${page.picture?.data?.url}")
                        prefs.saveFacebookData(page.id, page.picture?.data?.url.toString(),page.access_token.toString())
                    }
                } else {
                    Log.d("FB_PAGE","pages : ${pages}...${response}")
                    _fbConnected.value = false
                    _fbPageImage.value = null
                }
            } else {
                _fbConnected.value = false
                _fbPageImage.value = null
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


    fun setFbDisconnected() {
        _fbConnected.value = false
        _fbPageImage.value = ""
        prefs.clearFacebookData() // if you're saving token/page id
    }
}
