package com.example.flashmind.data.network
import android.content.Context
import com.example.flashmind.presentation.utils.getSignatureSha1
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val sha1 = getSignatureSha1(context)
        val packageName = context.packageName

        // Construye la nueva petición añadiendo los headers
        val newRequest = chain.request().newBuilder()
            .addHeader("X-Android-Package", packageName)
            .addHeader("X-Android-Cert", sha1 ?: "")
            .build()

        return chain.proceed(newRequest)
    }
}