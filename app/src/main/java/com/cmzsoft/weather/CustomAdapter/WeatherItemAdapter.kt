package com.cmzsoft.weather.CustomAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.Model.DataWeatherPerHourModel
import com.cmzsoft.weather.R

class WeatherItemAdapter(
    private val items: List<DataWeatherPerHourModel>
) : RecyclerView.Adapter<WeatherItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTime: TextView = itemView.findViewById(R.id.txt_item_time_weather_per_hour)
        val btnArrow: ImageButton = itemView.findViewById(R.id.btn_arrow)
        val imgIcon: ImageView = itemView.findViewById(R.id.weather_per_hour_item_icon_for_time)
        val txtDegree: TextView = itemView.findViewById(R.id.weather_per_hour_item_txt_degree)
        val txtWind: TextView = itemView.findViewById(R.id.weather_per_hour_item_txt_dir_wind)
        val imgRainIcon: ImageView =
            itemView.findViewById(R.id.weather_per_hour_item_icon_rainfall_rate)
        val txtRainRate: TextView =
            itemView.findViewById(R.id.weather_per_hour_item_txt_rainfall_rate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather_per_hour, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtTime.text = item.time

    }
}
