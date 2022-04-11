package com.example.checkweather.favorites.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.checkweather.R
import com.example.checkweather.favorites.viewmodel.OnFavoriteWeatherClickListener
import com.example.checkweather.model.Favorite
import java.util.*

class FavoriteAdapter(context: Context,var clickListner: OnFavoriteWeatherClickListener) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    private val TAG = "FavoritePlacesAdapter"
    private var context = context
    private var favorites: List<Favorite> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.custom_row_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteAdapter.ViewHolder, position: Int) {
        var favorite = favorites[position]
        holder.nameOfFavoritePlace.text = favorite.location
        holder.btnDeleteFav.setOnClickListener {
            clickListner.onDeleteMovieFromFavorite(favorite)
        }
        holder.constraintFavCustomRow.setOnClickListener {
            val intent = Intent(context, DetailsFavorite::class.java)
            intent.putExtra("latFav",favorite.lat)
            intent.putExtra("lonFav",favorite.lon)
            context.startActivity(intent)
            }
        }

    override fun getItemCount(): Int {
        return favorites.size
    }
    fun setList(favorites: List<Favorite>) {
        this.favorites = favorites
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameOfFavoritePlace: TextView = itemView.findViewById(R.id.favTxTViewCustomRow)
        var btnDeleteFav: ImageView = itemView.findViewById(R.id.deleteFavoriteBtn)
        var constraintFavCustomRow:ConstraintLayout=itemView.findViewById(R.id.constraintFavCustomRow)
    }

}