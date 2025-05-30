package com.cmzsoft.weather.CustomAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.CustomLayoutItem
import com.cmzsoft.weather.Model.TitleChartItemModel
import com.cmzsoft.weather.R

class CustomLayoutAdapter(
    private val items: List<CustomLayoutItem>
) : RecyclerView.Adapter<CustomLayoutAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.icon)
        val txtTitle: TextView = itemView.findViewById(R.id.txt_title)
        val sw: Switch = itemView.findViewById(R.id.switch_layout)
        val iconDrag: ImageView = itemView.findViewById(R.id.img_drag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_custom_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.imgIcon.setImageResource(item.icon)
        holder.txtTitle.text = item.title
        // holder.btnArrow.setOnClickListener { ... }
    }
}
