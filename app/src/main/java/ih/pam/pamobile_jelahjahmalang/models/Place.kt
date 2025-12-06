package ih.pam.pamobile_jelahjahmalang.models
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName


data
class Place(
    val id: String ="",
    val name: String = " ",
    val description: String = "",
    val address: String = "",
    val rating: Double = 0.0,
    val imageUrl: String = "",
    // PENTING: Nama variabel harus 'geoLocation' sesuai yang kamu tulis di Firebase
    val geoLocation: GeoPoint? = null
)
