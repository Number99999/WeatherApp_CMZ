package com.cmzsoft.weather

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityCustomLayout : AppCompatActivity() {
    //    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: CustomLayoutAdapter
//    private val modules = mutableListOf(
//        ModuleItem(R.drawable.icon_chart, "Tình trạng hiện tại", true),
//        ModuleItem(R.drawable.icon_chart, "Dự báo theo giờ", true),
//        ModuleItem(R.drawable.icon_chart, "Dự báo hàng ngày", true),
//        ModuleItem(R.drawable.icon_rainfall, "Lượng mưa", true),
//        ModuleItem(R.drawable.icon_wind, "Gió", true),
//        ModuleItem(R.drawable.icon_air_quality, "Chất lượng không khí", true),
//        ModuleItem(R.drawable.icon_detail, "Chi tiết", true),
//        ModuleItem(R.drawable.icon_camera, "Nhiếp ảnh", true),
//        ModuleItem(R.drawable.icon_allergy, "Dị ứng", true),
//        ModuleItem(R.drawable.icon_map, "Bản đồ", true),
//    )
//
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_custom_layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

//data class ModuleItem(val iconResId: Int, val name: String, var enabled: Boolean)
//class CustomLayoutAdapter(
//    private val modules: MutableList<ModuleItem>
//) : RecyclerView.Adapter<CustomLayoutAdapter.CustomViewHolder>() {
//
//    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val icon: ImageView = itemView.findViewById(R.id.iconModule)
//        val name: TextView = itemView.findViewById(R.id.textModule)
//        val switchToggle: Switch = itemView.findViewById(R.id.switchModule)
//        val handleDrag: ImageView = itemView.findViewById(R.id.handleDrag)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_module, parent, false)
//        return CustomViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        val module = modules[position]
//
//        // Gán icon, tên
//        holder.icon.setImageResource(module.iconResId)
//        holder.name.text = module.name
//
//        // Đặt lại listener trước khi gán isChecked để tránh lỗi tái sử dụng view
//        holder.switchToggle.setOnCheckedChangeListener(null)
//        holder.switchToggle.isChecked = module.enabled
//
//        // Xử lý sự kiện bật tắt switch
//        holder.switchToggle.setOnCheckedChangeListener { _, isChecked ->
//            module.enabled = isChecked
//            // Có thể thêm callback thông báo thay đổi ở đây nếu cần
//        }
//
//        // Bạn có thể xử lý drag & drop với handleDrag ở đây nếu cần
//    }
//
//    override fun getItemCount(): Int = modules.size
//
//    // Thêm các hàm hỗ trợ kéo thả hoặc xoá item nếu muốn
//    fun moveItem(fromPosition: Int, toPosition: Int) {
//        val movedItem = modules.removeAt(fromPosition)
//        modules.add(toPosition, movedItem)
//        notifyItemMoved(fromPosition, toPosition)
//    }
//
//    fun removeItem(position: Int) {
//        modules.removeAt(position)
//        notifyItemRemoved(position)
//    }
//}