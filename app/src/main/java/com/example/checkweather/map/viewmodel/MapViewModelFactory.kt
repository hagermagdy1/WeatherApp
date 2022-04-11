import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.checkweather.RepositoryInterface

class MapViewModelFactory(private val i_repo:RepositoryInterface):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(i_repo) as T
    }
}