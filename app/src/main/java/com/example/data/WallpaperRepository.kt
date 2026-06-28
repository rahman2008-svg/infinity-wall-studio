package com.example.data

import com.example.data.db.DailySettingsDao
import com.example.data.db.FavoriteDao
import com.example.data.models.DailySettings
import com.example.data.models.FavoriteWallpaper
import kotlinx.coroutines.flow.Flow

class WallpaperRepository(
    private val favoriteDao: FavoriteDao,
    private val dailySettingsDao: DailySettingsDao
) {
    val favorites: Flow<List<FavoriteWallpaper>> = favoriteDao.getAllFavorites()
    val dailySettings: Flow<DailySettings?> = dailySettingsDao.getSettings()

    suspend fun addFavorite(wallpaper: FavoriteWallpaper) {
        favoriteDao.insertFavorite(wallpaper)
    }

    suspend fun removeFavorite(wallpaper: FavoriteWallpaper) {
        favoriteDao.deleteFavorite(wallpaper)
    }

    suspend fun isFavorite(id: String): Boolean {
        return favoriteDao.isFavorite(id)
    }

    suspend fun updateDailySettings(isEnabled: Boolean, categoryId: String) {
        val settings = DailySettings(
            isEnabled = isEnabled,
            categoryId = categoryId,
            lastUpdated = System.currentTimeMillis()
        )
        dailySettingsDao.saveSettings(settings)
    }

    suspend fun clearDailySettings() {
        dailySettingsDao.clearSettings()
    }
}
