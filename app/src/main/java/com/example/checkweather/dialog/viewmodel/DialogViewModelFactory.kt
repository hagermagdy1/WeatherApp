package com.example.checkweather.dialog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.checkweather.RepositoryInterface
import com.example.checkweather.home.viewmodel.HomeViewModel
import java.lang.IllegalArgumentException

class DialogViewModelFactory(private val _irepo: RepositoryInterface) :
    ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            DialogViewModel(_irepo) as T
        }
        else{
            throw IllegalArgumentException("V iew Model Class Not Found")
        }
    }
}
