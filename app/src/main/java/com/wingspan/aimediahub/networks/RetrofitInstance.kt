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

    // ---------- PROVIDE OKHTTP ----------
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()



    // ---------- BACKEND RETROFIT ----------
    private var backendRetrofit: Retrofit? = null

    @Provides
    @Singleton
    @Named("Backend")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        if (backendRetrofit == null) {
            backendRetrofit = Retrofit.Builder()
                .baseUrl("https://automatedpostingbackend.onrender.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return backendRetrofit!!
    }

    @Provides
    @Singleton
    @Named("Backend")
    fun provideApiService(@Named("Backend") retrofit: Retrofit): ApiServices =
        retrofit.create(ApiServices::class.java)

    // ---------- BACKEND 2 (NEW) ----------
    @Provides
    @Singleton
    @Named("BackendV2")
    fun provideBackendV2Retrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://automatedpostingbackend-h9dc.onrender.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("BackendV2")
    fun provideApiServiceV2(
        @Named("BackendV2") retrofit: Retrofit
    ): ApiServices =
        retrofit.create(ApiServices::class.java)

    // ---------- BLUESKY RETROFIT ----------
    @Provides
    @Singleton
    @Named("Bluesky")
    fun provideBlueskyRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://bsky.social/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideBlueskyApi(@Named("Bluesky") retrofit: Retrofit): BlueskyApi =
        retrofit.create(BlueskyApi::class.java)
}
