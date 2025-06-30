package com.cmzsoft.weather.CustomAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.MainActivity
import com.cmzsoft.weather.Model.FakeGlobal
import com.cmzsoft.weather.Model.LocationInMapModel
import com.cmzsoft.weather.R
import com.cmzsoft.weather.Service.DatabaseService
import com.cmzsoft.weather.Service.LocationService
import com.google.android.gms.maps.model.LatLng

class LocationInMapAdapter(private val items: List<LocationInMapModel>) :
    RecyclerView.Adapter<LocationInMapAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemLocation = itemView.findViewById<LinearLayout>(R.id.item_location)
        val title = itemView.findViewById<TextView>(R.id.txt_title)
        val detail = itemView.findViewById<TextView>(R.id.txt_detail)
        var loc: LatLng? = null;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location_choose_location_with_map, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title;
        holder.detail.text = item.detail;
        holder.loc = item.loc;

        val context = holder.itemView.context
        holder.itemLocation.setOnClickListener {
            FakeGlobal.getInstance().curLocation =
                LocationService.getLocationFromLatLon(item.loc.latitude, item.loc.longitude);
            val isExist =
                DatabaseService.getInstance(context.applicationContext).locationWeatherService.checkIsExistLocationInDb(
                    FakeGlobal.getInstance().curLocation
                )
            if (isExist == false) {
                DatabaseService.getInstance(context.applicationContext).locationWeatherService.insertLocationWeather(
                    FakeGlobal.getInstance().curLocation
                );
            }
//            FakeGlobal.getInstance().flagIsChooseDefaultLocation = true
            FakeGlobal.getInstance().isShowConfirmDefault=true
            val changeIntent = Intent(context, MainActivity::class.java);
            changeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(changeIntent)
        }
    }
}
