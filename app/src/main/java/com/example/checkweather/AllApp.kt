package com.example.checkweather

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.getString(R.string.sharedPref),
            Context.MODE_PRIVATE
        )
    }
fun getCityText(context: Context, lat: Double, lon: Double, language: String): String {
    var city = "alex"
    val geocoder = Geocoder(context, Locale(language))
    try {
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (addresses.isNotEmpty()) {
            city = "${addresses[0].adminArea}, ${addresses[0].countryName}"
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return city
}
fun converCalendarToDayOfWeek(calendar: Calendar, language: String): String {
    return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale(language))
}

fun convertLongToTodayDate(time: Long, language: String): String {
    val date = Date(time)
    val format = SimpleDateFormat("d MMM, yyyy", Locale(language))
    return format.format(date)
}
fun convertLongToTodayDate(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("d MMM, yyyy")
    return format.format(date)
}

fun convertNumbersToArabic(value: Double): String {
    return (value.toString() + "")
        .replace("1".toRegex(), "١").replace("2".toRegex(), "٢")
        .replace("3".toRegex(), "٣").replace("4".toRegex(), "٤")
        .replace("5".toRegex(), "٥").replace("6".toRegex(), "٦")
        .replace("7".toRegex(), "٧").replace("8".toRegex(), "٨")
        .replace("9".toRegex(), "٩").replace("0".toRegex(), "٠")
}
fun convertNumbersToArabic(value: Int): String {
    return (value.toString() + "")
        .replace("1".toRegex(), "١").replace("2".toRegex(), "٢")
        .replace("3".toRegex(), "٣").replace("4".toRegex(), "٤")
        .replace("5".toRegex(), "٥").replace("6".toRegex(), "٦")
        .replace("7".toRegex(), "٧").replace("8".toRegex(), "٨")
        .replace("9".toRegex(), "٩").replace("0".toRegex(), "٠")
}