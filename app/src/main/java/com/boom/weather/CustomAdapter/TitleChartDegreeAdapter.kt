package com.boom.weather.CustomAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boom.weather.FrameWork.Data.LocalStorageManager
import com.boom.weather.Model.EstablishModel
import com.boom.weather.Model.Object.KeysStorage
import com.boom.weather.Model.TitleChartItemModel
import com.boom.weather.R
import com.boom.weather.Utils.WeatherUtil

class TitleChartDegreeAdapter(
    private val items: List<TitleChartItemModel>
) : RecyclerView.Adapter<TitleChartDegreeAdapter.ViewHolder>() {

    private var _dataEstablish: EstablishModel = LocalStorageManager.getObject<EstablishModel>(
        KeysStorage.establish, EstablishModel::class.java
    ) ?: EstablishModel()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txt_rainfall_rate: TextView = itemView.findViewById(R.id.txt_rainfall_rate)
        val txt_time: TextView = itemView.findViewById(R.id.txt_time)
        val icon_status_weather: ImageView = itemView.findViewById(R.id.icon_status_weather)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.title_chart_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txt_time.text = item.time
        holder.txt_rainfall_rate.text = "${item.rainPercent}%"

        val iconName = WeatherUtil.getWeatherIconName(item.weatherIconUrl, item.isDay)
        val context = holder.itemView.context
        val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
        if (resId != 0) {
            holder.icon_status_weather.setImageResource(resId)
        } else {
            holder.icon_status_weather.setImageResource(R.drawable.icon_weather_1)
        }
    }

//    fun clearItems() {
//        items.clear()  // Xóa tất cả các phần tử trong danh sách
//        notifyDataSetChanged()  // Thông báo RecyclerView cập nhật lại giao diện
//    }
}
