package com.cmzsoft.weather.CustomAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmzsoft.weather.Model.TitleChartItemModel
import com.cmzsoft.weather.R

class TitleChartDegreeAdapter(
    private val items: List<TitleChartItemModel>
) : RecyclerView.Adapter<TitleChartDegreeAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txt_rainfall_rate: TextView = itemView.findViewById(R.id.txt_rainfall_rate)
        val txt_time: TextView = itemView.findViewById(R.id.txt_time)
        val icon_status_weather: ImageView = itemView.findViewById(R.id.icon_status_weather)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.title_chart_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (item.weatherIconUrl != null)
            Glide.with(holder.icon_status_weather.context)
                .load(item.weatherIconUrl)
                .into(holder.icon_status_weather)
        holder.txt_time.text = item.time
        holder.txt_rainfall_rate.text = item.rainPercent.toString() + "%"
    }
}
