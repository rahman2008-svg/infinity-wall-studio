package com.example.ui

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.data.WallpaperData
import com.example.data.WallpaperRepository
import com.example.data.db.AppDatabase
import com.example.data.models.DailySettings
import com.example.data.models.FavoriteWallpaper
import com.example.data.models.Wallpaper
import com.example.data.models.WallpaperCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class WallpaperType {
    HOME, LOCK, BOTH
}

class WallpaperViewModel(private val repository: WallpaperRepository) : ViewModel() {

    // Hardcoded categories list from WallpaperData
    val categories: List<WallpaperCategory> = WallpaperData.categories

    // Reactive streams from database
    val favorites: StateFlow<List<FavoriteWallpaper>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailySettings: StateFlow<DailySettings?> = repository.dailySettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current states for user navigation & interaction
    private val _selectedWallpaper = MutableStateFlow<Wallpaper?>(null)
    val selectedWallpaper: StateFlow<Wallpaper?> = _selectedWallpaper.asStateFlow()

    private val _selectedCategory = MutableStateFlow<WallpaperCategory?>(null)
    val selectedCategory: StateFlow<WallpaperCategory?> = _selectedCategory.asStateFlow()

    private val _customPickedUri = MutableStateFlow<Uri?>(null)
    val customPickedUri: StateFlow<Uri?> = _customPickedUri.asStateFlow()

    // Palette customizer
    private val _customColor = MutableStateFlow(Color(0xFF263238)) // Default Slate-Gray
    val customColor: StateFlow<Color> = _customColor.asStateFlow()

    // Progress and feedback state
    private val _isApplying = MutableStateFlow(false)
    val isApplying: StateFlow<Boolean> = _isApplying.asStateFlow()

    private val _applyResult = MutableSharedFlow<String>()
    val applyResult: SharedFlow<String> = _applyResult.asSharedFlow()

    // Check if current wallpaper is favorited
    fun isCurrentFavorited(wallpaperId: String): Flow<Boolean> {
        return favorites.map { list -> list.any { it.id == wallpaperId } }
    }

    fun selectWallpaper(wallpaper: Wallpaper?) {
        _selectedWallpaper.value = wallpaper
    }

    fun selectCategory(category: WallpaperCategory?) {
        _selectedCategory.value = category
    }

    fun setCustomPickedUri(uri: Uri?) {
        _customPickedUri.value = uri
    }

    fun updateCustomColor(color: Color) {
        _customColor.value = color
    }

    fun toggleFavorite(wallpaper: Wallpaper) {
        viewModelScope.launch {
            val list = favorites.value
            val existing = list.find { it.id == wallpaper.id }
            if (existing != null) {
                repository.removeFavorite(existing)
                _applyResult.emit("Removed from Favorites!")
            } else {
                val fav = FavoriteWallpaper(
                    id = wallpaper.id,
                    url = wallpaper.url,
                    thumbnailUrl = wallpaper.thumbnailUrl,
                    title = wallpaper.title,
                    author = wallpaper.author,
                    categoryId = wallpaper.categoryId
                )
                repository.addFavorite(fav)
                _applyResult.emit("Added to Favorites!")
            }
        }
    }

