package com.boom.weather

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.boom.weather.Manager.AdManager

class ActivityTutorial : AppCompatActivity() {
    private lateinit var view1: RelativeLayout
    private lateinit var view2: RelativeLayout
    private lateinit var view3: RelativeLayout
    private var count_clickBtn: Int = 0;
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
        view1 = findViewById<RelativeLayout>(R.id.contain_tut1)
        view2 = findViewById<RelativeLayout>(R.id.contain_tut2)
        view3 = findViewById<RelativeLayout>(R.id.contain_tut3)
        view1.visibility = View.VISIBLE
        view2.visibility = View.GONE
        view3.visibility = View.GONE
        findViewById<ImageView>(R.id.back_tut2).visibility = View.GONE
        findViewById<Button>(R.id.btn_next).setOnClickListener {
            count_clickBtn++;
            when (count_clickBtn) {
                1 -> {
                    view1.visibility = View.GONE
                    view2.visibility = View.VISIBLE
                    view3.visibility = View.GONE
                    findViewById<ImageView>(R.id.back_tut2).visibility = View.VISIBLE
                }

                2 -> {
                    view1.visibility = View.GONE
                    view2.visibility = View.GONE
                    view3.visibility = View.VISIBLE
                    findViewById<ImageView>(R.id.back_tut2).visibility = View.GONE
                }

                3 -> {
                    val changeP = Intent(this, MainActivity::class.java)
                    startActivity(changeP)
                    finish()
                }
            }
        }
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