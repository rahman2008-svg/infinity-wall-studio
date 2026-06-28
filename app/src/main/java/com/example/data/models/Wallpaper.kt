package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Wallpaper(
    val id: String,
    val url: String,
    val thumbnailUrl: String,
    val title: String,
    val author: String,
    val categoryId: String
)

data class WallpaperCategory(
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val wallpapers: List<Wallpaper> = emptyList()
)

@Entity(tableName = "favorites")
data class FavoriteWallpaper(
    @PrimaryKey val id: String,
    val url: String,
    val thumbnailUrl: String,
    val title: String,
    val author: String,
    val categoryId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_settings")
data class DailySettings(
    @PrimaryKey val id: Int = 1,
    val isEnabled: Boolean,
    val categoryId: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
