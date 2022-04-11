package com.example.checkweather.network

import com.example.checkweather.RemoteSource
import com.example.checkweather.model.WeatherApi
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClient : RemoteSource {
    companion object {

        private var apiClient: APIClient? = null

        fun getInstance(): APIClient? {
            if (apiClient == null) apiClient = APIClient()
            return apiClient
        }

        var baseUrl = "https://api.openweathermap.org/data/2.5/"
        //var lat = "30.033333"
        //var lon = "31.233334"
    }

    private val retrofitService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)
    }

    override suspend fun getCurrentWeather(
        lat: String,
        long: String,
        language:String
    ): Response<WeatherApi>{
      val response =  retrofitService.getCurrentWeatherData(lat, long,language)
        return  response
    }

    override suspend fun getCurrentFavWeather(
        lat: String,
        long: String,
        language: String
    ): Response<WeatherApi> {
        val response =  retrofitService.getCurrentWeatherData(lat, long,language)
        return  response
    }

}
