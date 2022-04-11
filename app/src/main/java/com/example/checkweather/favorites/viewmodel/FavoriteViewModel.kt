import android.util.Log
import androidx.lifecycle.*
import com.example.checkweather.RepositoryInterface
import com.example.checkweather.model.Favorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavoriteViewModel (
    private val _irepo:RepositoryInterface
): ViewModel(){
    private val _weather = MutableLiveData<List<Favorite>>()
    val weather:LiveData<List<Favorite>> = _weather

    fun deleteFavorite(fav:Favorite){
        viewModelScope.launch (Dispatchers.IO){
            _irepo.deleteFavoriteWeather(fav)

        }
    }
    fun getFavorite(){
        Log.i("TAG", "getAllFavorite: view model ")
        viewModelScope.launch {
            var fav =  _irepo!!.storedFavoriteWeather()
            withContext(Dispatchers.IO){
                _weather.postValue(fav)
            }

        }
    }

}