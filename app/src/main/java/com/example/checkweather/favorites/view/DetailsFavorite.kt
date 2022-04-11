package com.example.checkweather.favorites.view

import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkweather.*
import com.example.checkweather.database.LocalSourceConcreteClass
import com.example.checkweather.databinding.FragmentDetailesFavoriteBinding
import com.example.checkweather.favorites.viewmodel.FavoriteDetailsViewModelFactory
import com.example.checkweather.home.HomeViewModelFactory
import com.example.checkweather.home.view.HomeFragment
import com.example.checkweather.home.view.NextDayAdapter
import com.example.checkweather.home.view.NextHourAdapter
import com.example.checkweather.home.viewmodel.HomeViewModel
import com.example.checkweather.model.*
import com.example.checkweather.network.APIClient
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class DetailsFavorite : AppCompatActivity() {
    lateinit var nextDayAdapter: NextDayAdapter
    lateinit var nextHourAdapter: NextHourAdapter
    lateinit var detailsFactory: FavoriteDetailsViewModelFactory
    lateinit var viewModel: FavoritesDetailsViewModel
    lateinit var dayTxtView: TextView
    lateinit var cityTxtView: TextView
    lateinit var dateTxtView: TextView
    lateinit var degreeTxtView: TextView
    lateinit var descriptionTxtView: TextView

    lateinit var pressureTxtView: TextView
    lateinit var speedTxtView: TextView
    lateinit var humidityTxtView: TextView
    lateinit var cloudsTxtView: TextView
    lateinit var visabilityTxtView: TextView
    lateinit var UVTxtView: TextView

    lateinit var nextHoursRecyclerView: RecyclerView
    lateinit var nextDaysRecyclerView: RecyclerView

    private var binding: FragmentDetailesFavoriteBinding? = null
    private var myLocationList = ArrayList<Double>()
    private lateinit var latitude: String
    private lateinit var longitude: String
    lateinit var tempeUnit: String
    lateinit var speedUnit: String
    lateinit var progressBar: ProgressBar
//    lateinit var noInterNetImage:ImageView
    lateinit  var language: String
    //var temp:Double = 0.0
    var newTemp=""
    var newSpeed=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_favorite)
        detailsFactory = FavoriteDetailsViewModelFactory(
            Repository.getInstance(
                APIClient.getInstance()!!,
                LocalSourceConcreteClass(this),
                applicationContext
            )
        )
        getSharedPreferences(this).apply {
            language = getString(getString(R.string.languageSettings), "en")!!
        }
        getValuesFromSharedPreferences()
        val intent = getIntent()
          var lat =intent.getStringExtra("latFav")
          var lon =  intent.getStringExtra("lonFav")


        viewModel = ViewModelProvider(this, detailsFactory).get(FavoritesDetailsViewModel::class.java)
        if (isInternetAvailable()) {
            viewModel.getWeatherFromOnline(
                lat.toString(),
                lon.toString(), language
            )
//            viewModel.getWeatherFromOnline(
//                31.205753.toString(),
//                29.924526.toString(), language
//            )
        }
        initUI()
        setUpNextDayRecyclerView()
        setUpNextHourRecyclerView()


        viewModel.weather.observe(this,{
            if (it != null) {

                tempeUnit = com.example.checkweather.getSharedPreferences(this).getString(
                    getString(R.string.tempSetting),
                    "k"
                )!!
                speedUnit= com.example.checkweather.getSharedPreferences(this).getString(
                    getString(R.string.speedSetting),
                    "s"
                )!!


                if (language == "en") {
                    putEnglishUnits(it)
                } else {
                    convertArabicUnits(it)
                }

                setData(it)

                getNextDayAdapterData(it.daily as java.util.ArrayList<Daily>, tempeUnit)
                getNextHourAdapterData(it.hourly as java.util.ArrayList<Hourly>, tempeUnit)
            }
            else{
                progressBar.visibility = View.VISIBLE

            }
        })
    }

    private fun isInternetAvailable(): Boolean {
        var isAvailable = false
        val manager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.isConnected || manager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE
            )!!
                .isConnected
        ) isAvailable = true
        return isAvailable
    }
    private fun setData(it: WeatherApi) {
        dayTxtView.text = converCalendarToDayOfWeek(Calendar.getInstance(), language)
        dateTxtView.text =
            convertLongToTodayDate(Calendar.getInstance().timeInMillis, language)
        cityTxtView.text = getCityText(this, it.lat, it.lon, language)
    }


    private fun getNextDayAdapterData(daily: java.util.ArrayList<Daily>, tempUnit: String) {
        nextDayAdapter.apply {
            this.daily = daily
            this.tempUnit = tempeUnit
            notifyDataSetChanged()
        }
    }

    private fun getNextHourAdapterData(hourly: java.util.ArrayList<Hourly>, tempUnit: String) {
        nextHourAdapter.apply {
            this.hourly = hourly
            notifyDataSetChanged()
        }
    }


    fun putEnglishUnits(model: WeatherApi) {
        binding.apply {
            setEnglishTemp(model.current.temp)
            setEnglishSpeedUnit(model.current.wind_speed)
            pressureTxtView.text = model.current.pressure.toString().plus(" hPa")
            humidityTxtView.text = model.current.humidity.toString().plus("%")
            cloudsTxtView.text = model.current.clouds.toString().plus("%")
            visabilityTxtView.text = model.current.visibility.toString().plus("m")
            UVTxtView.text = model.current.uvi.toString()
            descriptionTxtView.text=model.current.weather[0].description

            lateinit var weather: Weather

        }
    }

    private fun convertArabicUnits(model: WeatherApi) {
        binding.apply {

            setArabicTemp(model.current.temp)
            setArabicSpeedUnit(model.current.wind_speed)

            humidityTxtView.text = convertNumbersToArabic(model.current.humidity.toDouble())
                .plus("٪")
            pressureTxtView.text = convertNumbersToArabic(model.current.pressure.toDouble())
                .plus(" هب")
            cloudsTxtView.text = convertNumbersToArabic(model.current.clouds.toDouble())
                .plus("٪")
            visabilityTxtView.text = convertNumbersToArabic(model.current.visibility.toDouble())
                .plus("م")
            UVTxtView.text = convertNumbersToArabic(model.current.uvi.toInt().toDouble())

        }
    }



    private fun setUpNextDayRecyclerView() {
        val layoutManager = LinearLayoutManager(HomeFragment().context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        nextDayAdapter = NextDayAdapter(this)
        nextDaysRecyclerView.layoutManager = layoutManager
        nextDaysRecyclerView.setAdapter(nextDayAdapter)
    }
    private fun setUpNextHourRecyclerView() {
        val layoutManager = LinearLayoutManager(HomeFragment().context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        nextHourAdapter = NextHourAdapter(this)
        nextHoursRecyclerView.layoutManager = layoutManager
        nextHoursRecyclerView.setAdapter(nextHourAdapter)
    }
    private fun initUI() {
        progressBar=findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE
        dayTxtView = findViewById(R.id.todayTxtView)
        cityTxtView = findViewById(R.id.cityTxtView)
        dateTxtView = findViewById(R.id.fullDateTxtView)
        degreeTxtView = findViewById(R.id.tempTxtView)
        cityTxtView = findViewById(R.id.cityTxtView)
        descriptionTxtView = findViewById(R.id.descriptionTxtView)

        //
        pressureTxtView = findViewById(R.id.pressureTxtView)
        speedTxtView = findViewById(R.id.windSpeedTxtView)
        humidityTxtView = findViewById(R.id.humidityTxtView)
        cloudsTxtView = findViewById(R.id.cloudsTxtView)
        visabilityTxtView = findViewById(R.id.visibilityTxtView)
        UVTxtView = findViewById(R.id.uviTxtView)

        nextHoursRecyclerView = findViewById(R.id.nextHoursRecyclerView)
        nextDaysRecyclerView = findViewById(R.id.nextDaysRecyclerView)
    }
    companion object {
    }
    private fun getValuesFromSharedPreferences() {
        getSharedPreferences(this).apply {
            latitude = getString(getString(R.string.lat), " ").toString()
            longitude = getString(getString(R.string.lon), " ").toString()
            language = getString(getString(R.string.languageSettings), Locale.getDefault().toString()).toString()
            tempeUnit = getString(getString(R.string.tempSetting), "k").toString()
            tempeUnit = getString(getString(R.string.tempSetting), "k").toString()
        }
    }


    private fun setEnglishTemp(temp:Double) :String{

        when (tempeUnit) {
            "c" -> {
                newTemp= DecimalFormat("#").format(temp - 273.15)
                degreeTxtView.text =newTemp.plus("/°C")
            }
            "f" -> {
                newTemp= DecimalFormat("#").format(((temp - 273.15)*1.8)+32)
                degreeTxtView.text =newTemp.plus("°F")
            }
            else->{
                newTemp= DecimalFormat("#").format(temp)
                degreeTxtView.text =newTemp.plus("°K")
            }
        }
        return  newTemp
    }


    private fun setArabicTemp(temp:Double) :String{

        when (tempeUnit) {
            "c" -> {
                newTemp=(temp.toInt() - 273.15).toString()
                degreeTxtView.text = convertNumbersToArabic(newTemp.toDouble().roundToInt()).plus("°م")
            }
            "f" -> {
                newTemp= (((temp.toInt()- 273.15)*1.8)+32).toString()
                degreeTxtView.text =convertNumbersToArabic(newTemp.toDouble().roundToInt()).plus("°ف")
            }
            else->{
                degreeTxtView.text =convertNumbersToArabic(temp).plus("°ك")
            }
        }
        return  newTemp
    }
    private fun setEnglishSpeedUnit(windSpeed: Double): String {
        when (speedUnit) {
            "h" -> {
                newSpeed= DecimalFormat("#.##").format(windSpeed * 3.6)
                speedTxtView.text = newSpeed.plus("m/h")

            }
            else -> {
                newSpeed= DecimalFormat("#.##").format(windSpeed)
                speedTxtView.text = newSpeed.plus("m/s")

            }
        }
        return newSpeed
    }
    private fun setArabicSpeedUnit(windSpeed: Double): String {
        when (speedUnit) {
            "h" -> {
                newSpeed=(windSpeed.toInt() * 3.6).toString()
                speedTxtView.text =convertNumbersToArabic(newSpeed.toDouble()).plus("ميل/س")

            }
            else -> {
                speedTxtView.text = convertNumbersToArabic(windSpeed.toDouble()).plus("م/ث")

            }
        }
        return newSpeed
    }
}
