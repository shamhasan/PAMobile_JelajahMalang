package ih.pam.pamobile_jelahjahmalang.model

data class PlaceModel(
    val name: String = "",
    val description: String = "",
    val rating: Double = 0.0,
    val theme: String = "",
    val image: String = ""
)



data class Comment(
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val rating: Int = 0,
    val timestamp: Long = 0
)
