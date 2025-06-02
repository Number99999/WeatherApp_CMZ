//package com.cmzsoft.weather
//
//import android.graphics.drawable.GradientDrawable
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.tabs.TabLayout
//
//data class ThemeItem(val name: String, val colors: IntArray, var isSelected: Boolean = false)
//
//class ThemeAdapter(private val items: List<ThemeItem>, private val onClick: (Int) -> Unit) : RecyclerView.Adapter<ThemeAdapter.ViewHolder>() {
//
//    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val colorContainer: FrameLayout = view.findViewById(R.id.colorContainer)
//        val themeName: TextView = view.findViewById(R.id.themeName)
//        val checkMark: ImageView = view.findViewById(R.id.checkMark)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting_theme, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = items[position]
//
//        // Tạo gradient background cho colorContainer
//        val gradient = GradientDrawable(
//            GradientDrawable.Orientation.TOP_BOTTOM,
//            item.colors
//        )
//        holder.colorContainer.background = gradient
//
//        holder.themeName.text = item.name
//        holder.checkMark.visibility = if (item.isSelected) View.VISIBLE else View.GONE
//
//        holder.itemView.setOnClickListener {
//            onClick(position)
//        }
//    }
//
//    override fun getItemCount(): Int = items.size
//}
//
//class SettingTheme : AppCompatActivity() {
//    private lateinit var adapter: ThemeAdapter
//    private val themeList = mutableListOf(
//        ThemeItem("Phẳng", intArrayOf(0xFF9B3BFF.toInt(), 0xFF663BFF.toInt()), true),
//        ThemeItem("Hoạt hình", intArrayOf(0xFF3B7CBF.toInt(), 0xFF2C4E77.toInt())),
//        ThemeItem("Màu sắc", intArrayOf(0xFF62FFFF.toInt(), 0xFF429EA5.toInt())),
//        ThemeItem("Tối giản", intArrayOf(0xFFBFAF61.toInt(), 0xFF9A7B4D.toInt()))
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_setting_theme)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//
//
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        toolbar.setNavigationOnClickListener {
//            finish()
//        }
//
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        recyclerView.layoutManager = GridLayoutManager(this, 2)
//        adapter = ThemeAdapter(themeList) { pos ->
//            themeList.forEachIndexed { index, themeItem ->
//                themeItem.isSelected = index == pos
//            }
//            adapter.notifyDataSetChanged()
//        }
//        recyclerView.adapter = adapter
//
//        // TabLayout setup (có thể thêm listener nếu muốn)
//        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
//        tabLayout.addTab(tabLayout.newTab().setText("Biểu tượng"))
//        tabLayout.addTab(tabLayout.newTab().setText("Tiện ích"))
//        tabLayout.addTab(tabLayout.newTab().setText("Thông báo"))
//        tabLayout.addTab(tabLayout.newTab().setText("Nền trực tiếp"))
//
//        tabLayout.selectTab(tabLayout.getTabAt(0))
//    }
//}