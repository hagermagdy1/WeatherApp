package com.example.checkweather.favorites.view

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

class FavoritesDetailsViewModel (irepo: RepositoryInterface): ViewModel() {
    private val _irepo: RepositoryInterface = irepo
    private var _weather = MutableLiveData<WeatherApi>()
    val weather: LiveData<WeatherApi> = _weather

    fun getWeatherFromOnline(lat: String, lon: String, language: String) {

        viewModelScope.launch {
            val weathers = _irepo.getAllFavorite(lat = lat, long = lon, language)
            val weather2 = weathers.body()
            withContext(Dispatchers.Main) {
                _weather.postValue(weather2!!)
                Log.i("TAG", "getAllMovies: " + _weather.postValue(weather2!!))
            }
        }
    }
}