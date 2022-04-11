package com.example.checkweather.dialog.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.checkweather.R

class NoInternetDialog : AppCompatActivity() {
    lateinit var btnOk:Button
    lateinit var txtView:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet_dialog)
        btnOk=findViewById(R.id.btnNo)
        txtView=findViewById(R.id.noNetTxtView)

        btnOk.setOnClickListener{
            finish()
        }
    }
}