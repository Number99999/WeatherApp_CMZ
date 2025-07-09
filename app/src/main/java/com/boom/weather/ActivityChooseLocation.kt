package com.boom.weather

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.weather.CustomAdapter.LocationInMapAdapter
import com.boom.weather.Manager.AdManager
import com.boom.weather.Manager.LocaleManager
import com.boom.weather.Manager.NetworkManager
import com.boom.weather.Model.LocationInMapModel
import com.boom.weather.Model.LocationWeatherModel
import com.boom.weather.Model.Object.PermissionModel
import com.boom.weather.Service.Interface.GetCurrentLocationCallback
import com.boom.weather.Service.LocationService
import com.boom.weather.Utils.WeatherUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class ActivityChooseLocation : BaseActivity(), OnMapReadyCallback {
    private lateinit var myMap: GoogleMap  // Holds the map reference
    private var currentMarker: Marker? = null  // Holds the current marker
    private lateinit var searchViewMap: SearchView  // Holds the search view reference
    private lateinit var searchViewCity: SearchView  // Holds the search view reference
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_location)
        enableEdgeToEdge()

        findViewById<ImageView>(R.id.btn_back_city).setOnClickListener { finish() }
        val notificationTextView = findViewById<TextView>(R.id.txt_title)
        notificationTextView.isSelected = true

        if (NetworkManager.getInstance(this).isConnected() == false) {
            handleNotConnection()
            return;
        }

        searchViewCity = findViewById(R.id.search_view_city)
