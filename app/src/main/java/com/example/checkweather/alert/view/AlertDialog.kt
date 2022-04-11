package com.example.checkweather.alert.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.checkweather.R
import com.example.checkweather.WorkManagerr
import com.example.checkweather.alert.viewmodel.AlertViewModel
import com.example.checkweather.alert.viewmodel.AlertViewModelFactory
import com.example.checkweather.database.LocalSourceConcreteClass
import com.example.checkweather.model.Alert
import com.example.checkweather.model.Repository
import com.example.checkweather.network.APIClient
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlertDialog : AppCompatActivity() {
    private val TAG = "AlertFragment"
    lateinit var btnFrom: Button
    lateinit var btnTo: Button
    lateinit var timeText: TextView
    lateinit var btnSave: Button
    lateinit var startDate: String
    lateinit var endDate: String
    lateinit var alert: Alert
    lateinit var time: String
    lateinit var viewModel: AlertViewModel
    lateinit var alertFactory: AlertViewModelFactory
    lateinit var date: String;
    lateinit var description: String
    private var alarmStartYear: Int = 0
    private var alarmStartMonth: Int = 0
    private var alarmStartDay: Int = 0
    private var alarmStartHour: Int = 0
    private var alarmStartMinute: Int = 0

    // lateinit var datePicker: DatePicker
    //  lateinit var timePicker: TimePicker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_dialog)
        btnFrom = findViewById(R.id.fromBtn)
        btnTo = findViewById(R.id.toBtn)
        timeText = findViewById(R.id.alarmTxtView)
        btnSave = findViewById(R.id.btnSave)

        alertFactory = AlertViewModelFactory(
            Repository.getInstance(
                APIClient.getInstance()!!,
                LocalSourceConcreteClass(this), this
            )
        )
        viewModel = ViewModelProvider(this, alertFactory).get(AlertViewModel::class.java)
        btnFrom.setOnClickListener {
            startDate = showDatePickerFrom().toString()

        }
        btnTo.setOnClickListener {
            endDate = showDatePickerTo().toString()
        }
        btnSave.setOnClickListener {
            time = timeText.text.toString()
            var alert = Alert( time, date, date)
            viewModel.insertAlert(alert)
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
                WorkManagerr::class.java,
            )
                .setInitialDelay(convertDate(), TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(this).enqueue(
                oneTimeWorkRequest
            )
            finish()
        }
        timeText.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showDatePickerFrom() {
        val myCalender = Calendar.getInstance()
        val year = myCalender[Calendar.YEAR]
        val month = myCalender[Calendar.MONTH]
        val day = myCalender[Calendar.DAY_OF_MONTH]

        val myDateListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                if (view.isShown) {
                    date = "$day/${month + 1}/$year"
                    Log.i("TAG", "showDatePicker: " + date)
                    alarmStartYear=year
                    alarmStartMonth=month+1
                    alarmStartDay=day
                    Log.i(TAG, "showDatePickerFrom: year" + alarmStartYear)
                    Log.i(TAG, "showDatePickerFrom: month" + alarmStartMonth)
                    Log.i(TAG, "showDatePickerFrom: day" + alarmStartDay)
                }
            }
        val datePickerDialog = DatePickerDialog(
            this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            myDateListener, year, month, day
        )
        datePickerDialog.setTitle(getString(R.string.datePicker))
        datePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        datePickerDialog.show()
        //return date
    }

    fun showTimePicker(): String {
        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)
        TimePickerDialog(this, { timePicker: TimePicker, hour: Int, minute: Int ->
            alarmStartHour=hour
            alarmStartMinute=minute
            if (hour >= 12)
                timeText.text = DecimalFormat("#").format(hour) + ":" + DecimalFormat("#").format(minute) + "AM"
            else
                timeText.text = DecimalFormat("#").format(hour) + ":" + DecimalFormat("#").format(minute) + "PM"
        }, hour, minute, false).show()
        return timeText.text.toString()
    }
    private fun convertDate(): Long {
        val dates = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        var currentTime = Calendar.getInstance().timeInMillis
        var startTime =
            dates.parse("$alarmStartYear/$alarmStartMonth/$alarmStartDay $alarmStartHour:$alarmStartMinute")
                .time
        val initialDelay: Long = startTime - currentTime
        return initialDelay

    }


    private fun showDatePickerTo() {
        val myCalender = Calendar.getInstance()
        val year = myCalender[Calendar.YEAR]
        val month = myCalender[Calendar.MONTH]
        val day = myCalender[Calendar.DAY_OF_MONTH]

        val myDateListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                if (view.isShown) {
                    date = "$day/${month + 1}/$year"
                }
            }
        val datePickerDialog = DatePickerDialog(
            this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            myDateListener, year, month, day
        )
        datePickerDialog.setTitle(getString(R.string.datePicker))
        datePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        datePickerDialog.show()
    }
}