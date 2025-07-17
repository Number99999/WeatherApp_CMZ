package com.boom.weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.boom.weather.CustomView.PermissionDialogFragment
import com.boom.weather.FrameWork.Data.LocalStorageManager
import com.boom.weather.FrameWork.EventApp.FirebaseManager
import com.boom.weather.FrameWork.SDK.RemoteConfigManager
import com.boom.weather.Manager.AdManager
import com.boom.weather.Model.FakeGlobal
import com.boom.weather.Model.Object.KeyEventFirebase
import com.boom.weather.Model.Object.KeysStorage
import com.boom.weather.Model.Object.PermissionModel

class ActivityRequestLocation : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_request_location)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        InitEventBtn()
        LocalStorageManager.putString(KeysStorage.isFirstOpenApp, "true");
        findViewById<ScrollView>(R.id.request_location_1).visibility = View.VISIBLE
        findViewById<ScrollView>(R.id.request_location_2).visibility = View.GONE
        findViewById<ScrollView>(R.id.request_location_3).visibility = View.GONE
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Từ chối quyền vị trí", Toast.LENGTH_SHORT).show()
        }
    }

    fun showCustomPermissionDialog() {
        PermissionDialogFragment {
            requestLocationPermission()
        }.show(supportFragmentManager, "PermissionDialog")
    }

    fun requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun InitEventBtn() {
        InitEventButtonAcceptLocation()
        InitEventButtonManual()
        InitEventAcceptAllTime()
        InitEventAcceptThisTime()
        InitEventDontAccept()
        InitEventClickPolicy()
        findViewById<TextView>(R.id.btn_continue).setOnClickListener {
            if (findViewById<SwitchCompat>(R.id.switch_accept).isChecked) {
                FakeGlobal.getInstance().userAcceptRequestLocation = true;
                requestPermissionLocation()
            } else {
                val changeP = Intent(this, ActivityChooseLocation::class.java);
                startActivity(changeP)
//                finish()
            }
        }
    }

    private fun InitEventClickPolicy() {
        val txt = findViewById<TextView>(R.id.txt_policy)
        txt.setOnClickListener {
            val changePage = Intent(this, ActivityPolicy::class.java)
            startActivity(changePage)
        }

    }

    private fun InitEventButtonAcceptLocation() {
        val btn = findViewById<Button>(R.id.btn_accept)

        btn.setOnClickListener {
//            firstView?.visibility = View.GONE
//            secondView?.visibility = View.VISIBLE
//            val changePage = Intent(this, ActivityChooseLocationWithMap::class.java);\

//            startActivity(changePage);
//            showCustomPermissionDialog()
            this.loadNativeAds()
            findViewById<ScrollView>(R.id.request_location_1).visibility = View.GONE
            findViewById<ScrollView>(R.id.request_location_3).visibility = View.VISIBLE
            findViewById<ScrollView>(R.id.request_location_3).post {
                findViewById<ScrollView>(R.id.request_location_3).fullScroll(View.FOCUS_DOWN)
            }
            findViewById<ScrollView>(R.id.request_location_3).setOnTouchListener { _, _ -> true }
        }
    }

    private fun loadNativeAds() {
        if(RemoteConfigManager.getInstance().getRemoteConfig().nativeAdsEnabled==false) return;
        var adMgr = AdManager.getInstance(this@ActivityRequestLocation);
        adMgr.loadNativeClickAd(findViewById<FrameLayout>(R.id.ad_container), onAdLoaded = {
            FirebaseManager.getInstance(context = this)
                .sendEvent(KeyEventFirebase.navPermission, "loaded", true)
        }, onAdFailed = {
            println("onAdFailed")
            FirebaseManager.getInstance(context = this)
                .sendEvent(KeyEventFirebase.navPermission, "loaded", true)
        }, onAdImpression = {
        })
    }

    private fun InitEventButtonManual() {
        val btn = findViewById<Button>(R.id.btn_manual_search)
        btn.setOnClickListener {
//            context.startActivity(Intent.createChooser(intent, context.getString("email_body")))
//
            FakeGlobal.getInstance().userAcceptRequestLocation = false;
//            val changePage = Intent(this, ActivityBigCountry::class.java);
            val changePage = Intent(this, ActivityChooseLocation::class.java);
            startActivity(changePage);
        }
    }

    private fun InitEventAcceptAllTime() {
        val btn = findViewById<Button>(R.id.btn_accept_when_use_app)
        btn.setOnClickListener {
            showCustomPermissionDialog()
        }
    }

    private fun InitEventAcceptThisTime() {
        val btn = findViewById<Button>(R.id.btn_accept_this_time)
        btn.setOnClickListener {
            val changePage = Intent(this, MainActivity::class.java);
            startActivity(changePage);
        }
    }

    private fun InitEventDontAccept() {
        val btn = findViewById<Button>(R.id.btn_dont_accept)
        btn.setOnClickListener {
            val changePage = Intent(this, ActivityChooseLocation::class.java);
            startActivity(changePage);
        }
    }

    private fun requestPermissionLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PermissionModel.REQUEST_LOCATION
            )
            return
        } else {
            val changePage = Intent(this, MainActivity::class.java);
            changePage.putExtra("FROM_REQUEST_LOCATION", true);
            startActivity(changePage);
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionModel.REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val changePage = Intent(this, ActivityTutorial::class.java);
                changePage.putExtra("FROM_REQUEST_LOCATION", true);
                startActivity(changePage);
            } else {
                Toast.makeText(
                    this, "Location permission is required to use this feature!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}