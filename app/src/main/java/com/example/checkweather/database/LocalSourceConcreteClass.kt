package com.example.checkweather.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.checkweather.model.Alert
import com.example.checkweather.model.Favorite
import com.example.checkweather.model.WeatherApi


class LocalSourceConcreteClass(context: Context) :
    LocalSource {
     var weatherDao: WeatherDao?=null
      //var fav : LiveData<List<Favorite>>
     //var weather : LiveData<List<OpenWeatherApi>>
      var storedWeather: LiveData<WeatherApi>


    companion object {
        var sourceConcreteClass: LocalSourceConcreteClass? = null
        fun getInstance(context: Context): LocalSourceConcreteClass? {
            if (sourceConcreteClass == null) sourceConcreteClass = LocalSourceConcreteClass(context)
            return sourceConcreteClass
        }
    }
    init {
        val appDataBase = AppDataBase.getInstance(context.applicationContext)
        weatherDao = appDataBase!!.weatherDao()
        storedWeather =weatherDao!!.allweatherRoom

        //  fav = weatherDao!!.allWeathers()
       // weather=weatherDao!!.allweatherRoom()
    }

    override val allStoredWeathers: LiveData<WeatherApi>
        get() = storedWeather

    override suspend fun insertFavoriteLocalSource(fav: Favorite?) {
        weatherDao?.insertWeather(fav)
    }

    override suspend fun insertCurrentLocalSource(weather: WeatherApi) {
        weatherDao?.insertCurrentToLocal(weather)
        Log.i("TAG", "insertCurrentToLocal: " )
    }

    override suspend fun insertAlertLocalSource(alert: Alert ) {
        weatherDao?.insertAlert(alert)
    }

    override fun deleteAlertLocalSource(alert: Alert) {
        weatherDao?.deleteAlert(alert)
    }

    override fun deleteFavoriteLocalSource(fav: Favorite) {
        weatherDao?.deleteWeather(fav)

    }

    override suspend fun allStoredFavoriteWeather(): List<Favorite> {
        return weatherDao?.allWeathers()!!
    }



    override suspend fun allStoredAlertsWeather(): List<Alert> {
        return weatherDao?.allAlerts()!!
    }

}