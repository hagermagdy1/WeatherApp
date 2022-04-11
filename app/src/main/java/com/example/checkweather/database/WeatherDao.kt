package com.example.checkweather.database
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.checkweather.model.Alert
import com.example.checkweather.model.Favorite
import com.example.checkweather.model.WeatherApi

@Dao
interface WeatherDao {
    @Query("Select * from favorite")
    suspend fun allWeathers(): List<Favorite>

    @Query("Select * from alert")
    suspend fun allAlerts(): List<Alert>

    @get:Query("Select * from weather")
    val allweatherRoom: LiveData<WeatherApi>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: Favorite?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alerts: Alert?)

    @Delete
    fun deleteAlert(alert: Alert?)
    @Delete
    fun deleteWeather(weather: Favorite?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertCurrentToLocal(weather: WeatherApi?)


}