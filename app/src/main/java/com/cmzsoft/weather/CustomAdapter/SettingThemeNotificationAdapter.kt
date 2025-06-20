package com.cmzsoft.weather.CustomAdapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.Model.ItemThemeNotificationModel
import com.cmzsoft.weather.R

class SettingThemeNotificationAdapter(
    private val items: List<ItemThemeNotificationModel>, private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<SettingThemeNotificationAdapter.ViewHolder>() {

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
        holder.img.setImageResource(item.idIcon)
        val inputStream = holder.itemView.context.assets.open("bg/${item.urlBg}")
        val drawable = Drawable.createFromStream(inputStream, null)
        holder.img.background = drawable
        if (item.isChoosen) holder.checkMark.visibility = View.VISIBLE
        else holder.checkMark.visibility = View.GONE
        holder.itemView.setOnClickListener {
            onClick(position)
        }
    }

    override fun getItemCount(): Int = items.size
}