package com.cmzsoft.weather

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmzsoft.weather.Model.FakeGlobal

class ActivityRadarWeatherMap : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_radar_weather_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        webView = findViewById(R.id.weatherWebView)

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        webView.webViewClient = WebViewClient()

        val loc = FakeGlobal.getInstance().curLocation
        val windyMapOnlyUrl =
            "https://embed.windy.com/embed2.html" + "?lat=${loc.latitude}&lon=${loc.longitude}" + "&detailLat=${loc.latitude}&detailLon=${loc.longitude}" + "&width=100%&height=100%" + "&zoom=7&level=surface&overlay=rain" + "&menu=&message=false&marker=false&calendar=" + "&pressure=false&type=map&location=coordinates" + "&detail=false&metricWind=default&metricTemp=default"

        webView.loadUrl(windyMapOnlyUrl)
        findViewById<ImageView>(R.id.btn_back).setOnClickListener { finish() }
    }
}