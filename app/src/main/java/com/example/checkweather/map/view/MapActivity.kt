package com.example.checkweather.map.view
import MapViewModel
import MapViewModelFactory
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.checkweather.MainActivity
import com.example.checkweather.R
import com.example.checkweather.database.LocalSourceConcreteClass
import com.example.checkweather.model.Favorite
import com.example.checkweather.model.Repository
import com.example.checkweather.network.APIClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import java.io.IOException
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var googleMap: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private var lastKnownLocation: Location? = null
    lateinit var search: ImageView
    lateinit var adressEdit: EditText
    lateinit var latMarker: String
    lateinit var lonMarker: String
    var marker: Marker? = null
    lateinit var btnDone: Button
    lateinit var viewModel: MapViewModel
    lateinit var mapFactory: MapViewModelFactory
    lateinit var fav: Favorite
    lateinit var isFav: String
    lateinit var mapFragment: SupportMapFragment
    lateinit var addToFavBtn: ImageView
    lateinit var locationMarker: String
    lateinit var counteryNameResult:String
    lateinit var cityNameResult:String
    lateinit var concatAddress:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        search = findViewById(R.id.imageView)
        btnDone = findViewById(R.id.btnDone)
        addToFavBtn = findViewById(R.id.addToFav)

        mapFragment =
            (supportFragmentManager.findFragmentById(R.id.mapContainer) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this)
        mapFactory = MapViewModelFactory(
            Repository.getInstance(
                APIClient.getInstance()!!,
                LocalSourceConcreteClass(this), this
            )
        )
        viewModel = ViewModelProvider(this, mapFactory).get(MapViewModel::class.java)
        addToFavBtn.visibility = View.INVISIBLE
        mapFragment.getMapAsync(clicked)
        com.example.checkweather.getSharedPreferences(this).apply {
            isFav = getString(getString(R.string.isFav), "")!!
            Log.i("TAG", "onCreate: is fav " + isFav)
        }

        if (isFav.equals("true")) {
            btnDone.visibility = View.INVISIBLE
            setIsFavToSharedPreferences()
        } else {
            btnDone.visibility = View.INVISIBLE
        }

        addToFavBtn.setOnClickListener {
            val add = getAddress(latMarker.toDouble(),lonMarker.toDouble())

            fav = Favorite(lat = latMarker, lon = lonMarker, location = add.toString())
            goToFavourite(fav)
            finish()
        }
        btnDone.setOnClickListener {
            saveMapInSharedPreferences(latMarker, lonMarker)

           // Toast.makeText(this, "done btn", Toast.LENGTH_SHORT).show()

            finish()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        adressEdit = findViewById<View>(R.id.search) as EditText
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapContainer) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        search!!.setOnClickListener(View.OnClickListener {
            try {
                findLocationSearch()
            } catch (e: IOException) {
                Toast.makeText(this, "Please Enter Valid Location", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (googleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, googleMap!!.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true
        try {
            findLocation()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun findLocationSearch() {
        val location = adressEdit!!.text.toString()
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocationName(location, 1)
        var add = list[0]
        val locality = add.locality
        val ll = LatLng(add.latitude, add.longitude)
        val update = CameraUpdateFactory.newLatLngZoom(ll, 15f)
        googleMap!!.moveCamera(update)
        val markerOptions = MarkerOptions()
            .title(locality)
            .position(LatLng(add.latitude, add.longitude))
        marker = googleMap!!.addMarker(markerOptions)
//        Toast.makeText(this, "Your Lon and lag add.lat " + add.latitude, Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "Your Lon and lag add.long " + add.longitude, Toast.LENGTH_SHORT)
//            .show()
    }

    private fun goToFavourite(fav: Favorite) {
        viewModel.insertFav(fav)
    }

    private val clicked = OnMapReadyCallback { googleMap ->
        val cairo = LatLng(30.0444, 31.2357)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cairo, 10.0f))
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnMapClickListener { location ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(location))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f))
            locationMarker = location.toString()
            latMarker = location.latitude.toString()
            lonMarker = location.longitude.toString()
//            Toast.makeText(this, "markerrrrrr" + latMarker, Toast.LENGTH_SHORT).show()
//            Toast.makeText(this, "markerrrrrr" + lonMarker, Toast.LENGTH_SHORT).show()
               btnDone.visibility = View.VISIBLE
            // saveMapInSharedPreferences(add.latitude.toString(),add.longitude.toString())
            if (isFav.equals("true")) {
                addToFavBtn.visibility = View.VISIBLE
                btnDone.visibility = View.INVISIBLE

            } else {
                btnDone.visibility = View.VISIBLE
            }
        }

    }

    @Throws(IOException::class)
    fun findLocation() {
        var lat = 30.044
        var lon = 31.235

        val ll = LatLng(lat, lon)
        val update = CameraUpdateFactory.newLatLngZoom(ll, 15f)
        googleMap!!.moveCamera(update)
        if (marker != null) marker!!.remove()
        val markerOptions = MarkerOptions()
            .title("MainActivity.local")
            .position(LatLng(lat, lon))
        marker = googleMap!!.addMarker(markerOptions)
    }

    companion object {
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }

    private fun saveMapInSharedPreferences(lat: String, long: String) {
        val editor = com.example.checkweather.getSharedPreferences(this).edit()
        editor.putString(getString(R.string.lat), lat.toString())
        editor.putString(getString(R.string.lon), long.toString())
        editor.commit()
    }

    private fun setIsFavToSharedPreferences() {
        val editor = com.example.checkweather.getSharedPreferences(this).edit()
        editor.putString(this.getString(R.string.isFav), "false")
        editor.apply()
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        try {
            var language:String
            com.example.checkweather.getSharedPreferences(this).apply {
                language = getString(getString(R.string.languageSettings), "en")!!
            }
            val geocoder = Geocoder(this,Locale(language))
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.size > 0) {
                counteryNameResult= addresses[0].countryName.toString()
                cityNameResult=addresses[0].adminArea.toString()
                concatAddress= cityNameResult+","+counteryNameResult
            }
        }
        catch (e: IOException) {
            Log.e("TAG", e.localizedMessage)
        }
        return  concatAddress
    }
}