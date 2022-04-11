package com.example.checkweather

import androidx.lifecycle.LiveData
import com.example.checkweather.model.Alert
import com.example.checkweather.model.Favorite
import com.example.checkweather.model.WeatherApi
import retrofit2.Response

interface RepositoryInterface {

    suspend fun getAllMovies(lat:String,long:String,language:String): Response<WeatherApi>
    suspend fun getAllFavorite(lat:String,long:String,language:String): Response<WeatherApi>

suspend fun storedFavoriteWeather(): List<Favorite>
    suspend fun storedAlerts(): List<Alert>

     fun storedWeather(): LiveData<WeatherApi>

    suspend fun insertFavoriteWeather(fav: Favorite?)
    fun deleteFavoriteWeather(fav: Favorite?)
    suspend fun insertCurrentWeather(weather: WeatherApi?)
    suspend fun insertCurrentAlert(alert: Alert)
}