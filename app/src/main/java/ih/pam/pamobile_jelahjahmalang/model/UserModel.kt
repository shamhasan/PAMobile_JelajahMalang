package ih.pam.pamobile_jelahjahmalang.model

import com.google.gson.annotations.SerializedName

// Wrapper Utama
data class UserDocuments(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("fields")
    val fields: UserFields? = null
)

// Isi Data User
data class UserFields(
    @SerializedName("email") // Huruf kecil biasanya standar, tapi sesuaikan dengan DB kamu
    val email: StringValue? = null,

    @SerializedName("userName")
    val userName: StringValue? = null,

    @SerializedName("totalPoints")
    val totalPoints: IntegerValue? = null,

    // Array di Firestore REST memiliki struktur bertingkat: arrayValue -> values
    @SerializedName("completedMissions")
    val completedMissions: FirestoreArray? = null
)


// Level 1: Pembungkus arrayValue
data class FirestoreArray(
    @SerializedName("arrayValue")
    val arrayValue: FirestoreArrayContent? = null
)

// Level 2: Isi "values"
data class FirestoreArrayContent(
    @SerializedName("values")
    val values: List<StringValue>? = emptyList()
)