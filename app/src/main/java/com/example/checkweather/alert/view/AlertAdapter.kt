package com.example.checkweather.alert.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.checkweather.R
import com.example.checkweather.model.Alert
import java.util.*

class AlertAdapter(context: Context) :
    RecyclerView.Adapter<AlertAdapter.ViewHolder>() {
    lateinit var bundle:Bundle
    private var context = context
    private var alerts: List<Alert> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.custom_row_alert, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertAdapter.ViewHolder, position: Int) {
        var alert = alerts[position]
        holder.startDate.text = alert.startDate
        holder.endDate.text = alert.endDate


    }
    override fun getItemCount(): Int {
        return alerts.size
        Log.i("TAG", "getItemCount:alart " + alerts.size)
    }
    fun setList(alert: List<Alert>) {
        this.alerts = alerts
        Log.i("TAG", "setList: hours" + alert.size)
        Log.i("TAG", "setList: this.hours " + this.alerts.size)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var startDate: TextView = itemView.findViewById(R.id.startDate)
        var endDate: TextView = itemView.findViewById(R.id.endDate)
    }

}