package com.cmzsoft.weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmzsoft.weather.Model.FakeGlobal
import com.cmzsoft.weather.Model.PermissionModel

class ActivityRequestLocation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_request_location)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        InitEventBtn()
    }

    private fun InitEventBtn() {
        InitEventButtonAcceptLocation()
        InitEventButtonManual()
        InitEventAcceptAllTime()
        InitEventAcceptThisTime()
        InitEventDontAccept()
        InitEventClickPolicy()
    }

    private fun InitEventClickPolicy() {
        val txt = findViewById<TextView>(R.id.txt_policy)
        txt.setOnClickListener {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse(" https://www.google.com/") // Thay link tại đây
//            startActivity(intent)
            Toast.makeText(this, "Policy clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun InitEventButtonAcceptLocation() {
        val btn = findViewById<Button>(R.id.btn_accept)

        btn.setOnClickListener {
//            firstView?.visibility = View.GONE
//            secondView?.visibility = View.VISIBLE
//            val changePage = Intent(this, ActivityChooseLocationWithMap::class.java);
//            startActivity(changePage);
            FakeGlobal.getInstance().userAcceptRequestLocation = true;
            requestPermissionLocation()
        }
    }

    private fun InitEventButtonManual() {
        val btn = findViewById<Button>(R.id.btn_manual_search)
        btn.setOnClickListener {
            FakeGlobal.getInstance().userAcceptRequestLocation = false;
            val changePage = Intent(this, ActivityChooseLocationWithMap::class.java);

            startActivity(changePage);
        }
    }

    private fun InitEventAcceptAllTime() {
        val btn = findViewById<Button>(R.id.btn_accept_when_use_app)
        btn.setOnClickListener {
            val changePage = Intent(this, MainActivity::class.java);
            startActivity(changePage);
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
            val changePage = Intent(this, ActivityChooseLocationWithMap::class.java);
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
                val changePage = Intent(this, MainActivity::class.java);
                changePage.putExtra("FROM_REQUEST_LOCATION", true);
                startActivity(changePage);
            } else {
                Toast.makeText(
                    this, "Cần cấp quyền vị trí để sử dụng tính năng này!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}