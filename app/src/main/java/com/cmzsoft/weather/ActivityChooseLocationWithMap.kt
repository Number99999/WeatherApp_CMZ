package com.cmzsoft.weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cmzsoft.weather.DatabaService.DatabaseService
import com.cmzsoft.weather.Model.LocationWeatherModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
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

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

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
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)

                        currentMarker =
                            myMap.addMarker(MarkerOptions().position(latLng).title(location))
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

                        Toast.makeText(
                            this@ActivityChooseLocationWithMap,
                            addressList.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
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
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_PERMISSION_CODE
            )
            return
        }

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }
    }

    private fun initDataEventBack() {
        val btn = findViewById<ImageButton>(R.id.btn_back)
        btn.setOnClickListener {
            val changePage = Intent(this, MainActivity::class.java);
            startActivity(changePage);
        }
    }

    private fun getCurLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_PERMISSION_CODE
            )
            return
        }

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lat = location.latitude
                    val lng = location.longitude


                    val geocoder = Geocoder(this, Locale.getDefault())
                    var addressLine = "Không rõ địa chỉ"
                    try {
                        val addresses = geocoder.getFromLocation(lat, lng, 1)
                        if (!addresses.isNullOrEmpty()) {
                            addressLine = addresses[0].getAddressLine(0) ?: "Không rõ địa chỉ"
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val model = LocationWeatherModel()
                    model.id = 0L
                    model.name = addressLine
                    model.longitude = lng
                    model.latitude = lat
                    model.weather = ""
                    model.fullPathLocation = addressLine
                    DatabaseService.getInstance(this).locationWeatherService.insertLocationWeather(
                        model
                    )

                    currentMarker?.remove()
                    val currentLatLng = LatLng(lat, lng)
                    currentMarker = myMap.addMarker(
                        MarkerOptions().position(currentLatLng).title("Vị trí hiện tại")
                    )
                    Toast.makeText(this, addressLine, Toast.LENGTH_SHORT).show()
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                } else {
                    Toast.makeText(this, "Không lấy được vị trí", Toast.LENGTH_SHORT).show()
                }
            }

    }

    @Override
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getCurLocation()
            else {
                Toast.makeText(
                    this,
                    "can't get location because permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap;
        currentMarker?.remove();
        this.getCurLocation()
    }
}
