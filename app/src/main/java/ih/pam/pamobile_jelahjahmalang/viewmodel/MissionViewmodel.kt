package ih.pam.pamobile_jelahjahmalang.viewmodel

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