package com.codex.izmireshot.core.data

import com.codex.izmireshot.core.database.UserDao
import com.codex.izmireshot.core.model.Favorite
import com.codex.izmireshot.core.model.FavoriteType
import com.codex.izmireshot.core.model.RecentSearch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
) : FavoritesRepository {
    override fun observeFavorites(): Flow<List<Favorite>> =
        userDao.observeFavorites().map { rows -> rows.map { it.toModel() } }

    override fun observeIsFavorite(type: FavoriteType, id: String): Flow<Boolean> =
        userDao.observeIsFavorite(type, id)

    override suspend fun toggleFavorite(favorite: Favorite) {
        val exists = userDao.observeIsFavorite(favorite.type, favorite.id).first()
        if (exists) userDao.deleteFavorite(favorite.type, favorite.id) else userDao.upsertFavorite(favorite.toEntity(System.currentTimeMillis()))
    }
}

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
) : SearchRepository {
    override fun observeRecentSearches(): Flow<List<RecentSearch>> =
        userDao.observeRecentSearches().map { rows -> rows.map { it.toModel() } }

    override suspend fun record(search: RecentSearch) {
        userDao.upsertRecentSearch(search.toEntity())
    }
}
