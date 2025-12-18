package pg.mobile.projectpampricil.data

data class PlaceModel(
    val name: String = "",
    val description: String = "",
    val rating: Double = 0.0,
    val theme: String = ""
)



data class Comment(
    val text: String = "",
    val rating: Int = 0
)
