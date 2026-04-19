package com.codex.izmireshot.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.appDataStore by preferencesDataStore(name = "eshot_preferences")

class AppPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val useDynamicColors: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[USE_DYNAMIC_COLORS] ?: true
    }

    suspend fun setUseDynamicColors(value: Boolean) {
        dataStore.edit { prefs -> prefs[USE_DYNAMIC_COLORS] = value }
    }

    private companion object {
        val USE_DYNAMIC_COLORS = booleanPreferencesKey("use_dynamic_colors")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.appDataStore
}
