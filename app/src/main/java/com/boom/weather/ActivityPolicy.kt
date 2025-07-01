package com.boom.weather

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ActivityPolicy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy)
        apply {
            findViewById<ImageView>(R.id.btnBack).setOnClickListener {
                finish()
            }
            val webViewPolicy = findViewById<WebView>(R.id.webViewPolicy)
            val settings = webViewPolicy.settings
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            webViewPolicy.webViewClient = WebViewClient()
            webViewPolicy.loadUrl("https://sites.google.com/boomstudio.vn/privacypolicyforboomgamesjsc")
        }
    }
}