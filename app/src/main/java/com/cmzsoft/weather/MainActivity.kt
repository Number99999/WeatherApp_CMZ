package com.cmzsoft.weather

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmzsoft.weather.APICall.RequestAPI


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val requestAPI = RequestAPI()
        Thread {
            val result = requestAPI.CallAPI()
            runOnUiThread {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            }
        }.start()

        val btn = findViewById<Button>(R.id.button2)
        btn.setOnClickListener {
//            val changePage = Intent(this, MainActivity2::class.java);
            val changePage = Intent(this, activity_setting_scene::class.java);
            startActivity(changePage);
        }


//        val intent = Intent(this, R.layout.)
//        startActivity(intent)
    }
}