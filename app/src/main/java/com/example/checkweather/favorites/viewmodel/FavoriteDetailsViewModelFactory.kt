package com.example.checkweather.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.checkweather.RepositoryInterface
import com.example.checkweather.favorites.view.FavoritesDetailsViewModel

import java.lang.IllegalArgumentException

class FavoriteDetailsViewModelFactory(private val _irepo: RepositoryInterface) :
    ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoritesDetailsViewModel::class.java)){
            FavoritesDetailsViewModel(_irepo) as T
        }
        else{
            throw IllegalArgumentException("View Model Class Not Found")
        }
    }
}