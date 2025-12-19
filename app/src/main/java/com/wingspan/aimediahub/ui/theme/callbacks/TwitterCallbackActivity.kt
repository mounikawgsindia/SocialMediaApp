package com.wingspan.aimediahub.ui.theme.callbacks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wingspan.aimediahub.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TwitterCallbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the redirect data
        val uri = intent.data
        if (uri != null && uri.toString().startsWith("com.wingspan.aimediahub://twitter-callback")) {

            val sessionId = uri.getQueryParameter("session_id")
//            val userId = uri.getQueryParameter("user_id")
//            val name = uri.getQueryParameter("name")

            // Now you have the user info
            // You can save it locally or pass to your main activity
            Log.d("TwitterCallback", "Token: $sessionId")

            // Example: send data back to MainActivity
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("sessionId", sessionId)
            }
            startActivity(intent)
            finish() // Close callback activity
        } else {
            // Something went wrong
            Toast.makeText(this, "Twitter login failed", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}