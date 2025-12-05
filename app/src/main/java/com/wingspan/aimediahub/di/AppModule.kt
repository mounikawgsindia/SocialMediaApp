package com.wingspan.aimediahub.di

import com.wingspan.aimediahub.networks.FacebookApi
import com.wingspan.aimediahub.repository.FacebookRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFacebookRepository(api: FacebookApi): FacebookRepository {
        return FacebookRepository(api)
    }
}
