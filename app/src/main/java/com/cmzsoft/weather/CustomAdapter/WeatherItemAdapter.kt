package com.cmzsoft.weather.CustomAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.FrameWork.Data.LocalStorageManager
import com.cmzsoft.weather.Model.DataWeatherPerHourModel
import com.cmzsoft.weather.Model.EstablishModel
import com.cmzsoft.weather.Model.Object.KeysStorage
import com.cmzsoft.weather.R
import com.cmzsoft.weather.Utils.WeatherUtil

@SuppressLint("SetTextI18n")
class WeatherItemAdapter(
    private val items: List<DataWeatherPerHourModel>
) : RecyclerView.Adapter<WeatherItemAdapter.ViewHolder>() {

    private var _dataEstablish: EstablishModel = LocalStorageManager.getObject<EstablishModel>(
        KeysStorage.establish, EstablishModel::class.java
    )

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
        holder.txtWind.text = "${(item.winDir)}, ${(item.winSpeed.toString())}kph";
        holder.txtTime.text = WeatherUtil.convertHourToCurType(
            item.time.substring(item.time.length - 5),
            _dataEstablish.is24h
        );
        holder.txtDegree.text = "${
            WeatherUtil.convertToCurTypeTemp(
                item.tempC.toDouble(), _dataEstablish.typeTemp
            )
        }°${_dataEstablish.typeTemp}"
        val nameIcon = WeatherUtil.getWeatherIconName(item.iconCode, item.isDay)
        val context = holder.itemView.context
        val resId = context.resources.getIdentifier(nameIcon, "drawable", context.packageName)
        holder.imgIcon.setImageResource(resId)
        holder.txtRainRate.text = "${item.changeRain}%"
    }
}
