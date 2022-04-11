
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.checkweather.model.Repository
import java.lang.IllegalArgumentException

class FavoritesViewModelFactory(private val _irepo: Repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            FavoriteViewModel(_irepo!!)as T
        }
        else{
            throw IllegalArgumentException("View Model Class Not Found")
        }
    }
}