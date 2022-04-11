package com.example.checkweather.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.checkweather.RemoteSource
import com.example.checkweather.RepositoryInterface
import com.example.checkweather.database.LocalSource
import com.example.checkweather.network.APIClient
import retrofit2.Response

class Repository private constructor(
    var   remoteSource: RemoteSource,
    var localSource: LocalSource,
    var context: Context
): RepositoryInterface {

    override suspend fun getAllFavorite(
        lat: String,
        long: String,
        language: String
    ): Response<WeatherApi> {
        return remoteSource.getCurrentFavWeather(lat, long, language = language)
    }


    override suspend fun getAllMovies(
        lat: String,
        long: String,
        language: String
    ): Response<WeatherApi> {
        return remoteSource.getCurrentWeather(lat, long, language = language)
    }

    override suspend fun storedFavoriteWeather(): List<Favorite> {
        return localSource.allStoredFavoriteWeather()
    }

    override suspend fun storedAlerts(): List<Alert> {
        return localSource.allStoredAlertsWeather()
    }

    override fun storedWeather(): LiveData<WeatherApi> {
        return localSource.allStoredWeathers
    }

    override suspend fun insertFavoriteWeather(fav: Favorite?) {
        Log.i("TAG", "insertFavvv: Repository")
        localSource.insertFavoriteLocalSource(fav)
    }


    override fun deleteFavoriteWeather(fav: Favorite?) {
        localSource.deleteFavoriteLocalSource(fav!!)
    }

    override suspend fun insertCurrentWeather(weather: WeatherApi?) {
     localSource.insertCurrentLocalSource(weather!!)
        Log.i("TAG", "repoo: "+ weather )

    }

    override suspend fun insertCurrentAlert(alert: Alert) {
        localSource.insertAlertLocalSource(alert)
    }

    companion object {
        private var instance: Repository? = null
        fun getInstance(
            remoteSource: APIClient, localSource:LocalSource,context: Context
        ): Repository {

            return instance ?: Repository(remoteSource, localSource, context)
        }



    }

    init {
        this.remoteSource = remoteSource
        this.localSource = localSource
    }

}