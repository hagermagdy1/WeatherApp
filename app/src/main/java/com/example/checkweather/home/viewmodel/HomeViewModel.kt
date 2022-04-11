package com.example.checkweather.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.example.checkweather.RepositoryInterface
import com.example.checkweather.model.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel (irepo: RepositoryInterface): ViewModel() {
    private val _irepo: RepositoryInterface = irepo

    private var _weather = MutableLiveData<WeatherApi>()
    val weather: LiveData<WeatherApi> = _weather

    fun insertCurrentToLocal(weather: WeatherApi) {
        viewModelScope.launch(Dispatchers.IO) {
            _irepo.insertCurrentWeather(weather)
            Log.i("TAG", "insertCurrentToLocal: ")

        }
    }

    fun getWeatherFromOnline(lat: String, lon: String, language: String) {
        viewModelScope.launch {
            val weathers = _irepo.getAllMovies(lat = lat, long = lon, language)
            val weather2 = weathers.body()
            withContext(Dispatchers.Main) {
                _weather.postValue(weather2!!)
                Log.i("TAG", "getAllMovies: " + _weather.postValue(weather2!!))
            }
        }
    }
    fun getWeatherNew(): LiveData<WeatherApi> {
        return _irepo.storedWeather()
    }
    }