package com.codex.izmireshot.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EshotDatabase =
        Room.databaseBuilder(context, EshotDatabase::class.java, "izmir-eshot.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTransportDao(database: EshotDatabase): TransportDao = database.transportDao()

    @Provides
    fun provideUserDao(database: EshotDatabase): UserDao = database.userDao()
}
