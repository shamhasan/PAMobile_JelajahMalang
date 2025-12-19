package ih.pam.pamobile_jelahjahmalang.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val user = FirebaseAuth.getInstance().currentUser

        val token = runBlocking {
            user?.getIdToken(false)?.await()?.token
        }

        val original = chain.request()

        val newUrl = original.url.newBuilder()
            .addQueryParameter("auth", token)
            .build()

        val request = original.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }
}
