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
    private const val INSTAGRAM_BASE_URL = "https://graph.instagram.com/"


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


    // ------------------ Instagram ------------------
    @Provides
    @Singleton
    @Named("Instagram")
    fun provideInstagramRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(INSTAGRAM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideInstagramApi(@Named("Instagram") retrofit: Retrofit): InstagramApi {
        return retrofit.create(InstagramApi::class.java)
    }
}
