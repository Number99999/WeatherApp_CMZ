package com.cmzsoft.weather

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmzsoft.weather.FrameWork.Data.LocalStorageManager
import com.cmzsoft.weather.Manager.LocaleManager
import com.cmzsoft.weather.Model.Object.KeysStorage
import com.cmzsoft.weather.R


class LoadingAppActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading_app)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        applyCurrentLocale()
        activeActivity()
    }

    fun applyCurrentLocale() {
        val lMgr = LocaleManager(this)
        lMgr.applySavedLanguage()
//        lMgr.setLocale(lMgr.getSavedLanguage())
    }

    fun activeActivity() {
        if (LocalStorageManager.getString(KeysStorage.isFirstOpenApp) == null) {
            val changePage = Intent(this, ActivityRequestLocation::class.java)
            startActivity(changePage)
            finish()
        } else {
            val changePage = Intent(this, MainActivity::class.java)
            changePage.putExtra("FROM_REQUEST_LOCATION", false)
            changePage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(changePage)
            finish()
        }
    }
}
