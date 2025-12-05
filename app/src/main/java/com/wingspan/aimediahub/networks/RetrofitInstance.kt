package com.wingspan.aimediahub.networks

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RetrofitInstance {

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // connection timeout
        .readTimeout(30, TimeUnit.SECONDS)    // server response timeout
        .writeTimeout(30, TimeUnit.SECONDS)   // sending data timeout
        .build()



    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://automatedpostingbackend.onrender.com/")
            .client(okHttpClient) // Ensure BASE_URL is defined in gradle.properties
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    @Provides
    @Singleton
    @Named("Backend")
    fun provideRetrofit(): Retrofit = retrofit


    @Provides
    @Singleton
    fun provideApiService(
        @Named("Backend") retrofit: Retrofit
    ): ApiServices {
        return retrofit.create(ApiServices::class.java)
    }

}