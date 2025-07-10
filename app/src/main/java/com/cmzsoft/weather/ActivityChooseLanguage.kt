package com.cmzsoft.weather

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.CustomAdapter.SettingChooseLanguageAdapter
import com.cmzsoft.weather.FrameWork.Data.LocalStorageManager
import com.cmzsoft.weather.Manager.LocaleManager
import com.cmzsoft.weather.Model.ChooseLanguageModel
import com.cmzsoft.weather.Model.ItemChooseLanguageModel
import com.cmzsoft.weather.Model.Object.KeysStorage
import com.cmzsoft.weather.R

class ActivityChooseLanguage : BaseActivity(),
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
        findViewById<RelativeLayout>(R.id.btn_apply).setOnClickListener {
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
        val newSelectedItem = adapter.items[position]

        if (newSelectedItem.isChoose == true) {
            return
        }

        val oldSelectedIndex = adapter.items.indexOfFirst { it.isChoose }
        if (oldSelectedIndex != -1) {
            adapter.items[oldSelectedIndex].isChoose = false
        }

        newSelectedItem.isChoose = true

        if (oldSelectedIndex != -1) {
            adapter.notifyItemChanged(oldSelectedIndex)
        }
        adapter.notifyItemChanged(position)

        _data.en = false
        _data.es = false
        _data.pt = false
        _data.fr = false
        _data.hi = false
        _data.tl = false
        _data.ja = false

        val lMgr = LocaleManager(this)
        val localeCode: String
        when (position) {
            0 -> {
                _data.en = true
                localeCode = "en"
            }

            1 -> {
                _data.es = true
                localeCode = "es"
            }

            2 -> {
                _data.pt = true
                localeCode = "pt"
            }

            3 -> {
                _data.fr = true
                localeCode = "fr"
            }

            4 -> {
                _data.hi = true
                localeCode = "hi"
            }

            5 -> {
                _data.tl = true
                localeCode = "tl"
            }

            6 -> {
                _data.ja = true
                localeCode = "ja"
            }

            else -> return
        }
        lMgr.setLocale(localeCode)
        LocalStorageManager.putObject(KeysStorage.settingLanguage, _data)
        recreate()
    }
}