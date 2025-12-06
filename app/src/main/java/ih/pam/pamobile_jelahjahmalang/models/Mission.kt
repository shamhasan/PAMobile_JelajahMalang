package ih.pam.pamobile_jelahjahmalang.models

import com.google.firebase.firestore.Exclude

data class Mission(
    val id: String = "",
    val title: String = "",
    val level: String = "",
    val points: Int = 0,
    val missionTask: String = "",
    val locationName: String = "", // Nama lokasi untuk ditampilkan di kartu
    val placeId: String = "",
    // ID referensi ke collection 'places'
    @get:Exclude
    var placeDetailAddress: String = "Memuat alamat...",
)