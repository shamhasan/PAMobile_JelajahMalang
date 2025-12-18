package ih.pam.pamobile_jelahjahmalang.utils


import ih.pam.pamobile_jelahjahmalang.model.Comment
import ih.pam.pamobile_jelahjahmalang.model.PlaceModel
import ih.pam.pamobile_jelahjahmalang.network.ApiServicePricil
import java.util.UUID

//
class PlaceRepository(private val apiService: ApiServicePricil) {

    // Ambil LIST JELAJAHMALANG!!!
    suspend fun fetchPlaces(): List<PlaceModel> {
        return apiService.getPlaces().values.toList()
    }

    // Ambil komentar
    suspend fun fetchComments(placeName: String): List<Comment> {
        val result = apiService.getComments(placeName)
        return result?.values?.toList() ?: emptyList()
    }

    // Tambah komentar
    suspend fun addComment(placeName: String, text: String, rating: Int) {
        val id = UUID.randomUUID().toString()   // ID unik buat path {id}.json
        val comment = Comment(text = text, rating = rating)

        apiService.addComment(
            placeName = placeName,
            id = id,
            comment = comment
        )
    }

    // Update rating
    suspend fun updateRating(placeName: String, rating: Double) {
        apiService.updateRating(placeName, rating)
    }

    //FAVORITE

    suspend fun fetchFavorites(): Set<String> {
        val map = apiService.getFavorites()
        return map?.filterValues { it }?.keys?.toSet() ?: emptySet()
    }

    suspend fun setFavorite(placeName: String, isFavorite: Boolean) {
        if (isFavorite) {
            // FAVORIT → tulis true
            apiService.setFavorite(placeName, true)
        } else {
            // UNFAVORIT → hapus key‑nya
            apiService.removeFavorite(placeName)
        }
    }
}
