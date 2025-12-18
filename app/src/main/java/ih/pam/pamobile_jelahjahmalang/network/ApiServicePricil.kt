package ih.pam.pamobile_jelahjahmalang.network

import ih.pam.pamobile_jelahjahmalang.model.Comment
import ih.pam.pamobile_jelahjahmalang.model.PlaceModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServicePricil {

    // Ambil semua place
    @GET("places.json")
    suspend fun getPlaces(): Map<String, PlaceModel>

    // Ambil semua komentar 1 tempat
    @GET("comments/{place}.json")
    suspend fun getComments(
        @Path("place") placeName: String
    ): Map<String, Comment>?

    // Tambah komentar baru
    @PUT("comments/{place}/{id}.json")
    suspend fun addComment(
        @Path("place") placeName: String,
        @Path("id") id: String,
        @Body comment: Comment
    )

    // Update rating tempat
    @PUT("places/{place}/rating.json")
    suspend fun updateRating(
        @Path("place") placeName: String,
        @Body rating: Double
    )

    // ambil semua favorit
    @GET("favorites.json")
    suspend fun getFavorites(): Map<String, Boolean>?

    // set favorit = true
    @PUT("favorites/{place}.json")
    suspend fun setFavorite(
        @Path("place") placeName: String,
        @Body value: Boolean
    )

    // hapus favorit (UNfavorite)
    @DELETE("favorites/{place}.json")
    suspend fun removeFavorite(
        @Path("place") placeName: String
    )
}
