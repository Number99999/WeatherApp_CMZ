package com.boom.weather.CustomAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boom.weather.Model.ItemThemeIconModel
import com.boom.weather.R

class SettingThemeIconAdapter(
    private val items: List<ItemThemeIconModel>, private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<SettingThemeIconAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.img_theme_icon)
        val title = view.findViewById<TextView>(R.id.tv_theme_label)
        val checkMark = view.findViewById<ImageView>(R.id.img_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_theme_icon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.img.setImageResource(item.idImage)
        if (item.isChoosen) holder.checkMark.visibility = View.VISIBLE
        else holder.checkMark.visibility = View.GONE
        holder.itemView.setOnClickListener {
            onClick(position)
        }
    }

    override fun getItemCount(): Int = items.size
}