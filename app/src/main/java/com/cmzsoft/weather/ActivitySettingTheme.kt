package com.cmzsoft.weather

import android.graphics.Rect
import android.os.Bundle
import android.view.View
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
import com.google.android.material.tabs.TabLayout

class ActivitySettingTheme : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var data: SettingThemeModel
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

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        val spacing = resources.getDimensionPixelSize(R.dimen.widget_size_10)

        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))

        tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.addTab(tabLayout.newTab().setText("Biểu tượng"))
        tabLayout.addTab(tabLayout.newTab().setText("Tiện ích"))
        tabLayout.addTab(tabLayout.newTab().setText("Thông báo"))
        tabLayout.addTab(tabLayout.newTab().setText("Nền trực tiếp"))

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

    init {
        data = LocalStorageManager.getObject<SettingThemeModel>(
            KeysStorage.settingTheme, SettingThemeModel::class.java
        ) ?: SettingThemeModel(0, 0, 0, 0)
    }

    fun setupFirst() {
        curTab = 0;
        tabLayout.selectTab(tabLayout.getTabAt(0))
        val lst = mutableListOf(
            ItemThemeIconModel("Mặc định", 0, R.drawable.theme_icon_1, true),
            ItemThemeIconModel("Bằng phẳng", 1, R.drawable.theme_icon_2, false),
            ItemThemeIconModel("Chibi", 2, R.drawable.theme_icon_3, false),
            ItemThemeIconModel("3D", 3, R.drawable.theme_icon_4, false),
            ItemThemeIconModel("Phẳng chìm", 4, R.drawable.theme_icon_5, false),
        )
        val adapter = SettingThemeIconAdapter(lst) { idx -> println("ZZZZZZZZZZZZZZZZZ ${idx}") }
        recyclerView.adapter = adapter
    }

    private fun onSelectTab(idx: Int) {
        if (idx == curTab) return;
        when (idx) {
            0 -> {
                curTab = idx
                tabLayout.selectTab(tabLayout.getTabAt(idx))
                val lst = mutableListOf(
                    ItemThemeIconModel("Mặc định", 0, R.drawable.theme_icon_1, true),
                    ItemThemeIconModel("Bằng phẳng", 1, R.drawable.theme_icon_2, false),
                    ItemThemeIconModel("Chibi", 2, R.drawable.theme_icon_3, false),
                    ItemThemeIconModel("3D", 3, R.drawable.theme_icon_4, false),
                    ItemThemeIconModel("Phẳng chìm", 4, R.drawable.theme_icon_5, false),
                )
                val adapter =
                    SettingThemeIconAdapter(lst) { idx -> println("ZZZZZZZZZZZZZZZZZ ${idx}") }
                recyclerView.adapter = adapter
            }

            2 -> {
                curTab = idx
                tabLayout.selectTab(tabLayout.getTabAt(idx))
                val lst = mutableListOf(
                    ItemThemeNotificationModel(
                        "Đơn giản", 0, R.drawable.theme_noti_1, "bg_7.png", false
                    ), ItemThemeNotificationModel(
                        "Biểu đồ hàng giờ", 1, R.drawable.theme_noti_2, "bg_10.png", false
                    ), ItemThemeNotificationModel(
                        "Biểu đồ 7 ngày", 2, R.drawable.theme_noti_3, "bg_2.png", false
                    ), ItemThemeNotificationModel(
                        "Thời tiết 7 ngày", 3, R.drawable.theme_noti_4, "bg_12.png", false
                    ), ItemThemeNotificationModel(
                        "Thời tiết hàng giờ", 4, R.drawable.theme_noti_5, "bg_14.png", false
                    )
                )
                val adapter =
                    SettingThemeNotificationAdapter(lst) { idx -> println("ZZZZZZZZZZZZZZZZZ ${idx}") }
                recyclerView.adapter = adapter
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
