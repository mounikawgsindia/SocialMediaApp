package com.wingspan.aimediahub.networks
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://graph.facebook.com/v19.0/"

    @Provides
    @Singleton
    @Named("Facebook")
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFacebookApi( @Named("Facebook")retrofit: Retrofit): FacebookApi {
        return retrofit.create(FacebookApi::class.java)
    }
}
