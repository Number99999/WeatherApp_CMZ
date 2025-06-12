package com.cmzsoft.weather.CustomAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.MainActivity
import com.cmzsoft.weather.Model.FakeGlobal
import com.cmzsoft.weather.Model.LocationWeatherModel
import com.cmzsoft.weather.R

class ItemLocationManagerAdapter(private val items: List<LocationWeatherModel>) :
    RecyclerView.Adapter<ItemLocationManagerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.txt_title_location)
        var fullPath = itemView.findViewById<TextView>(R.id.txt_path_location)
        var iconStatus = itemView.findViewById<ImageView>(R.id.icon_status)
        var txtDefault = itemView.findViewById<TextView>(R.id.txt_default)
        var contain = itemView.findViewById<LinearLayout>(R.id.contain_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location_manager, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.title.text = item.name;
        holder.fullPath.text = item.fullPathLocation.replace("${item.name}, ", "");

        holder.txtDefault.visibility = (if (item.isDefaultLocation) {
            View.VISIBLE
        } else View.GONE)

        holder.contain.setOnClickListener {
            FakeGlobal.getInstance().curLocation = item;
            val changeIntent = Intent(context, MainActivity::class.java);
            changeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(changeIntent)
        }
    }
}
