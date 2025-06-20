package com.cmzsoft.weather

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmzsoft.weather.FrameWork.Data.LocalStorageManager
import com.cmzsoft.weather.Model.Object.KeysStorage

class LoadingAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading_app)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        activeActivity()
    }

    fun activeActivity() {
//        val changePage = Intent(this, ActivitySettingTheme::class.java);
//        startActivity(changePage);
//
//        return
        if (LocalStorageManager.getString(KeysStorage.isFirstOpenApp) == null) {
            val changePage = Intent(this, ActivityRequestLocation::class.java);
            startActivity(changePage);
            finish()
        } else {
            val changePage = Intent(this, MainActivity::class.java);
            startActivity(changePage);
            finish()
        }
    }

}