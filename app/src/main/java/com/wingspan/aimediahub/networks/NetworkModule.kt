package com.wingspan.aimediahub.networks
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://graph.facebook.com/v19.0/"
    private const val INSTAGRAM_BASE_URL = "https://graph.instagram.com/"
    private const val TWITTER_BASE_URL = "https://api.twitter.com/1.1/"

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



    //------------------------------twitter--------------------------
    @Provides
    @Singleton
    @Named("Twitter")
    fun provideTwitterRetrofit(@Named("TwitterInterceptor") interceptor: okhttp3.Interceptor): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(TWITTER_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTwitterApi(@Named("Twitter") retrofit: Retrofit): TwitterApi1 {
        return retrofit.create(TwitterApi1::class.java)
    }

    @Provides
    @Singleton
    @Named("TwitterInterceptor")
    fun provideTwitterInterceptor(): okhttp3.Interceptor {
        return okhttp3.Interceptor { chain ->
            val request = chain.request().newBuilder()
                // add any headers here if needed, e.g. Authorization
                .build()
            chain.proceed(request)
        }
    }
}
