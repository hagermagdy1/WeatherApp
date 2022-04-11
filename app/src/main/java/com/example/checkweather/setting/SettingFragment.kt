package com.example.checkweather.setting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import com.example.checkweather.MainActivity
import com.example.checkweather.dialog.view.NoInternetDialog
import com.example.checkweather.R
import com.example.checkweather.getSharedPreferences
import com.example.checkweather.map.view.MapActivity
import com.google.android.gms.location.*
import java.util.*

class SettingFragment : Fragment() {
    val PERMISSION_ID = 1
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var locationRadioGroup :RadioGroup
    private lateinit var gpsRadioBtn :RadioButton
    private lateinit var locationRadioBtn :RadioButton

    private lateinit var languageRadioGroup :RadioGroup
    private lateinit var englishRadioBtn :RadioButton
    private lateinit var arabicRadioBtn :RadioButton

    private lateinit var unitsRadioGroup :RadioGroup
    private lateinit var celRadioBtn :RadioButton
    private lateinit var ferhRadioBtn :RadioButton
    private lateinit var kelRadioBtn :RadioButton

    private lateinit var speedRadioGroup :RadioGroup
    private lateinit var mPerSecRadioBtn :RadioButton
    private lateinit var mPerHourRadioBtn :RadioButton


    private lateinit var btnSave :Button

    private lateinit var newTempSetting: String
    private lateinit var newLanguageSetting: String
    private lateinit var newLocationSetting: String
    private lateinit var newSpeedSetting: String

    private lateinit var oldTempSetting: String
    private lateinit var oldLanguageSetting: String
    private lateinit var oldSpeedSetting: String

    private lateinit var oldLocationSetting: String
   lateinit var language:String;
lateinit var updateBtn:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        getSharedPreferences(requireActivity()).apply {
            language = getString(getString(R.string.languageSettings), "en")!!
        }
        var view :View =  inflater.inflate(R.layout.fragment_setting, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
           saveSharedPrefernce()
         getFreshLocation()
        setSettings()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationRadioGroup=view.findViewById(R.id.radioGroupLocation)
        languageRadioGroup=view.findViewById(R.id.radioGroupLanguage)
        unitsRadioGroup=view.findViewById(R.id.radioGroupUnits)
        gpsRadioBtn=view.findViewById(R.id.settings_location)
        locationRadioBtn= view.findViewById(R.id.settings_map)
        arabicRadioBtn=view.findViewById(R.id.settings_arabic)
        englishRadioBtn=view.findViewById(R.id.settings_english)
        kelRadioBtn=view.findViewById(R.id.unit_kelvin)
        ferhRadioBtn=view.findViewById(R.id.unit_fahrenheit)
        celRadioBtn=view.findViewById(R.id.unit_celsius)
        btnSave=view.findViewById(R.id.btn_save)
        updateBtn=view.findViewById(R.id.btnUpdateLocataion)

        speedRadioGroup=view.findViewById(R.id.speedRadioGroup)
        mPerSecRadioBtn=view.findViewById(R.id.mPerSRadioBtn)
        mPerHourRadioBtn=view.findViewById(R.id.mPerHRadioBtn)


        updateBtn.setOnClickListener{
            if (checkPermission()) {
                if (isLocationEnabled()) {
                    getLocationSettings()
                }
                else{
                    val intent = Intent(requireContext(), NoInternetDialog::class.java)
                    startActivity(intent)

                }
            }
        }

        btnSave.setOnClickListener {
            saveSharedPrefernce()
            save()
        }

    }


    private fun getUnitSettings() {
        when (unitsRadioGroup.checkedRadioButtonId) {
            R.id.unit_celsius -> newTempSetting = "c"
            R.id.unit_fahrenheit -> newTempSetting = "f"
            R.id.unit_kelvin -> newTempSetting = "k"
        }

    }

    private fun getSpeedSettings() {
        when (speedRadioGroup.checkedRadioButtonId) {
            R.id.mPerSRadioBtn -> newSpeedSetting = "s"
            R.id.mPerHRadioBtn -> newSpeedSetting = "h"
        }

    }

