package com.example.checkweather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.checkweather.model.*

@TypeConverters(DataConverter::class)
@Database(
    entities = [WeatherApi::class,Favorite::class,Alert::class]
,version =22
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao?

    companion object {
        var appDataBase: AppDataBase? = null
        @Synchronized
      open  fun getInstance(context: Context): AppDataBase? {
          return appDataBase?:Room.databaseBuilder(
              context.applicationContext,
              AppDataBase::class.java,"weatherr"
          ).fallbackToDestructiveMigration().build()
        }
    }
}