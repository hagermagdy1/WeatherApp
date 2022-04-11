package com.example.checkweather.favorites.view

import FavoriteViewModel
import FavoritesViewModelFactory
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkweather.R
import com.example.checkweather.database.LocalSourceConcreteClass
import com.example.checkweather.favorites.viewmodel.OnFavoriteWeatherClickListener
import com.example.checkweather.getSharedPreferences
import com.example.checkweather.map.view.MapActivity
import com.example.checkweather.model.Favorite
import com.example.checkweather.model.Repository
import com.example.checkweather.network.APIClient
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FavoriteFragment : Fragment() , OnFavoriteWeatherClickListener {

    lateinit var favFactory: FavoritesViewModelFactory
    lateinit var viewModel: FavoriteViewModel
    lateinit var floatingActionButton: FloatingActionButton
    lateinit var favAdapter: FavoriteAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerview: RecyclerView
    lateinit var constraintFav:ConstraintLayout
    lateinit var emptyFavList:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
     var view :View = inflater.inflate(R.layout.fragment_favorite, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            favFactory = FavoritesViewModelFactory(
                Repository.getInstance(
                    APIClient.getInstance()!!,
                    LocalSourceConcreteClass(requireContext()), requireContext()
                )
            )
           initiateUI(view)
        setUpRecyclerView()
            viewModel = ViewModelProvider(this, favFactory).get(FavoriteViewModel::class.java)

        viewModel.getFavorite()

        viewModel.weather.observe(requireActivity()){weathers ->
            if (weathers!=null) {
                favAdapter.setList(weathers)
                favAdapter.notifyDataSetChanged()
            }
            else{
                emptyFavList.visibility = View.VISIBLE

            }

        }

            floatingActionButton.setOnClickListener {
                setIsFavToSharedPreferences()
                val intent = Intent(requireContext(), MapActivity::class.java)
                 startActivity(intent)

            }


        }


    override fun onResume() {
        super.onResume()
        viewModel.getFavorite()

    }
    private fun setIsFavToSharedPreferences() {
        val editor = getSharedPreferences(requireContext()).edit()
        editor.putString(requireContext().getString(R.string.isFav), "true")
        editor.apply()
        }

    private fun initiateUI(view: View) {
      floatingActionButton= view.findViewById(R.id.floatingActionButton)
        recyclerview=view.findViewById(R.id.fragmentFavoriteRecyclerView)
        constraintFav=view.findViewById(R.id.constraintFav)
        emptyFavList=view.findViewById(R.id.emptyFavList)
        emptyFavList.visibility = View.INVISIBLE
    }

    private fun setUpRecyclerView(){
        layoutManager= LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        favAdapter= FavoriteAdapter(requireContext(),this)
        recyclerview.layoutManager= layoutManager
        recyclerview.setAdapter(favAdapter)
    }
    override fun onDeleteMovieFromFavorite(fav: Favorite?) {
        viewModel.deleteFavorite(fav!!)
        viewModel.getFavorite()

    }



}