package com.example.jelajahmalang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import ih.pam.pamobile_jelahjahmalang.models.Mission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MissionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _missionList = MutableStateFlow<List<Mission>>(emptyList())
    val missionList: StateFlow<List<Mission>> = _missionList

    init {
        fetchMissionsWithAddresses()
    }

    private fun fetchMissionsWithAddresses() {
        viewModelScope.launch {
            try {

                Log.d("MissionViewModel", "Mulai mengambil data...")
                // 1. Ambil semua data Misi dulu
                val missionsSnapshot = db.collection("missions").get().await()
                Log.d("MissionViewModel", "Ditemukan ${missionsSnapshot.size()} dokumen di collection 'missions'")

                val tempMissionList = missionsSnapshot.documents.mapNotNull { doc ->
                    try {
                        // Pastikan Mission punya default constructor
                        val mission = doc.toObject(Mission::class.java)
                        // Copy ID dari dokumen Firestore ke property id di object Mission
                        mission?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e("MissionViewModel", "Gagal parsing misi: ${doc.id}", e)
                        null
                    }
                }
                if (tempMissionList.isEmpty()) {
                    Log.w("MissionViewModel", "List misi kosong. Cek nama collection di Firestore apakah benar 'missions'?")
                    _missionList.value = emptyList()
                    return@launch
                }

                // 2. Ambil semua data Places (untuk dicocokkan alamatnya)
                // Tips: Jika data places sangat banyak, sebaiknya jangan fetch semua,
                // tapi fetch by ID. Namun untuk skala aplikasi ini, fetch semua masih oke.
                val placesSnapshot = db.collection("places").get().await()
                Log.d("MissionViewModel", "Ditemukan ${placesSnapshot.size()} dokumen di collection 'places'")


                // Buat Map (Kamus) -> ID Tempat : Alamat Lengkap
                val addressMap = placesSnapshot.documents.associate { doc ->
                    doc.id to (doc.getString("address") ?: "Alamat tidak tersedia")
                }

                // 3. GABUNGKAN DATA (JOIN)
                // Kita update field 'placeDetailAddress' di setiap misi berdasarkan 'placeId'
                val mergedList = tempMissionList.map { mission ->
                    // Cari alamat di Map berdasarkan placeId milik misi
                    val realAddress = addressMap[mission.placeId] ?: "Lokasi belum terdata (ID: ${mission.placeId})"

                    // Salin object misi dan isi alamatnya
                    mission.copy(placeDetailAddress = realAddress)
                }

                // 4. Update ke UI
                Log.d("MissionViewModel", "Data siap ditampilkan: ${mergedList.size} item")
                _missionList.value = mergedList

            } catch (e: Exception) {
                Log.e("MissionViewModel", "Error fetching data: ", e)
            }
        }
    }
}