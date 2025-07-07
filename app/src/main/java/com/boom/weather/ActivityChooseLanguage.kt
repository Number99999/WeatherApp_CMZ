package com.boom.weather

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.weather.CustomAdapter.SettingChooseLanguageAdapter
import com.boom.weather.FrameWork.Data.LocalStorageManager
import com.boom.weather.Manager.LocaleManager
import com.boom.weather.Model.ChooseLanguageModel
import com.boom.weather.Model.ItemChooseLanguageModel
import com.boom.weather.Model.Object.KeysStorage

class ActivityChooseLanguage : AppCompatActivity(),
    SettingChooseLanguageAdapter.OnItemClickListener {
    private var _data: ChooseLanguageModel = LocalStorageManager.getObject<ChooseLanguageModel>(
        KeysStorage.settingLanguage, ChooseLanguageModel::class.java
    )

    private var isClosed: Boolean = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_language)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingLanguageAdapter()
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            if (isClosed == true) {
            } else {
                isClosed = true
                val changePage = Intent(this, MainActivity::class.java)
                startActivity(changePage)
                finish()
            }
        }
        findViewById<Button>(R.id.btn_apply).setOnClickListener {
            if (isClosed == true) {
            } else {
                isClosed = true
                val changePage = Intent(this, MainActivity::class.java)
                startActivity(changePage)
                finish()
            }
        }
    }

    private fun settingLanguageAdapter() {
        var listLanguage: MutableList<ItemChooseLanguageModel> = mutableListOf(
            ItemChooseLanguageModel(R.drawable.icon_en, "English", _data.en),
            ItemChooseLanguageModel(R.drawable.icon_es, "Spanish", _data.es),
            ItemChooseLanguageModel(R.drawable.icon_pt, "Portuguese", _data.pt),
            ItemChooseLanguageModel(R.drawable.icon_fr, "French", _data.fr),
            ItemChooseLanguageModel(R.drawable.icon_hi, "Hindi", _data.hi),
            ItemChooseLanguageModel(R.drawable.icon_tl, "Filipino", _data.tl),
            ItemChooseLanguageModel(R.drawable.icon_ja, "Japanese", _data.ja)
        )

        val adapter = SettingChooseLanguageAdapter(listLanguage, this)
        val rcl = findViewById<RecyclerView>(R.id.listLanguage)
        rcl.adapter = adapter;
        rcl.layoutManager = LinearLayoutManager(this)
        rcl.invalidate()
    }

    override fun onItemClick(position: Int) {
        val adapter =
            (findViewById<RecyclerView>(R.id.listLanguage).adapter as SettingChooseLanguageAdapter)
        val item = adapter.items[position]
        if (item.isChoose == true) return;
        item.isChoose = true
        _data.en = false
        _data.es = false
        _data.pt = false
        _data.fr = false
        _data.hi = false
        _data.tl = false
        _data.ja = false

        val lMgr = LocaleManager(this)
        when (position) {
            0 -> {
                _data.en = true
                lMgr.setLocale("en")  // Set to English
            }

            1 -> {
                _data.es = true
                lMgr.setLocale("es")  // Set to Spanish
            }

            2 -> {
                _data.pt = true
                lMgr.setLocale("pt")  // Set to Portuguese
            }

            3 -> {
                _data.fr = true
                lMgr.setLocale("fr")  // Set to French
            }

            4 -> {
                _data.hi = true
                lMgr.setLocale("hi")  // Set to Hindi
            }

            5 -> {
                _data.tl = true
                lMgr.setLocale("tl")  // Set to Filipino
            }

            6 -> {
                _data.ja = true
                lMgr.setLocale("ja")  // Set to Japanese
            }
        }
        LocalStorageManager.putObject(KeysStorage.settingLanguage, _data)
        adapter.notifyDataSetChanged()

        recreate()
    }
}