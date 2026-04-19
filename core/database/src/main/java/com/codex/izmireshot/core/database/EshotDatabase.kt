package com.codex.izmireshot.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.codex.izmireshot.core.model.FavoriteType

@Database(
    entities = [
        BusLineEntity::class,
        BusStopEntity::class,
        RoutePointEntity::class,
        DepartureTimeEntity::class,
        AnnouncementEntity::class,
        ConnectionLineEntity::class,
        FavoriteEntity::class,
        RecentSearchEntity::class,
        SyncStatusEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(DatabaseConverters::class)
abstract class EshotDatabase : RoomDatabase() {
    abstract fun transportDao(): TransportDao
    abstract fun userDao(): UserDao
}

class DatabaseConverters {
    @TypeConverter
    fun favoriteTypeToString(type: FavoriteType): String = type.name

    @TypeConverter
    fun favoriteTypeFromString(value: String): FavoriteType = FavoriteType.valueOf(value)
}
