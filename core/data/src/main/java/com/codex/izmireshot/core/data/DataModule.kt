package com.codex.izmireshot.core.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindTransportRepository(impl: TransportRepositoryImpl): TransportRepository

    @Binds
    @Singleton
    abstract fun bindNearbyRepository(impl: TransportRepositoryImpl): NearbyRepository

    @Binds
    @Singleton
    abstract fun bindAnnouncementsRepository(impl: TransportRepositoryImpl): AnnouncementsRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository
}
