package ih.pam.pamobile_jelahjahmalang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import ih.pam.pamobile_jelahjahmalang.models.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DetailMissionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _placeDetail = MutableStateFlow<Place?>( null)
    val placeDetail: StateFlow<Place?> = _placeDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getPlaceDetail(placeId: String){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val document = db.collection("places").document(placeId).get().await()

                if (document.exists()){
                    val place = document.toObject(Place::class.java)
                    if (place != null) {
                        // Copy ID dan masukkan ke state
                        _placeDetail.value = place.copy(id = document.id)
                    } else {
                        Log.e("DetailVM", "Gagal parsing dokumen ke object Place")
                    }
                }else{
                    Log.e("DetailVM", "Tempat tidak ditemukan dengan ID: $placeId")
                }
            } catch (e: Exception) {
                Log.e("DetailVM", "Gagal mengambil detail tempat", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}