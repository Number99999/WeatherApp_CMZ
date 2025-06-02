package com.cmzsoft.weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmzsoft.weather.CustomAdapter.LocationInMapAdapter
import com.cmzsoft.weather.Model.LocationInMapModel
import com.cmzsoft.weather.Model.PermissionModel
import com.cmzsoft.weather.Utils.WeatherUtil
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

class ActivityChooseLocationWithMap : AppCompatActivity(), OnMapReadyCallback {

    private val FINE_PERMISSION_CODE = 1

    private lateinit var myMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var searchView: SearchView
    private lateinit var mapFragment: SupportMapFragment
    private var currentMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_location_with_map)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurLocation()

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment?.getMapAsync(this)
        searchView = findViewById(R.id.search_view)

        initEventOnTypingSearchView()
        initDataEventBack()
    }

    private fun initEventOnTypingSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = query ?: return false

                val geocoder = Geocoder(this@ActivityChooseLocationWithMap)
                try {
                    val addressList = geocoder.getFromLocationName(location, 1)
                    if (addressList != null && addressList.isNotEmpty()) {
                        currentMarker?.remove();

                        setUIRecycleView(addressList)
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)

                        currentMarker =
                            myMap.addMarker(MarkerOptions().position(latLng).title(location))
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    } else {
                        Toast.makeText(
                            this@ActivityChooseLocationWithMap,
                            "Không tìm thấy địa điểm",
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


    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_PERMISSION_CODE
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    private fun initDataEventBack() {
        val btn = findViewById<ImageView>(R.id.btn_back)
        btn.setOnClickListener {
            val changePage = Intent(this, MainActivity::class.java);
            startActivity(changePage);
        }
    }

    private fun getCurLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_PERMISSION_CODE
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            try {
                if (location != null) {
                    val lat = location.latitude
                    val lng = location.longitude

                    val geocoder = Geocoder(this, Locale.getDefault())
                    var addressLine = "Không rõ địa chỉ"
                    val addresses = geocoder.getFromLocation(lat, lng, 10)
                    if (!addresses.isNullOrEmpty()) {
                        addressLine = addresses[0].getAddressLine(0) ?: "Không rõ địa chỉ"
                        if (addressLine != "Không rõ địa chỉ") setUIRecycleView(listOf(addresses[0]))
                        Toast.makeText(
                            this, WeatherUtil.getLocationFromAddressLines(
                                addresses[0].getAddressLine(0)
                            ), Toast.LENGTH_SHORT
                        ).show()
                    }

//                    val model = LocationWeatherModel()
//                    model.id = 0L
//                    model.name = addressLine
//                    model.longitude = lng
//                    model.latitude = lat
//                    model.weather = ""
//                    model.fullPathLocation = addressLine
//                    DatabaseService.getInstance(this).locationWeatherService.insertLocationWeather(
//                        model
//                    )

                    currentMarker?.remove()
                    val currentLatLng = LatLng(lat, lng)
                    currentMarker = myMap.addMarker(
                        MarkerOptions().position(currentLatLng).title("Vị trí hiện tại")
                    )
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                } else {
                    Toast.makeText(this, "Không lấy được vị trí", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this, "Lỗi khi lấy vị trí: " + e.message.toString(), Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    @Override
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurLocation()
            } else {
                Toast.makeText(
                    this, "can't get location because permission denied", Toast.LENGTH_SHORT
                ).show()
            }
        }
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

    private fun setEvent() {
        myMap.uiSettings.isZoomControlsEnabled = true
        searchView.setOnClickListener {
            if (searchView.isIconified) {
                searchView.isIconified = false
                searchView.requestFocus()
            }
        }
    }

    private fun setIsMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PermissionModel.REQUEST_LOCATION
            )
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
                    val geocoder = Geocoder(this@ActivityChooseLocationWithMap, Locale.getDefault())
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
            if (address != null) setUIRecycleView(listOf(address));
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
