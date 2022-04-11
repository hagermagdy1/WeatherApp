package com.example.checkweather

import com.example.checkweather.model.WeatherApi
import retrofit2.Response

  interface RemoteSource {
       suspend fun getCurrentWeather(
          lat: String,
          long: String,
          language:String
      ):Response<WeatherApi>
      suspend fun getCurrentFavWeather(
          lat: String,
          long: String,
          language:String
      ):Response<WeatherApi>

  }