    private fun getLanguagesSettings() {
        when (languageRadioGroup.checkedRadioButtonId) {
            R.id.settings_arabic -> newLanguageSetting = "ar"
            R.id.settings_english -> newLanguageSetting = "en"
        }

    }

    private fun getLocationSettings() {
        when (locationRadioGroup.checkedRadioButtonId) {
            R.id.settings_map -> { newLocationSetting ="map"
                val intent = Intent(requireContext(), MapActivity::class.java)
                activity?.finish()
                startActivity(intent)
            }

            R.id.settings_location -> {
                newLocationSetting = "jps"
                val intent = Intent(requireContext(), MainActivity::class.java)
                 startActivity(intent)
            }
        }
        getSharedPreferences(requireContext()).edit().apply {
            this.putString(getString(R.string.locationnSetting), newLocationSetting).commit()
        }
    }


    fun save (){
        getSharedPreferences(requireContext()).edit().apply {
            this.putString(getString(R.string.languageSettings), newLanguageSetting).commit()
        }
        getSharedPreferences(requireContext()).edit().apply {
            this.putString(getString(R.string.speedSetting), newSpeedSetting).commit()
        }
        getSharedPreferences(requireContext()).edit().apply {
            this.putString(getString(R.string.tempSetting), newTempSetting).commit()
        }
    }

    private fun setSettings() {
        getSettingsFromSharedPreferences()
        when (oldTempSetting) {
            "c" ->celRadioBtn.isChecked = true
            "f" -> ferhRadioBtn.isChecked = true
            "k" -> kelRadioBtn.isChecked = true
        }
        when (oldLanguageSetting) {
            "ar" -> arabicRadioBtn.isChecked = true
            "en" -> englishRadioBtn.isChecked = true
        }
        when (oldLocationSetting) {
            "jps" -> {gpsRadioBtn.isChecked = true
                if (checkPermission()) {
                    if (isLocationEnabled()) {
                        getFreshLocation()
                        getSharedPreferences(requireContext()).edit().apply {
                            this.putString(getString(R.string.locationnSetting), "jps").commit()

                        }
                    }
                        getSharedPreferences(requireContext()).edit().apply {
                            this.putString(getString(R.string.locationnSetting), "jps").commit()
                        }

                } else {
                    requestPermissions()
                }
            }
            "map" -> {
                locationRadioBtn.isChecked = true

            }
        }
        when (oldSpeedSetting) {
            "s" -> mPerSecRadioBtn.isChecked = true
            "h" -> mPerHourRadioBtn.isChecked = true
        }

    }
    fun isLocationEnabled(): Boolean {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    fun checkPermission(): Boolean {


        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }
    fun getFreshLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 120000
        locationRequest.fastestInterval = 120000
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

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
            Log.i("TAG", "onLocationResult: gbss  settings " + location.longitude)
            Log.i("TAG", "onLocationResult: gbss  settings" + location.latitude)
            saveLocationInSharedPreferences(location.longitude, location.latitude)
        }
    }
    private fun saveLocationInSharedPreferences(long: Double, lat: Double) {
        val editor = getSharedPreferences(requireContext()).edit()
        editor.putString(getString(R.string.lon), long.toString())
        editor.putString(getString(R.string.lat), lat.toString())
        editor.apply()
    }

    private fun getSettingsFromSharedPreferences() {

        getSharedPreferences(requireContext()).apply {
            oldTempSetting = getString(getString(R.string.tempSetting), "k")!!
            oldLanguageSetting = getString(getString(R.string.languageSettings),
                Locale.getDefault().toString()
            )!!
            oldSpeedSetting = getString(getString(R.string.speedSetting),
                "s"
            )!!

            oldLocationSetting = getString(getString(R.string.locationnSetting),
                "jps"
            )!!

            Log.i("Setting", "getSettingsFromSharedPreferences: "+ oldTempSetting)
            Log.i("Setting", "getSettingsFromSharedPreferences: "+ oldLanguageSetting)
            Log.i("Setting", "getSettingsFromSharedPreferences: "+ oldSpeedSetting)
            Log.i("Setting", "getSettingsFromSharedPreferences: "+ oldLocationSetting)
        }
    }

    private fun saveSharedPrefernce(){
        getUnitSettings()
        getSpeedSettings()
        getLanguagesSettings()
    }

}