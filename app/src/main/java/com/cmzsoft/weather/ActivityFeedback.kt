package com.cmzsoft.weather

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmzsoft.weather.R

class ActivityFeedback : BaseActivity() {
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
        val send = Intent(Intent.ACTION_SENDTO)
        val uriText = "mailto:" + Uri.encode("no-reply@accounts.google.com") +
                "?subject=" + Uri.encode("App feedback: ${FileBuildConfig.VERSION_NAME}") +
                "&body=" + Uri.encode(findViewById<EditText>(R.id.edt_feedback).text.toString())
        val uri = uriText.toUri()
        send.setData(uri)
        startActivity(send)
    }
}