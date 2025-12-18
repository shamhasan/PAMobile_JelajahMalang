package ih.pam.pamobile_jelahjahmalang.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ih.pam.pamobile_jelahjahmalang.model.MissionDocuments
import ih.pam.pamobile_jelahjahmalang.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MissionViewmodel: ViewModel() {

    private val _missions = MutableStateFlow<List<MissionDocuments>>(emptyList())
    val missions: StateFlow<List<MissionDocuments>> = _missions

    private val _mission = MutableStateFlow<MissionDocuments?>(null)
    val mission: StateFlow<MissionDocuments?> = _mission

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isDistanceValid = MutableStateFlow<Boolean?>(null)
    val isDistanceValid: StateFlow<Boolean?> = _isDistanceValid

    init {
        fetchMissions()
    }

    fun fetchMissions(){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instance.getMission()

                _missions.value = response.documents?:emptyList()
            }catch (e: Exception){
                e.printStackTrace()
                _missions.value = emptyList()
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun validateMissionDistance(
        userLat: Double,
        userLng: Double,
        missionLat: Double,
        missionLng: Double
    ): Boolean {
        val results = FloatArray(1)

        // Hitung jarak
        Location.distanceBetween(
            userLat,
            userLng,
            missionLat,
            missionLng,
            results
        )

        val distanceInMeters = results[0]
        val isValid = distanceInMeters <= 1000

        _isDistanceValid.value = isValid // Update state jika perlu dipantau UI

        return isValid
    }

    fun fetchMissionsById(missionId: String){
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getMissionById(id = missionId)
                _mission.value = response ?: null

            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }


}