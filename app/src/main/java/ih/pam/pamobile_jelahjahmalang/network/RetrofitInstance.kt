package ih.pam.pamobile_jelahjahmalang.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://jelajahmalang-3a6fa-default-rtdb.firebaseio.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiServicePricil by lazy {
        retrofit.create(ApiServicePricil::class.java)
    }
}
