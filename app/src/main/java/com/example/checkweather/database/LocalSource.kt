package com.example.checkweather.database

import androidx.lifecycle.LiveData
import com.example.checkweather.model.Alert
import com.example.checkweather.model.Favorite
import com.example.checkweather.model.WeatherApi

interface LocalSource {
    suspend fun insertFavoriteLocalSource(fav: Favorite?)
    suspend fun insertCurrentLocalSource(weather: WeatherApi)

    suspend fun insertAlertLocalSource(alert: Alert)

    fun deleteAlertLocalSource(alert: Alert)

    fun deleteFavoriteLocalSource(fav: Favorite)
    suspend fun allStoredFavoriteWeather():List<Favorite>
    val allStoredWeathers: LiveData<WeatherApi>

    suspend fun allStoredAlertsWeather():List<Alert>


}