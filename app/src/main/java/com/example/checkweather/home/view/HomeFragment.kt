package com.example.checkweather.home.view

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.example.checkweather.*
import com.example.checkweather.R
import com.example.checkweather.database.LocalSourceConcreteClass
import com.example.checkweather.databinding.FragmentHomeBinding
import com.example.checkweather.home.HomeViewModelFactory
import com.example.checkweather.home.viewmodel.HomeViewModel
import com.example.checkweather.model.*
import com.example.checkweather.network.APIClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class HomeFragment : Fragment() {
    lateinit var nextDayAdapter: NextDayAdapter
    lateinit var nextHourAdapter: NextHourAdapter
    lateinit var homeFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel
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

    private var binding: FragmentHomeBinding? = null
    private var myLocationList = ArrayList<Double>()
    private lateinit var latitude: String
    private lateinit var longitude: String
    lateinit var tempeUnit: String
    lateinit var speedUnit: String

    //   lateinit var progressBar:ProgressBar
    private var language: String = "en"

    //var temp:Double = 0.0
    var newTemp = ""
    var newSpeed = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("TAG", "onCreateView:  FRAGMEEEEEEEEEEEEEEENT HOME")
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSharedPreferences(requireActivity()).apply {
            language = getString(getString(R.string.languageSettings), "en")!!
        }
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        homeFactory = HomeViewModelFactory(
            Repository.getInstance(
                APIClient.getInstance()!!,
                LocalSourceConcreteClass(requireContext()),
                requireContext()
            )
        )
        getValuesFromSharedPreferences()

        initUI(view)
        setUpNextDayRecyclerView()
        setUpNextHourRecyclerView()
        viewModel = ViewModelProvider(this, homeFactory).get(HomeViewModel::class.java)
        if (isInternetAvailable()) {
            viewModel.getWeatherFromOnline(
                latitude,
                longitude, language
            )
            viewModel.weather.observe(requireActivity()) { it ->
                initUI(view)
                setUpNextDayRecyclerView()
                setUpNextHourRecyclerView()
                if (it != null) {
                    if (it != null) {
                        viewModel.insertCurrentToLocal(it)
                        tempeUnit = getSharedPreferences(requireContext()).getString(
                            getString(R.string.tempSetting),
                            "k"
                        )!!
                        speedUnit = getSharedPreferences(requireContext()).getString(
                            getString(R.string.speedSetting),
                            "s"
                        )!!
                        //setEnglishTemp(tempeUnit)
                        Log.i("APICLInET", "getCurrentWeather: " + it)
                        if (language == "en") {
                            putEnglishUnits(it)
                        } else {
                            convertArabicUnits(it)
                        }
                        setData(it)
                        getNextDayAdapterData(it.daily as java.util.ArrayList<Daily>, tempeUnit)
                        getNextHourAdapterData(it.hourly as java.util.ArrayList<Hourly>, tempeUnit)
                    } else {

                    }
                }
            }

        } else {
            Log.i("TAG", "onViewCreated: error in network")
            viewModel.getWeatherNew().observe(requireActivity())
            { it ->
                if (it != null) {
                    Log.i("TAG", "onViewCreated it: " + it)
                    tempeUnit = getSharedPreferences(requireContext()).getString(
                        getString(R.string.tempSetting),
                        "k"
                    )!!
                    speedUnit = getSharedPreferences(requireContext()).getString(
                        getString(R.string.speedSetting),
                        "s"
                    )!!
                    Log.i("APICLInET", "getCurrentWeather: " + it)
                    if (language == "en") {
                        putEnglishUnits(it)
                    } else {
                        convertArabicUnits(it)
                    }
                    setData(it)
                    getNextDayAdapterData(it.daily as java.util.ArrayList<Daily>, tempeUnit)
                    getNextHourAdapterData(it.hourly as java.util.ArrayList<Hourly>, tempeUnit)
                }


            }
        }



    }
        private fun isInternetAvailable(): Boolean {
        var isAvailable = false
        val manager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.isConnected || manager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE
            )!!
                .isConnected
        ) isAvailable = true
        return isAvailable
    }
    private fun setData(it: WeatherApi) {
        dayTxtView.text = converCalendarToDayOfWeek(Calendar.getInstance(), language)
        dateTxtView.text = convertLongToTodayDate(Calendar.getInstance().timeInMillis, language)
        cityTxtView.text = getCityText(requireContext(), it.lat, it.lon, language)
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
        //    this.tempUnit = tempeUnit
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
        nextDayAdapter = NextDayAdapter(requireContext())
        nextDaysRecyclerView.layoutManager = layoutManager
        nextDaysRecyclerView.setAdapter(nextDayAdapter)
    }
    private fun setUpNextHourRecyclerView() {
        val layoutManager = LinearLayoutManager(HomeFragment().context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        nextHourAdapter = NextHourAdapter(requireContext())
        nextHoursRecyclerView.layoutManager = layoutManager
        nextHoursRecyclerView.setAdapter(nextHourAdapter)
    }
    private fun initUI(view: View) {
      //  progressBar=view.findViewById(R.id.progressBar)
        //progressBar.visibility = View.INVISIBLE
        dayTxtView = view.findViewById(R.id.todayTxtView)
        cityTxtView = view.findViewById(R.id.cityTxtView)
        dateTxtView = view.findViewById(R.id.fullDateTxtView)
        degreeTxtView = view.findViewById(R.id.tempTxtView)
        cityTxtView = view.findViewById(R.id.cityTxtView)
        descriptionTxtView = view.findViewById(R.id.descriptionTxtView)

        //
        pressureTxtView = view.findViewById(R.id.pressureTxtView)
        speedTxtView = view.findViewById(R.id.windSpeedTxtView)
        humidityTxtView = view.findViewById(R.id.humidityTxtView)
        cloudsTxtView = view.findViewById(R.id.cloudsTxtView)
        visabilityTxtView = view.findViewById(R.id.visibilityTxtView)
        UVTxtView = view.findViewById(R.id.uviTxtView)

        nextHoursRecyclerView = view.findViewById(R.id.nextHoursRecyclerView)
        nextDaysRecyclerView = view.findViewById(R.id.nextDaysRecyclerView)
    }
    companion object {
    }
    private fun getValuesFromSharedPreferences() {
        getSharedPreferences(requireContext()).apply {
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
                newTemp=DecimalFormat("#").format(temp - 273.15)
                degreeTxtView.text =newTemp.plus("/°C")
            }
            "f" -> {
                newTemp=DecimalFormat("#").format(((temp - 273.15)*1.8)+32)
                degreeTxtView.text =newTemp.plus("°F")
            }
            else->{
                newTemp=DecimalFormat("#").format(temp)
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
                newSpeed=DecimalFormat("#.##").format(windSpeed * 3.6)
                speedTxtView.text = newSpeed.plus("m/h")

            }
            else -> {
                newSpeed=DecimalFormat("#.##").format(windSpeed)
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