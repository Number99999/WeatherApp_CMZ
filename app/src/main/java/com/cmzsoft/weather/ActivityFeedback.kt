package com.cmzsoft.weather

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityFeedback : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feedback)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_send).setOnClickListener {
            sendFeedback()
        }
    }

    private fun sendFeedback() {

        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData("mailto:duongnn.cmzsoft@gmail.com".toUri()) // only email apps should handle this
//            intent.putExtra(
//                Intent.EXTRA_EMAIL,
//                "someone@gmail.com"
//            );
            intent.putExtra(Intent.EXTRA_SUBJECT, "App feedback")
            intent.putExtra(Intent.EXTRA_TEXT, findViewById<EditText>(R.id.edt_feedback).text)
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this, "\"There are no email client installed on your device.\"", Toast.LENGTH_LONG
            ).show()
        }
    }
}