package com.example.data.db

import android.content.Context
import androidx.room.*
import com.example.data.models.DailySettings
import com.example.data.models.FavoriteWallpaper
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteWallpaper>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(wallpaper: FavoriteWallpaper)

    @Delete
    suspend fun deleteFavorite(wallpaper: FavoriteWallpaper)

    @Query("SELECT EXISTS(SELECT * FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean
}

@Dao
interface DailySettingsDao {
    @Query("SELECT * FROM daily_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<DailySettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: DailySettings)

    @Query("DELETE FROM daily_settings")
    suspend fun clearSettings()
}

@Database(entities = [FavoriteWallpaper::class, DailySettings::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun dailySettingsDao(): DailySettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "infinity_wall_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
