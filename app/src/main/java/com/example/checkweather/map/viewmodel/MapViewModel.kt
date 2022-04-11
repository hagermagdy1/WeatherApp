import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkweather.RepositoryInterface
import com.example.checkweather.model.Favorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(private val _irepo: RepositoryInterface):ViewModel() {

        fun insertFav(fav: Favorite){
            viewModelScope.launch (Dispatchers.IO){
                _irepo.insertFavoriteWeather(fav)

            }
        }
    }

