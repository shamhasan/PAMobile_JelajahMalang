package ih.pam.pamobile_jelahjahmalang.network

import ih.pam.pamobile_jelahjahmalang.model.FirestoreResponse
import ih.pam.pamobile_jelahjahmalang.model.MissionDocuments
import ih.pam.pamobile_jelahjahmalang.model.UserDocuments
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ApiService {

    // Untuk mission
    @GET("projects/my-pam-test/databases/(default)/documents/missions")
    suspend fun getMission(): FirestoreResponse

    @GET("projects/my-pam-test/databases/(default)/documents/missions/{id}")
    suspend fun getMissionById(
        @Path("id") id: String
    ): MissionDocuments


    // Untuk user
    @GET("projects/my-pam-test/databases/(default)/documents/users/{uid}")
    suspend fun getUser(
        @Path("uid") uid: String
    ): UserDocuments

    @PATCH("projects/my-pam-test/databases/(default)/documents/users/{uid}")
    suspend fun createUserProfile(
        @Path("uid") uid: String,
        @Body body: UserDocuments
    ): UserDocuments

}