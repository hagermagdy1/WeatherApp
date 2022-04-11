package com.example.checkweather.home.view


import androidx.recyclerview.widget.RecyclerView
import com.example.checkweather.R
import com.example.checkweather.getSharedPreferences
import com.example.checkweather.model.Daily
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.checkweather.convertLongToTodayDate
import com.example.checkweather.convertNumbersToArabic
import java.text.DecimalFormat

class NextDayAdapter(private val context: Context) :
    RecyclerView.Adapter<NextDayAdapter.ViewHolder>() {

    var daily: List<Daily> = emptyList()
    lateinit var tempUnit:String

    private lateinit var language: String
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):NextDayAdapter.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.next_day_custom_row, parent, false)
        val holder=ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(
        holder:NextDayAdapter.ViewHolder,
        position: Int
    ) {
        getSharedPreferences(context).apply {
            language = getString(context.getString(R.string.languageSettings), "en")!!
        }
        Log.i("TAG", "onBindViewHolder: MovieAdapter ")
        val day = daily[position]

        tempUnit = getSharedPreferences(context).getString(
            context.getString(R.string.tempSetting),
            "k"
        )!!


        holder.txt2.text= convertLongToTodayDate(day.dt)
        holder.txt2.text=getNameOfDay(day.dt)


        holder.desc.text = day.weather[0].description
        if (language == "ar") {

            holder.txtTemp.text = convertNumbersToArabic(day.temp.day)
        } else {

            holder.txtTemp.text =getTemperature(day).plus("°C")
        }

    }


    private fun getTemperature(day: Daily): String {

        var temperature: String
        val minTemperature = DecimalFormat("#").format(day.temp.day - 273.15)
        val maxTemperature = DecimalFormat("#").format(day.temp.max - 273.15)
        temperature = "$minTemperature / $maxTemperature"
        return temperature
    }
    override fun getItemCount(): Int {
        return daily.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var desc:TextView
         var txtTemp:TextView
         var txt2:TextView
        init {
            desc = itemView.findViewById(R.id.nextDayDescriptionTxtView)
            txtTemp = itemView.findViewById(R.id.textTempDescriptionTxtView)
            txt2 = itemView.findViewById(R.id.text_card_day)
        }


}
    private fun getNameOfDay(milliSeconds: Long): String {
        return   SimpleDateFormat("EE", Locale.getDefault()).format(milliSeconds * 1000)
    }

}
