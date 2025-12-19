package com.wingspan.aimediahub.ui.theme

import android.net.Uri
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import java.util.UUID

@Composable
fun FacebookOAuthWebView(
    onLoginSuccess: (String) -> Unit // callback with your backend JWT
) {

    val context = LocalContext.current
    val webView = remember { WebView(context) }
    val FB_APP_ID = "1157175713250946"
    val BACKEND_CALLBACK_URL = "https://automatedpostingbackend.onrender.com/social/facebook/callback"
    DisposableEffect(Unit) {
        // Configure WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        Log.d("token 1234","--FacebookOAuthWebView")
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    // Detect when backend redirects with app token
                    if (it.startsWith("aimediahub://login-success")) {
                        val uri = Uri.parse(it)
                        val token = uri.getQueryParameter("token")
                        token?.let { jwt ->
                            // Call the callback
                            onLoginSuccess(jwt)

                            // Optional: clear WebView history / cache
                            webView.clearHistory()
                            webView.clearCache(true)
                        }
                        return true
                    }
                }
                return false
            }
        }

        // Load Facebook OAuth URL
        val state = UUID.randomUUID().toString()
        val fbOAuthUrl = "https://www.facebook.com/v20.0/dialog/oauth?" +
                "client_id=${FB_APP_ID}" +
                "&redirect_uri=${BACKEND_CALLBACK_URL}" +
                "&state=$state" +
                "&scope=email,public_profile"

        webView.loadUrl(fbOAuthUrl)

        onDispose {
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = Modifier.fillMaxSize()
    )
}
