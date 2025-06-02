package com.cmzsoft.weather

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.CustomAdapter.LocationInMapAdapter
import com.cmzsoft.weather.Model.LocationInMapModel
import com.cmzsoft.weather.Utils.WeatherUtil
import com.google.android.gms.maps.model.LatLng
import java.io.IOException

class ActivityBigCountry : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_big_country)
        SetUpListBigCountry()
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        initEventSearchView()
    }

    private fun SetUpListBigCountry() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val bigCities = listOf(
            LocationInMapModel(LatLng(40.7128, -74.0060), "New York", "USA"),
            LocationInMapModel(LatLng(34.0522, -118.2437), "Los Angeles", "USA"),
            LocationInMapModel(LatLng(51.5074, -0.1278), "London", "UK"),
            LocationInMapModel(LatLng(35.6895, 139.6917), "Tokyo", "Japan"),
            LocationInMapModel(LatLng(48.8566, 2.3522), "Paris", "France"),
            LocationInMapModel(LatLng(39.9042, 116.4074), "Beijing", "China"),
            LocationInMapModel(LatLng(19.0760, 72.8777), "Mumbai", "India"),
            LocationInMapModel(LatLng(-23.5505, -46.6333), "São Paulo", "Brazil"),
            LocationInMapModel(LatLng(55.7558, 37.6173), "Moscow", "Russia"),
            LocationInMapModel(LatLng(10.762622, 106.660172), "Ho Chi Minh City", "Vietnam")
        )
        val adapter = LocationInMapAdapter(bigCities)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun initEventSearchView() {
        val searchView = findViewById<SearchView>(R.id.search_view)

        searchView.setOnClickListener {
            if (searchView.isIconified) {
                searchView.isIconified = false
                searchView.requestFocus()
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                val location = query ?: return false

                val geocoder = Geocoder(this@ActivityBigCountry)
                try {
                    val addressList = geocoder.getFromLocationName(location, 1)
                    if (addressList != null && addressList.isNotEmpty()) {
                        setUIRecycleView(addressList)
                    } else {
                        Toast.makeText(
                            this@ActivityBigCountry, "Không tìm thấy địa điểm", Toast.LENGTH_SHORT
                        ).show()
                    }
                    searchView.onActionViewCollapsed()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setUIRecycleView(addressList: List<Address>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val listModel = mutableListOf<LocationInMapModel>()
        for (i in addressList) {
            val l = LatLng(i.latitude, i.longitude);
            var detail = WeatherUtil.getLocationFromAddressLines(i.getAddressLine(0));
            val title = detail.split(", ").firstOrNull() ?: ""

            listModel.add(
                LocationInMapModel(
                    l, title, detail
                )
            )
        }
        val adapter = LocationInMapAdapter(listModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }
}