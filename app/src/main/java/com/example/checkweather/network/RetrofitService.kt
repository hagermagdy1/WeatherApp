package com.example.checkweather.network

import com.example.checkweather.model.WeatherApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

var appId = "cce64fba5705becc7fbe52b46e9df003"


 interface RetrofitService {

     @GET("onecall")
    suspend fun getCurrentWeatherData(
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("lang") lang: String ,
        @Query("appid") app_id: String = appId
    ): Response<WeatherApi>

 }

