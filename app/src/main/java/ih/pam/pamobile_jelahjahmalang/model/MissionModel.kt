package ih.pam.pamobile_jelahjahmalang.model

import com.google.gson.annotations.SerializedName

data class FirestoreResponse(
    @SerializedName("documents")
    val documents: List<MissionDocuments>? = null
)

data class MissionDocuments(
    @SerializedName("name")
    val name: String,

    @SerializedName("fields")
    val fields: MissionFields? = null
){
    fun getCleanId(): String{
        return name.substringAfterLast("/");
    }
}

data class MissionFields(

    // --- Data Teks (String) ---
    @SerializedName("title")
    val title: StringValue? = null,

    @SerializedName("missionTask")
    val missionTask: StringValue? = null,

    @SerializedName("locationName")
    val locationName: StringValue? = null,

    @SerializedName("address")
    val address: StringValue? = null,

    @SerializedName("level")
    val level: StringValue? = null,

    // --- Data Angka (Integer & Double) ---

    @SerializedName("points")
    val points: IntegerValue? = null,

    @SerializedName("latitude")
    val latitude: DoubleValue? = null,

    @SerializedName("longitude")
    val longitude: DoubleValue? = null
)



data class StringValue(
    @SerializedName("stringValue")
    val value: String = ""
)
    
data class IntegerValue(
    @SerializedName("integerValue")
    val value: String = "0"
    // firestore mengirim int sebagai string
)

data class DoubleValue(
    @SerializedName("doubleValue")
    val value: Double = 0.0
)

data class ArrayValue(
    @SerializedName("values")
    val values: List<StringValue>? = emptyList()
)
