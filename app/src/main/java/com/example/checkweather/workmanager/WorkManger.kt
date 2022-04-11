package com.example.checkweather

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.checkweather.alert.view.AlertFragment
import com.example.checkweather.network.APIClient


class WorkManagerr(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    lateinit var lat:String
    lateinit var lon:String
    lateinit var language:String



    override suspend fun doWork(): Result {
         displayNotification("Sunnnnyyyyyy")
        getSharedPreferences(context).apply {
            lat = getString(context.getString(R.string.lat), "en")!!
        }
        getSharedPreferences(context).apply {
            lon = getString(context.getString(R.string.lon), "en")!!
        }
        getSharedPreferences(context).apply {
            language = getString(context.getString(R.string.languageSettings), "en")!!
        }
        val weatherApi= APIClient.getInstance()!!.getCurrentWeather(lat,lon,language)

        if (weatherApi != null) {
            if(weatherApi.isSuccessful){

                Log.i("TAG", "WorkManager"+weatherApi.body()?.toString()+"WorkManager")
                val description= weatherApi.body()!!.alerts!![0].tags
                if(description !=null ){
                    //displayNotification(description.toString())
                }else {
                    //displayNotification("No Weather Alert Today Enjoy")
                }
            }
            else{
                Log.i("TAG", weatherApi.raw().toString()+"Fail")
            }
        }
        displayNotification("Sunny")
        return  Result.success()
    }

        private fun displayNotification(keyword: String) {
        val notificationIntent = Intent(context, AlertFragment::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val contentIntent = PendingIntent.getActivity(
            context,
            1,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "h",
                "h",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            assert(notificationManager != null)
            notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(
            context, "h"
        )
            .setContentTitle("title$keyword")
            .setContentText("Weather Alerts!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_launcher_background)
        assert(notificationManager != null)
        notificationManager.notify(1, builder.build())
    }

}