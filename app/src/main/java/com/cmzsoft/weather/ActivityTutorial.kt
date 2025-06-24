package com.cmzsoft.weather

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmzsoft.weather.MainActivity
import com.cmzsoft.weather.Manager.AdManager

class ActivityTutorial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_turorial)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadNativeAds()
    }

    private fun loadNativeAds() {
        var adMgr = AdManager.getInstance(this@ActivityTutorial);
        adMgr.loadNativeClickAd(findViewById<FrameLayout>(R.id.ad_container), onAdLoaded = {
            println("onAdLoaded")
        }, onAdFailed = { println("onAdFailed") }, onAdImpression = {
            println("onAdImpression")
        })
    }
}