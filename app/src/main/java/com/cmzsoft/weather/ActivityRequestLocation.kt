package com.cmzsoft.weather

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
    }

    private fun InitEventButtonAcceptLocation() {
        val btn = findViewById<Button>(R.id.btn_accept)
        val firstView = findViewById<ScrollView>(R.id.request_location_1)
        val secondView = findViewById<ScrollView>(R.id.request_location_2)

        btn.setOnClickListener {
            firstView?.visibility = View.GONE
            secondView?.visibility = View.VISIBLE
        }
    }

    private fun InitEventButtonManual() {
        val btn = findViewById<Button>(R.id.btn_manual_search)
        btn.setOnClickListener {
            val changePage = Intent(this, MainActivity::class.java);
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
            val changePage = Intent(this, MainActivity::class.java);
            startActivity(changePage);
        }
    }
}