    // Apply online image wallpaper
    fun applyWallpaper(context: Context, imageUrl: String, type: WallpaperType) {
        viewModelScope.launch {
            _isApplying.value = true
            val success = withContext(Dispatchers.IO) {
                try {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    val loader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .allowHardware(false) // Required to convert to bitmap
                        .build()
                    val result = loader.execute(request)
                    if (result is SuccessResult) {
                        val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
                        if (bitmap != null) {
                            applyBitmapToSystem(wallpaperManager, bitmap, type)
                            true
                        } else false
                    } else false
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
            _isApplying.value = false
            if (success) {
                _applyResult.emit("Wallpaper applied successfully!")
            } else {
                _applyResult.emit("Failed to apply wallpaper.")
            }
        }
    }

    // Apply programmatically generated Solid Color wallpaper
    fun applySolidColorWallpaper(context: Context, color: Color, type: WallpaperType) {
        viewModelScope.launch {
            _isApplying.value = true
            val success = withContext(Dispatchers.IO) {
                try {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    // Generate screen-sized solid bitmap (typical fallback 1080x1920)
                    val bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(color.toArgb())
                    applyBitmapToSystem(wallpaperManager, bitmap, type)
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
            _isApplying.value = false
            if (success) {
                _applyResult.emit("Solid Color applied successfully!")
            } else {
                _applyResult.emit("Failed to apply color.")
            }
        }
    }

    // Apply local picked photo Uri
    fun applyLocalPhotoWallpaper(context: Context, uri: Uri, type: WallpaperType) {
        viewModelScope.launch {
            _isApplying.value = true
            val success = withContext(Dispatchers.IO) {
                try {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    context.contentResolver.openInputStream(uri).use { stream ->
                        val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                        if (bitmap != null) {
                            applyBitmapToSystem(wallpaperManager, bitmap, type)
                            true
                        } else false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
            _isApplying.value = false
            if (success) {
                _applyResult.emit("Local Photo applied successfully!")
            } else {
                _applyResult.emit("Failed to apply local photo.")
            }
        }
    }

    private fun applyBitmapToSystem(manager: WallpaperManager, bitmap: Bitmap, type: WallpaperType) {
        when (type) {
            WallpaperType.HOME -> {
                manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
            }
            WallpaperType.LOCK -> {
                manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
            }
            WallpaperType.BOTH -> {
                manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
            }
        }
    }

    // Daily Wallpaper Surprise Scheduler
    fun enableDailyWallpaper(categoryId: String) {
        viewModelScope.launch {
            repository.updateDailySettings(isEnabled = true, categoryId = categoryId)
            _applyResult.emit("Daily Wallpaper active for ${categoryId.replaceFirstChar { it.uppercase() }}!")
        }
    }

    fun disableDailyWallpaper() {
        viewModelScope.launch {
            repository.clearDailySettings()
            _applyResult.emit("Daily Wallpaper cycle disabled.")
        }
    }

    // Trigger instant daily wallpaper update
    fun triggerDailyWallpaperCycleNow(context: Context, categoryId: String) {
        viewModelScope.launch {
            _isApplying.value = true
            val randomWallpaper = WallpaperData.getRandomWallpaperForCategory(categoryId)
            if (randomWallpaper != null) {
                val success = withContext(Dispatchers.IO) {
                    try {
                        val wallpaperManager = WallpaperManager.getInstance(context)
                        val loader = ImageLoader(context)
                        val request = ImageRequest.Builder(context)
                            .data(randomWallpaper.url)
                            .allowHardware(false)
                            .build()
                        val result = loader.execute(request)
                        if (result is SuccessResult) {
                            val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
                            if (bitmap != null) {
                                applyBitmapToSystem(wallpaperManager, bitmap, WallpaperType.BOTH)
                                true
                            } else false
                        } else false
                    } catch (e: Exception) {
                        e.printStackTrace()
                        false
                    }
                }
                _isApplying.value = false
                if (success) {
                    repository.updateDailySettings(isEnabled = true, categoryId = categoryId)
                    _applyResult.emit("Daily cycle triggered! Enjoy your new wallpaper.")
                } else {
                    _applyResult.emit("Failed to update daily wallpaper.")
                }
            } else {
                _isApplying.value = false
                _applyResult.emit("Could not retrieve image.")
            }
        }
    }
}

// ViewModel Factory
class WallpaperViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WallpaperViewModel::class.java)) {
            val db = AppDatabase.getDatabase(context)
            val repository = WallpaperRepository(db.favoriteDao(), db.dailySettingsDao())
            @Suppress("UNCHECKED_CAST")
            return WallpaperViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
