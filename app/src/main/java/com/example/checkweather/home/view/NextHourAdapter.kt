package com.example.checkweather.home.view

import androidx.recyclerview.widget.RecyclerView
import com.example.checkweather.R
import com.example.checkweather.getSharedPreferences
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.checkweather.convertNumbersToArabic
import com.example.checkweather.model.Hourly
import java.text.DecimalFormat
import kotlin.math.roundToInt

class NextHourAdapter(private val context: Context) :
    RecyclerView.Adapter<NextHourAdapter.ViewHolder>() {
    var hourly: List<Hourly> = emptyList()
    var tempUnit: String = ""
    lateinit var tempeUnit: String
     lateinit var language: String
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):NextHourAdapter.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.next_hours_custom_row, parent, false)
        val holder=ViewHolder(view)
        Log.i("TAG", "onCreateViewHolder: ")
        return holder
    }

    override fun onBindViewHolder(
        holder:NextHourAdapter.ViewHolder,
        position: Int
    ) {
        getSharedPreferences(context).apply {
            language = getString(context.getString(R.string.languageSettings), "en")!!
        }
        Log.i("TAG", "onBindViewHolder: MovieAdapter ")
        val hour = hourly[position ]

       holder.dateHour.text = getTimeInHour(hour.dt).toString()
        tempeUnit = getSharedPreferences(context).getString(
            context.getString(R.string.tempSetting),
            "k"
        )!!

        if (language == "ar") {
            var temp= (hour.temp.toInt())
            var newTemp=""
            when (tempeUnit) {
                "c" -> {
                    newTemp=(temp - 273.15).toString()
                    holder.tempTxtView.text =convertNumbersToArabic(newTemp.toDouble().roundToInt()).plus("°م")
                }
                "f" -> {
                    newTemp=(((temp - 273.15)*1.8)+32).toString()
                    holder.tempTxtView.text = convertNumbersToArabic(newTemp.toDouble().roundToInt()).plus("ف°")
                }
                else->{
                    holder.tempTxtView.text =convertNumbersToArabic(temp.toDouble()).plus("°ك")
                }
            }
        } else {
              var temp= (hour.temp.toInt())
            var newTemp=""
            when (tempeUnit) {
                "c" -> {
                    newTemp=DecimalFormat("#").format(temp - 273.15)
                    holder.tempTxtView.text =newTemp.plus("°C")
                }
                "f" -> {
                    newTemp=DecimalFormat("#").format(((temp - 273.15)*1.8)+32)
                    holder.tempTxtView.text =newTemp.plus("°F")
                }
                else->{
                    newTemp=DecimalFormat("#").format(temp)
                    holder.tempTxtView.text =newTemp.plus("°K")
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return hourly.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateHour: TextView

        lateinit var language: String
        var tempTxtView: TextView

        init {
            dateHour = itemView.findViewById(R.id.hoursTxtView)
            tempTxtView = itemView.findViewById(R.id.tempNextHourTxtView)
        }
        }
    private fun getTimeInHour(milliSeconds: Long): String {
        val time = milliSeconds * 1000.toLong()
        val format = SimpleDateFormat("h a",Locale(language))
        Log.i("TAG", "getTimeInHour: " + Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("Africa/Cairo")
        return format.format(Date(time))
    }
    }