//        val btnCloseTxtViewCity = searchViewCity.findViewById<ImageView>(
//            androidx.appcompat.R.id.search_close_btn
//        )
//        btnCloseTxtViewCity?.visibility = View.GONE
        searchViewCity.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchLocation(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        searchViewMap = findViewById(R.id.search_view_map)
        val btnCloseSearchViewMap = searchViewMap.findViewById<ImageView>(
            androidx.appcompat.R.id.search_close_btn
        )
        btnCloseSearchViewMap?.visibility = View.GONE
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)

        findViewById<ImageView>(R.id.icon_map).setOnClickListener {
            findViewById<LinearLayout>(R.id.contain_city).visibility = View.GONE
            findViewById<LinearLayout>(R.id.contain_map).visibility = View.VISIBLE
        }
        findViewById<ImageView>(R.id.btn_back_map).setOnClickListener {
            findViewById<LinearLayout>(R.id.contain_city).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.contain_map).visibility = View.GONE
        }

        initEventOnTypingSearchViewMap()
        SetUpListBigCountry()

        findViewById<ImageView>(R.id.btn_close_title).setOnClickListener {
            (findViewById<ImageView>(R.id.btn_close_title).parent as View).visibility = View.GONE
        }
        this.loadNativeAds()
    }

    private fun handleNotConnection() {
        findViewById<RecyclerView>(R.id.recyclerViewCity).visibility = View.GONE
        findViewById<FrameLayout>(R.id.ad_container).visibility = View.GONE
    }

    private fun searchLocation(query: String?) {
        val location = query ?: return
        val locale = Locale(LocaleManager(this).getSavedLanguage())
        val geocoder = Geocoder(this, locale)
        try {
            val addressList = geocoder.getFromLocationName(location, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)

                currentMarker?.remove()
                currentMarker = myMap.addMarker(MarkerOptions().position(latLng).title(location))
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                setUIRecycleViewMap(addressList)
                setUIRecycleViewCity(addressList)
            } else {
                Toast.makeText(this, getString(R.string.string16), Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadNativeAds() {
        var adMgr = AdManager.getInstance(this@ActivityChooseLocation);
        val contain = findViewById<FrameLayout>(R.id.ad_container)
        contain.visibility = View.GONE
        adMgr.loadNativeClickAd(contain, onAdLoaded = {
            contain.visibility = View.VISIBLE
            println("onAdLoaded")
        }, onAdFailed = { println("onAdFailed") }, onAdImpression = {
            contain.visibility = View.GONE
            println("onAdImpression")
        })
    }

    private fun setUIRecycleViewCity(addressList: List<Address>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCity)
        val listModel = mutableListOf<LocationInMapModel>()

        for (i in addressList) {
            val l = LatLng(i.latitude, i.longitude)
            val detail = WeatherUtil.getLocationFromAddressLines(i.getAddressLine(0))
            val title = detail.split(", ").firstOrNull() ?: ""

            listModel.add(LocationInMapModel(l, title, detail))
        }

        val adapter = LocationInMapAdapter(listModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun initEventOnTypingSearchViewMap() {
        searchViewMap.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = query ?: return false

                val geocoder = Geocoder(this@ActivityChooseLocation)
                try {
                    val addressList = geocoder.getFromLocationName(location, 1)
                    if (addressList != null && addressList.isNotEmpty()) {
                        currentMarker?.remove();

                        setUIRecycleViewMap(addressList)
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)

                        currentMarker =
                            myMap.addMarker(MarkerOptions().position(latLng).title(location))
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    } else {
                        Toast.makeText(
                            this@ActivityChooseLocation,
                            getString(R.string.string16),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

    private fun SetUpListBigCountry() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewCity)
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

    private fun getCurLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                PermissionModel.REQUEST_LOCATION
//            )
            return
        }

        LocationService.getCurrentLocation(object : GetCurrentLocationCallback {
            @Override
            override fun onLocationReceived(address: LocationWeatherModel) {
                val lat = address.latitude
                val lng = address.longitude
                currentMarker?.remove()
                val currentLatLng = LatLng(lat, lng)
                currentMarker = myMap.addMarker(
                    MarkerOptions().position(currentLatLng).title(getString(R.string.string18))
                )
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }

            override fun onError(exception: Exception) {
                Toast.makeText(
                    this@ActivityChooseLocation, getString(R.string.string16), Toast.LENGTH_SHORT
                ).show()
                exception.printStackTrace();
            }
        })
    }

    @Override
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionModel.REQUEST_LOCATION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurLocation()
            } else {
                Toast.makeText(
                    this, getString(R.string.string17), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setUIRecycleViewMap(addressList: List<Address>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMap)
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

    private fun setEvent() {
        myMap.uiSettings.isZoomControlsEnabled = true
        searchViewMap.isIconified = false
        searchViewMap.setOnClickListener {
            if (searchViewMap.isIconified) {
                searchViewMap.isIconified = false
                searchViewMap.requestFocus()
            }
        }

        searchViewCity.isIconified = false
        searchViewCity.setOnClickListener {
            if (searchViewCity.isIconified) {
                searchViewCity.isIconified = false
                searchViewCity.requestFocus()
            }
        }
    }

    private fun setIsMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                PermissionModel.REQUEST_LOCATION
//            )
        } else {
            myMap.isMyLocationEnabled = true
        }
    }

    private fun setOnMapClicked() {
        myMap.setOnMapClickListener { pos: LatLng ->
            currentMarker?.remove()
            currentMarker = myMap.addMarker(MarkerOptions().position(pos))

            myMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(pos, myMap.cameraPosition.zoom),
                500,
                object : GoogleMap.CancelableCallback {
                    override fun onCancel() {}
                    override fun onFinish() {
                        getAddressFromLatLng(pos)
                    }
                })
        }
    }

    private fun getAddressFromLatLng(latLng: LatLng) {
        lifecycleScope.launch {
            val address = withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(this@ActivityChooseLocation, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        addresses[0]
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
//            Toast.makeText(this@ActivityChooseLocationWithMap, address, Toast.LENGTH_SHORT).show()
//            currentMarker?.title = address
            currentMarker?.showInfoWindow()
            if (address != null) setUIRecycleViewMap(listOf(address));
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap;
        currentMarker?.remove();
        this.setEvent();
        this.getCurLocation();
        this.setIsMyLocation();
        this.setOnMapClicked();

    }


}
