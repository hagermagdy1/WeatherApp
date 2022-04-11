package com.example.checkweather.alert.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.example.checkweather.R
import com.example.checkweather.WorkManagerr
import com.example.checkweather.alert.viewmodel.AlertViewModel
import com.example.checkweather.alert.viewmodel.AlertViewModelFactory
import com.example.checkweather.database.LocalSourceConcreteClass
import com.example.checkweather.model.Repository
import com.example.checkweather.network.APIClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlertFragment : Fragment() {
   lateinit var alertFactory: AlertViewModelFactory
    lateinit var viewModel: AlertViewModel
    lateinit var alertApapter: AlertAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerview: RecyclerView
    lateinit var addAlertBtn:FloatingActionButton
    lateinit var time:String;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_alert, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alertFactory = AlertViewModelFactory(
            Repository.getInstance(
                APIClient.getInstance()!!,
                LocalSourceConcreteClass(requireContext()), requireContext()
            )
        )
        initiateUI(view)
        setUpRecyclerView()
        viewModel = ViewModelProvider(this, alertFactory).get(AlertViewModel::class.java)

        viewModel.getAlerts()



        viewModel.weather.observe(requireActivity()) { weathers ->
            if (weathers != null) {
                alertApapter.setList(weathers)
                alertApapter.notifyDataSetChanged()

            }
        }
            addAlertBtn.setOnClickListener{
                val intent = Intent(requireContext(), AlertDialog::class.java)
                startActivity(intent)
            }



    }
        override fun onResume() {
            super.onResume()
            viewModel.getAlerts()

        }


        private fun initiateUI(view: View) {
            addAlertBtn= view.findViewById(R.id.addAlertButon)
            recyclerview=view.findViewById(R.id.fragmentAlertRecyclerView)
            addAlertBtn=view.findViewById(R.id.addAlertButon)

        }

        private fun setUpRecyclerView(){
            layoutManager= LinearLayoutManager(requireContext())
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            alertApapter= AlertAdapter(requireContext())
            recyclerview.layoutManager= layoutManager
            recyclerview.setAdapter(alertApapter)
        }
    companion object {

            }

}