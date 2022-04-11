package com.example.checkweather.alert.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkweather.RepositoryInterface
import com.example.checkweather.model.Alert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertViewModel (irepo: RepositoryInterface): ViewModel() {
    private val _irepo: RepositoryInterface = irepo

    private val _weather = MutableLiveData<List<Alert>>()
    val weather:LiveData<List<Alert>> = _weather
    fun insertAlert(alerts: Alert) {
        viewModelScope.launch(Dispatchers.IO) {
            _irepo.insertCurrentAlert(alerts)

        }
    }
    fun getAlerts(){
        viewModelScope.launch {
            var alert = _irepo!!.storedAlerts()
            withContext(Dispatchers.Main){
                _weather.postValue(alert)

            }

        }
    }
}
