package com.gotourntravels.network

import com.gotourntravels.BuildConfig
import com.gotourntravels.network.dto.ApiError
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.gotourntravels.datastore.UserPrefs

class ApiException(message: String) : Exception(message)

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttp(prefs: UserPrefs): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val token = runBlocking { prefs.token.first() }
            val req = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .apply { if (!token.isNullOrBlank()) addHeader("Authorization", "Bearer $token") }
                .build()
            val resp = chain.proceed(req)
            // Surface API error body
            if (!resp.isSuccessful) {
                val raw = resp.body?.string().orEmpty()
                val msg = try {
                    Gson().fromJson(raw, ApiError::class.java)?.message
                } catch (_: Exception) { null } ?: "Request failed (${resp.code})"
                throw ApiException(msg)
            }
            resp
        }

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        // Register a factory that maps MongoDB _id → id field on our models.
        // Easiest: rely on @SerializedName annotations on each `id` field.
        // For brevity here we use a custom FieldNamingStrategy.
        return GsonBuilder()
            .setFieldNamingStrategy { f ->
                if (f.name == "id") "_id" else f.name
            }
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): GoTourApi = retrofit.create(GoTourApi::class.java)
}
