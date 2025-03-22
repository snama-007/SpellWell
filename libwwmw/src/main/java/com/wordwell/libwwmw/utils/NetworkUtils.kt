package com.wordwell.libwwmw.utils

import android.content.Context
import com.wordwell.libwwmw.data.api.MerriamWebsterApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// NetworkUtils provides utility functions for setting up the API client.
// It configures the OkHttpClient and Retrofit for network operations.
internal object NetworkUtils {
    private const val TIMEOUT_SECONDS = 30L // Timeout duration for network requests

    /**
     * Creates an instance of MerriamWebsterApi using Retrofit.
     * Configures the OkHttpClient with logging and API key interceptors.
     * @param context The application context
     * @return An instance of MerriamWebsterApi
     */
    fun createApiService(context: Context): MerriamWebsterApi {
        // Verify app integrity
        require(SecurityUtils.verifyAppIntegrity(context)) { "App integrity check failed" }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val url = original.url.newBuilder()
                    .addQueryParameter("key", Constants.getApiKey(context))
                    .build()
                chain.proceed(original.newBuilder().url(url).build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(MerriamWebsterApi.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MerriamWebsterApi::class.java)
    }

} 