package com.cmzsoft.weather

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.CustomAdapter.SettingThemeIconAdapter
import com.cmzsoft.weather.CustomAdapter.SettingThemeNotificationAdapter
import com.cmzsoft.weather.FrameWork.Data.LocalStorageManager
import com.cmzsoft.weather.Model.ItemThemeIconModel
import com.cmzsoft.weather.Model.ItemThemeNotificationModel
import com.cmzsoft.weather.Model.Object.KeysStorage
import com.cmzsoft.weather.Model.SettingThemeModel
import com.cmzsoft.weather.R
import com.google.android.material.tabs.TabLayout

class ActivitySettingTheme : BaseActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerViewIcon: RecyclerView
    private lateinit var recyclerViewNoti: RecyclerView
    private lateinit var data: SettingThemeModel

    private var adapterIcon: SettingThemeIconAdapter? = null
    private var adapterNotification: SettingThemeNotificationAdapter? = null
    private var curTab: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting_theme)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.btn_back).setOnClickListener { finish() }
        setupFullRecycleView()

        tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.addTab(tabLayout.newTab().setText("Icons"))
        tabLayout.addTab(tabLayout.newTab().setText("Widgets"))
        tabLayout.addTab(tabLayout.newTab().setText("Notifications"))
        tabLayout.addTab(tabLayout.newTab().setText("Live Wallpaper"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                onSelectTab(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
        setupFirst()
    }

    private fun setupFullRecycleView() {
        val spacing = resources.getDimensionPixelSize(R.dimen.widget_size_10)

        recyclerViewIcon = findViewById<RecyclerView>(R.id.recyclerViewIcon)
        recyclerViewIcon.layoutManager = GridLayoutManager(this, 2)
        recyclerViewIcon.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))

        recyclerViewNoti = findViewById<RecyclerView>(R.id.recyclerViewNoti)
        recyclerViewNoti.layoutManager = GridLayoutManager(this, 2)
        recyclerViewNoti.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))
    }

    init {
        data = LocalStorageManager.getObject<SettingThemeModel>(
            KeysStorage.settingTheme, SettingThemeModel::class.java
        ) ?: SettingThemeModel(0, 0, 0, 0)
    }

    fun setupFirst() {
        curTab = 0;
        tabLayout.selectTab(tabLayout.getTabAt(0))
        val lst = mutableListOf(
            ItemThemeIconModel("Default", 0, R.drawable.theme_icon_1, true),
            ItemThemeIconModel("Flat", 1, R.drawable.theme_icon_2, false),
            ItemThemeIconModel("Chibi", 2, R.drawable.theme_icon_3, false),
            ItemThemeIconModel("3D", 3, R.drawable.theme_icon_4, false),
            ItemThemeIconModel("Flat Shadow", 4, R.drawable.theme_icon_5, false),
        )
        if (adapterIcon == null) {
            adapterIcon = SettingThemeIconAdapter(lst) { }
            recyclerViewIcon.adapter = adapterIcon
        }

        recyclerViewIcon.visibility = View.VISIBLE
        recyclerViewNoti.visibility = View.GONE
    }

    private fun onSelectTab(idx: Int) {
        if (idx == curTab) return;
        when (idx) {
            0 -> {
                curTab = idx
                tabLayout.selectTab(tabLayout.getTabAt(idx))
                val lst = mutableListOf(
                    ItemThemeIconModel("Default", 0, R.drawable.theme_icon_1, true),
                    ItemThemeIconModel("Flat", 1, R.drawable.theme_icon_2, false),
                    ItemThemeIconModel("Chibi", 2, R.drawable.theme_icon_3, false),
                    ItemThemeIconModel("3D", 3, R.drawable.theme_icon_4, false),
                    ItemThemeIconModel("Flat Depth", 4, R.drawable.theme_icon_5, false),
                )
                if (adapterIcon == null) {
                    adapterIcon = SettingThemeIconAdapter(lst) { }
                    recyclerViewIcon.adapter = adapterIcon
                }

                recyclerViewIcon.visibility = View.VISIBLE
                recyclerViewNoti.visibility = View.GONE
            }

            2 -> {
                curTab = idx
                tabLayout.selectTab(tabLayout.getTabAt(idx))
                val lst = mutableListOf(
                    ItemThemeNotificationModel(
                        "Simple", 0, R.drawable.theme_noti_1, "bg_7.png", false
                    ),
                    ItemThemeNotificationModel(
                        "Hourly Chart", 1, R.drawable.theme_noti_2, "bg_10.png", false
                    ),
                    ItemThemeNotificationModel(
                        "7-Day Chart", 2, R.drawable.theme_noti_3, "bg_2.png", false
                    ),
                    ItemThemeNotificationModel(
                        "7-Day Weather", 3, R.drawable.theme_noti_4, "bg_12.png", false
                    ),
                    ItemThemeNotificationModel(
                        "Hourly Weather", 4, R.drawable.theme_noti_5, "bg_14.png", false
                    )
                )

                if (adapterNotification == null) {
                    adapterNotification =
                        SettingThemeNotificationAdapter(lst) { }
                    recyclerViewNoti.adapter = adapterNotification
                }

                recyclerViewIcon.visibility = View.GONE
                recyclerViewNoti.visibility = View.VISIBLE
            }
        }
    }
}

class GridSpacingItemDecoration(
    private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) outRect.top = spacing
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) outRect.top = spacing
        }
    }
}
