package pg.mobile.projectpampricil.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ih.pam.pamobile_jelahjahmalang.utils.PlaceRepository
import ih.pam.pamobile_jelahjahmalang.viewmodel.PlaceViewModel

class PlaceViewModelFactory(
    private val repository: PlaceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
