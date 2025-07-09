package com.boom.weather.CustomAdapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.boom.weather.ActivityChooseLocation
import com.boom.weather.MainActivity
import com.boom.weather.Manager.AdManager
import com.boom.weather.Model.FakeGlobal
import com.boom.weather.Model.LocationWeatherModel
import com.boom.weather.R
import com.boom.weather.Service.DatabaseService
import com.boom.weather.Service.LocationService

class ItemLocationManagerAdapter(private var items: MutableList<LocationWeatherModel>) :
    RecyclerView.Adapter<ItemLocationManagerAdapter.ViewHolder>() {
    private var isClicked: Boolean = false;

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.txt_title_location)
        var fullPath = itemView.findViewById<TextView>(R.id.txt_path_location)
        var iconStatus = itemView.findViewById<ImageView>(R.id.icon_status)
        var txtDefault = itemView.findViewById<TextView>(R.id.txt_default)
        var contain = itemView.findViewById<LinearLayout>(R.id.contain_card)
        var txtAdd = itemView.findViewById<TextView>(R.id.txt_add)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location_manager, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size
    private fun loadNativeAds(ctx: View) {
        var adMgr = AdManager.getInstance(ctx.context);
        val container = ctx.findViewById<FrameLayout>(R.id.contain_ads)
        container.visibility = View.GONE
        adMgr.loadNativeClickAd(container, onAdLoaded = {
            println("onAdLoaded")
            container.visibility = View.VISIBLE
        }, onAdFailed = { println("onAdFailed") }, onAdImpression = {
            println("onAdImpression")
            container.visibility = View.GONE
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        if (position == items.lastIndex) {
            holder.contain.visibility = View.GONE
            holder.txtAdd.visibility = View.VISIBLE
            holder.itemView.setOnClickListener {
                val changePage = Intent(context, ActivityChooseLocation::class.java);
                context.startActivity(changePage)

                Handler(Looper.getMainLooper()).postDelayed({
                    it.isClickable = true // Bật lại click sau 1 giây
                }, 1000)
            }
            return;
        }
        val defaultAdd =
            DatabaseService.getInstance(context.applicationContext).locationWeatherService.getDefaultLocationWeather();
        holder.txtDefault.visibility = if (item.isDefaultLocation == 1) {
            View.VISIBLE
        } else {
            View.GONE
        }
        if (position == 0) {
            val isExis =
                DatabaseService.getInstance(holder.itemView.context).locationWeatherService.checkIsExistLocationInDb(
                    item
                )
            val isCurrent = LocationService.checkPermissionLocation() && isExis == false
            if (isCurrent) {
                holder.title.text = "${item.name} (Your location)"
            } else holder.title.text = "${item.name}"
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
                FakeGlobal.getInstance().isShowConfirmDefault = (holder.txtDefault.isGone)
            }
        } else {
            holder.title.text = item.name
            holder.contain.setOnClickListener {
                if (item.isEdit == true) return@setOnClickListener
                FakeGlobal.getInstance().isShowConfirmDefault = item.isDefaultLocation == 0
                FakeGlobal.getInstance().curLocation = item
                FakeGlobal.getInstance().flagIsChooseDefaultLocation = false
                val changeIntent = Intent(context, MainActivity::class.java)
                changeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                context.startActivity(changeIntent)
            }
        }
        holder.fullPath.text = shortPathLocation(item.fullPathLocation)
        val btn_delete = holder.itemView.findViewById<ImageView>(R.id.btn_delete)
        btn_delete.visibility = if (position != 0) View.VISIBLE else View.GONE
        btn_delete.setOnClickListener {
            items.remove(item)
            DatabaseService.getInstance(context.applicationContext).locationWeatherService.deleteItemInTable(
                item
            );
            notifyDataSetChanged()
        }
        if (item.weather != null && item.weather.isNotEmpty()) {
            val weatherIcon = item.weather.toIntOrNull()
            if (weatherIcon != null) holder.itemView.findViewById<ImageView>(R.id.icon_status)
                .setImageResource(weatherIcon);
        }

        if (position % 2 == 1 && position != items.lastIndex) {
            loadNativeAds(holder.itemView)
        }
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
