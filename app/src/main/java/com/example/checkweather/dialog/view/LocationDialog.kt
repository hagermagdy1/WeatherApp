package com.example.checkweather.dialog.view
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager

import android.widget.Toast

import com.google.android.gms.location.*

import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import com.example.checkweather.MainActivity
import com.example.checkweather.R
import com.example.checkweather.map.view.MapActivity

class LocationDialog : AppCompatActivity() {
    val PERMISSION_ID = 1

    lateinit var btnOk:Button
    lateinit var radioBtnGroup:RadioGroup
    lateinit var gbsClicked :RadioButton
    lateinit var mapsClicked :RadioButton
    lateinit var notificationChecked: SwitchCompat
    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_dialog)
        btnOk = findViewById(R.id.btnOk)
        radioBtnGroup = findViewById(R.id.radioGroup)
        gbsClicked = findViewById(R.id.gpsRadioBtn)
        mapsClicked = findViewById(R.id.mapRadioBtn)
        val editor = com.example.checkweather.getSharedPreferences(this).edit()
        editor.putString(getString(R.string.firstOpen), "false")
        editor.commit()
        val intent = Intent(applicationContext, MapActivity::class.java)
        btnOk.setOnClickListener {

            when (radioBtnGroup.checkedRadioButtonId) {
                R.id.mapRadioBtn ->
                {
                    com.example.checkweather.getSharedPreferences(this).edit().apply {
                        this.putString(getString(R.string.locationnSetting), "map").commit()
                    }
                    startActivity(intent)
                }

                R.id.gpsRadioBtn ->
                    if (checkPermission()) {
                        if (isLocationEnabled()) {
                            Toast.makeText(this, "Okayyy", Toast.LENGTH_SHORT).show();
                                    getFreshLocation()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            com.example.checkweather.getSharedPreferences(this).edit().apply {
                                this.putString(getString(R.string.locationnSetting), "jps").commit()
                            }
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Please enable the location", Toast.LENGTH_SHORT)
                                .show();
                            com.example.checkweather.getSharedPreferences(this).edit().apply {
                                this.putString(getString(R.string.locationnSetting), "jps").commit()
                            }
                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        }
                    } else {
                                 requestPermissions()
                    }
            }
        }
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    fun checkPermission(): Boolean {

        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }
    override fun onResume() {
        super.onResume()
        getFreshLocation()
    }
    fun getFreshLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 120000
        locationRequest.fastestInterval = 120000
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient?.apply {
                    requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper())
                }
            }
        }
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            Log.i("TAG", "onLocationResult: gbss " + location.longitude)
            Log.i("TAG", "onLocationResult: gbss " + location.latitude)
            saveLocationInSharedPreferences(location.longitude, location.latitude)
        }
    }
    private fun saveLocationInSharedPreferences(long: Double, lat: Double) {
        val editor = com.example.checkweather.getSharedPreferences(this).edit()
        editor.putString(getString(R.string.lon), long.toString())
        editor.putString(getString(R.string.lat), lat.toString())
        editor.apply()
    }
}

