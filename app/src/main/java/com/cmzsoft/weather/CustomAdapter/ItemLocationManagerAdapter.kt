package com.cmzsoft.weather.CustomAdapter

import android.annotation.SuppressLint
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
import com.cmzsoft.weather.Service.DatabaseService
import com.cmzsoft.weather.Service.LocationService

class ItemLocationManagerAdapter(private var items: MutableList<LocationWeatherModel>) :
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        val defaultAdd =
            DatabaseService.getInstance(context.applicationContext).locationWeatherService.getDefaultLocationWeather();
        holder.txtDefault.visibility = if (item.isDefaultLocation == 1) {
            View.VISIBLE
        } else {
            View.GONE
        }
        if (position == 0) {
            if (LocationService.checkPermissionLocation()) holder.title.text =
                "${item.name} (Vị trí của bạn)"
            else "${item.name}"
            if (defaultAdd == null) {
                holder.txtDefault.visibility = View.VISIBLE
            }

            holder.contain.setOnClickListener {
                if (item.isEdit == true) return@setOnClickListener
                FakeGlobal.getInstance().flagIsChooseDefaultLocation = true
                FakeGlobal.getInstance().curLocation = item
                val changeIntent = Intent(context, MainActivity::class.java)
                changeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                context.startActivity(changeIntent)
            }
        } else {
            holder.title.text = item.name
            holder.contain.setOnClickListener {
                if (item.isEdit == true) return@setOnClickListener
                FakeGlobal.getInstance().curLocation = item
                FakeGlobal.getInstance().flagIsChooseDefaultLocation = false
                val changeIntent = Intent(context, MainActivity::class.java)
                changeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                context.startActivity(changeIntent)
            }
        }
        holder.fullPath.text = shortPathLocation(item.fullPathLocation)
        val btn_delete = holder.itemView.findViewById<ImageView>(R.id.btn_delete)
        btn_delete.visibility = if (item.isEdit) View.VISIBLE else View.GONE
        btn_delete.setOnClickListener {
            items.remove(item)
            DatabaseService.getInstance(context.applicationContext).locationWeatherService.deleteItemInTable(
                item
            );
            notifyDataSetChanged()
        }
        if (item.weather != null && item.weather.isNotEmpty()) {
            val weatherIcon = item.weather.toIntOrNull()
            if (weatherIcon != null)
                holder.itemView.findViewById<ImageView>(R.id.icon_status)
                    .setImageResource(weatherIcon);
        }

        val btn_drag = holder.itemView.findViewById<ImageView>(R.id.btn_drag_to_move)
        btn_drag.visibility = if (item.isEdit) View.VISIBLE else View.GONE
//        btn_drag.setOnDragListener {
//
//        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun btnEditClicked() {
        for (item in items) {
            item.setIdEdit(!item.isEdit)
        }
        // update RecyclerView
        notifyDataSetChanged()  // Hoặc notifyItemChanged() nếu cần thay đổi cho từng item
    }

    private fun shortPathLocation(fullPath: String): String {
        val spl = fullPath.split(", ");
        if (spl.size >= 2) return spl.get(spl.size - 2) + ", " + spl.get(spl.size - 1);
        return fullPath;
    }
}
