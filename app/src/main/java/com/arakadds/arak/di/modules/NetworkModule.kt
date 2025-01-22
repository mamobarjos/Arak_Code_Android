package com.arakadds.arak.di.modules

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.arakadds.arak.BuildConfig

import com.arakadds.arak.network.NetWorkApi
import com.arakadds.arak.utils.Constants.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.readystatesoftware.chuck.ChuckInterceptor
import java.lang.reflect.Type
import java.time.Instant

@Module
object NetworkModule {

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Singleton
    @Provides
    fun providesOkhttp(app: Application): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Cache-Control", "no-cache, no-store")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .method(original.method, original.body)
                
                chain.proceed(requestBuilder.build())
            }
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder()
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .build()
            }
            .cache(null) // Disable cache completely
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    fun providesRetrofit(client: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(Instant::class.java, InstantTypeAdapter)
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Singleton
    @Provides
    fun providesApiService(retrofit: Retrofit): NetWorkApi =
        retrofit.create(NetWorkApi::class.java)

    object InstantTypeAdapter : JsonSerializer<Instant>, JsonDeserializer<Instant> {
        override fun serialize(src: Instant?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return JsonPrimitive(src.toString())
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Instant {
            return Instant.parse(json?.asString)
        }
    }
}