package com.example.checkweather.favorites.viewmodel

import com.example.checkweather.model.Favorite

interface OnFavoriteWeatherClickListener {
    fun onDeleteMovieFromFavorite(fav: Favorite?)
}
