package com.example.ui.screens

import android.app.WallpaperManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import com.example.ui.theme.SleekSurfaceVariant
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.WallpaperData
import com.example.data.models.FavoriteWallpaper
import com.example.data.models.Wallpaper
import com.example.data.models.WallpaperCategory
import com.example.ui.WallpaperType
import com.example.ui.WallpaperViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: WallpaperViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Navigation and screen management
    var currentTab by remember { mutableStateOf("explore") } // explore, live, daily, palette, favorites
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedWallpaper by viewModel.selectedWallpaper.collectAsStateWithLifecycle()
    var showAboutSheet by remember { mutableStateOf(false) }

    val isApplying by viewModel.isApplying.collectAsStateWithLifecycle()

    // Observe result toast messages
    LaunchedEffect(Unit) {
        viewModel.applyResult.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setCustomPickedUri(uri)
            viewModel.selectWallpaper(
                Wallpaper(
                    id = "local_picked",
                    url = uri.toString(),
                    thumbnailUrl = uri.toString(),
                    title = "My Photo",
                    author = "Gallery Picker",
                    categoryId = "local"
                )
            )
        }
    }

    Scaffold(
        topBar = {
            if (selectedWallpaper == null) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AllInclusive,
                                contentDescription = "Infinity Logo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Infinity Wall Studio",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    },
                    navigationIcon = {
                        if (selectedCategory != null) {
                            IconButton(
                                onClick = { viewModel.selectCategory(null) },
                                modifier = Modifier.testTag("back_category_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back to categories"
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showAboutSheet = true },
                            modifier = Modifier.testTag("about_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "About Developer & Company",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = {
            if (selectedWallpaper == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .drawWithContent {
                            drawContent()
                            // Top border representing border-[#E6DCDC]
                            drawLine(
                                color = SleekSurfaceVariant,
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                                strokeWidth = 2f
                            )
                        }
                ) {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    NavigationBarItem(
                        selected = currentTab == "explore" && selectedCategory == null,
                        onClick = {
                            viewModel.selectCategory(null)
                            currentTab = "explore"
                        },
                        icon = { Icon(Icons.Outlined.Explore, contentDescription = "Explore") },
                        label = { Text("Explore", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_explore")
                    )
                    NavigationBarItem(
                        selected = currentTab == "live",
                        onClick = {
                            viewModel.selectCategory(null)
                            currentTab = "live"
                        },
                        icon = { Icon(Icons.Outlined.AutoAwesome, contentDescription = "Cosmic Live") },
                        label = { Text("Live", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_live")
                    )
                    NavigationBarItem(
                        selected = currentTab == "daily",
                        onClick = {
                            viewModel.selectCategory(null)
                            currentTab = "daily"
                        },
                        icon = { Icon(Icons.Outlined.Today, contentDescription = "Daily") },
                        label = { Text("Daily", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_daily")
                    )
                    NavigationBarItem(
                        selected = currentTab == "palette",
                        onClick = {
                            viewModel.selectCategory(null)
                            currentTab = "palette"
                        },
                        icon = { Icon(Icons.Outlined.Palette, contentDescription = "Palette Lab") },
                        label = { Text("Palette", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_palette")
                    )
                    NavigationBarItem(
                        selected = currentTab == "favorites",
                        onClick = {
                            viewModel.selectCategory(null)
                            currentTab = "favorites"
                        },
                        icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorites") },
                        label = { Text("Favorites", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_favorites")
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                selectedWallpaper != null -> {
                    WallpaperPreviewScreen(
                        wallpaper = selectedWallpaper!!,
                        viewModel = viewModel,
                        onBack = {
                            viewModel.selectWallpaper(null)
                            viewModel.setCustomPickedUri(null)
                        }
                    )
                }
                selectedCategory != null -> {
                    CategoryDetailView(
                        category = selectedCategory!!,
                        viewModel = viewModel
                    )
                }
                else -> {
                    when (currentTab) {
                        "explore" -> ExploreTab(
                            viewModel = viewModel,
                            onPickPhoto = {
                                photoPickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        )
                        "live" -> LiveWallpaperTab()
                        "daily" -> DailyWallpaperTab(viewModel = viewModel)
                        "palette" -> PaletteLabTab(viewModel = viewModel)
                        "favorites" -> FavoritesTab(viewModel = viewModel)
                    }
                }
            }

            if (isApplying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .pointerInput(Unit) {},
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Applying Wallpaper...",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Configuring Canvas details",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (showAboutSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showAboutSheet = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 8.dp,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(start = 20.dp, end = 20.dp, bottom = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. App Logo / Name Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AllInclusive,
                                contentDescription = "Infinity Logo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Infinity Wall Studio",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // 2. About Developer Section (Premium card)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Developer Avatar with initials
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(Color(0xFFD0E4FF), Color(0xFFA0CFFF))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "PR",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF1D1B1E)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = "Prince AR Abdur Rahman",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = "Independent App Developer",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Independent App Developer passionate about building modern Android applications, productivity tools, AI-powered experiences, media players, educational apps, and next-generation digital products.",
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Social Contacts header
                                Text(
                                    text = "Connect with Developer",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    letterSpacing = 0.5.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Contact Buttons
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // WhatsApp Button 1
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .clickable {
                                                try {
                                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                                        data = Uri.parse("https://api.whatsapp.com/send?phone=8801707424006")
                                                    }
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "WhatsApp: 01707424006", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Chat,
                                            contentDescription = "WhatsApp 1",
                                            tint = Color(0xFF25D366),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "WhatsApp: 01707424006",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    // WhatsApp Button 2
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .clickable {
                                                try {
                                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                                        data = Uri.parse("https://api.whatsapp.com/send?phone=8801796951709")
                                                    }
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "WhatsApp: 01796951709", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Chat,
                                            contentDescription = "WhatsApp 2",
                                            tint = Color(0xFF25D366),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "WhatsApp: 01796951709",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    // Facebook Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .clickable {
                                                try {
                                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/share/1BNn32qoJo/"))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Facebook link copied", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Public,
                                            contentDescription = "Facebook",
                                            tint = Color(0xFF1877F2),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Facebook Profile",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    // Instagram Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .clickable {
                                                try {
                                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/ur___abdur____rahman__2008"))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Instagram link copied", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Public,
                                            contentDescription = "Instagram",
                                            tint = Color(0xFFE1306C),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Instagram Profile",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // 3. About Company Section (NexVora Lab's Ofc)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Store,
                                            contentDescription = "Company",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = "NexVora Lab's Ofc",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = "Creative App Publisher",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "NexVora Lab's Ofc focuses on creating innovative Android applications designed to improve productivity, entertainment, learning, and digital experiences.",
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Mission",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Build fast, beautiful, privacy-friendly, and user-focused applications accessible to everyone.",
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // 4. Technical Information & Version
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Technical Information",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "Architecture: Jetpack Compose & Clean MVVM",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "v1.0.0",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 5. Credits & Legal
                        Text(
                            text = "Developed by Prince AR Abdur Rahman",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Published by NexVora Lab's Ofc",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "© 2026 NexVora Lab's Ofc. All Rights Reserved.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 1. EXPLORE TAB
// ==========================================
@Composable
fun ExploreTab(
    viewModel: WallpaperViewModel,
    onPickPhoto: () -> Unit
) {
    val categories = viewModel.categories
    val featuredWallpaper = remember {
        WallpaperData.categories.firstOrNull()?.wallpapers?.firstOrNull()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(3) }) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Text(
                    text = "On this device",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // My Photos Gradient Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.25f) // visually pleasing sleek proportion
                            .clickable { onPickPhoto() }
                            .testTag("picker_card"),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFD0E4FF), Color(0xFFA0CFFF))
                                    )
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.4f))
                                    .align(Alignment.Center)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AddPhotoAlternate,
                                    contentDescription = "Add Photo",
                                    tint = Color(0xFF1D1B1E),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.Center)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.Black.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "My photos",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Current Wallpaper Gradient Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.25f)
                            .clickable {
                                if (featuredWallpaper != null) {
                                    viewModel.selectWallpaper(featuredWallpaper)
                                }
                            }
                            .testTag("current_wallpaper_card"),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFFAD8FD), Color(0xFFD5A3FF))
                                    )
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.4f))
                                    .align(Alignment.Center)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Wallpaper,
                                    contentDescription = "Current Settings",
                                    tint = Color(0xFF1D1B1E),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.Center)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.Black.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Current wall",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Featured Spotlight",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (featuredWallpaper != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clickable { viewModel.selectWallpaper(featuredWallpaper) }
                            .testTag("featured_card"),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = featuredWallpaper.url,
                                contentDescription = featuredWallpaper.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.25f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        "DAILY PICK",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = featuredWallpaper.title,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "By ${featuredWallpaper.author}",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Categories",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Categories 3-Column Grid
        items(categories) { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f) // Sleek 3-column proportional aspect ratio
                    .clickable { viewModel.selectCategory(category) }
                    .testTag("category_card_${category.id}"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = category.coverUrl,
                        contentDescription = category.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = category.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${category.wallpapers.size} walls",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        item(span = { GridItemSpan(3) }) {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ==========================================
// 2. CATEGORY DETAIL SCREEN
// ==========================================
@Composable
fun CategoryDetailView(
    category: WallpaperCategory,
    viewModel: WallpaperViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredWallpapers = remember(searchQuery, category.wallpapers) {
        if (searchQuery.isBlank()) {
            category.wallpapers
        } else {
            category.wallpapers.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.author.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = category.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Premium Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search ${category.title}...", fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("wallpaper_search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredWallpapers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "No results",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No matching wallpapers found",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Try searching for another style or creator",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredWallpapers, key = { it.id }) { wallpaper ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.65f)
                            .clickable { viewModel.selectWallpaper(wallpaper) }
                            .testTag("wallpaper_item_${wallpaper.id}"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = wallpaper.thumbnailUrl,
                                contentDescription = wallpaper.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                                        )
                                    )
                            )
                            Text(
                                text = wallpaper.title,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. LIVE WALLPAPER TAB
// ==========================================
@Composable
fun LiveWallpaperTab() {
    val context = LocalContext.current

    var particleSpeed by remember { mutableFloatStateOf(1.0f) }
    var particleSize by remember { mutableFloatStateOf(5.0f) }
    var baseColorIndex by remember { mutableIntStateOf(0) }
    val colorPalettes = listOf(
        Pair(Color(0xFF6366F1), Color(0xFF38BDF8)), // Cosmic Indigo
        Pair(Color(0xFFEC4899), Color(0xFFF43F5E)), // Neon Rose
        Pair(Color(0xFF10B981), Color(0xFF059669)), // Mystic Emerald
        Pair(Color(0xFFF59E0B), Color(0xFFD97706))  // Liquid Gold
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cosmic Space Live",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "An interactive, real-time rendering space fluid generator. Ripple, flow and dynamic particles.",
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val touches = remember { mutableStateListOf<Pair<Float, Float>>() }
                val rippleRadii = remember { mutableStateListOf<Float>() }

                LaunchedEffect(touches.size) {
                    while (true) {
                        kotlinx.coroutines.delay(30)
                        for (i in rippleRadii.indices) {
                            if (i < rippleRadii.size) {
                                rippleRadii[i] = rippleRadii[i] + 12f
                            }
                        }
                    }
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                touches.add(Pair(offset.x, offset.y))
                                rippleRadii.add(10f)
                                if (touches.size > 5) {
                                    touches.removeAt(0)
                                    rippleRadii.removeAt(0)
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height

                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(colorPalettes[baseColorIndex].first.copy(alpha = 0.5f), Color(0xFF07040D)),
                            center = androidx.compose.ui.geometry.Offset(width / 2f, height / 2f),
                            radius = height * 0.8f
                        )
                    )

                    val starsCount = 30
                    for (i in 0 until starsCount) {
                        val starX = (width * ((i * 37) % 100) / 100f)
                        val starY = (height * ((i * 59) % 100) / 100f)
                        drawCircle(
                            color = colorPalettes[baseColorIndex].second.copy(alpha = 0.5f),
                            radius = particleSize * (1f + (i % 3) * 0.5f),
                            center = androidx.compose.ui.geometry.Offset(starX, starY)
                        )
                    }

                    for (i in touches.indices) {
                        if (i < rippleRadii.size && rippleRadii[i] < 300f) {
                            drawCircle(
                                color = colorPalettes[baseColorIndex].first,
                                radius = rippleRadii[i],
                                center = androidx.compose.ui.geometry.Offset(touches[i].first, touches[i].second),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f * (1f - rippleRadii[i] / 300f))
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "TAP TO TEST RIPPLES",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Engine Customizer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Theme Aura Color", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    colorPalettes.forEachIndexed { index, colorPair ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(colorPair.first, colorPair.second)
                                    )
                                )
                                .border(
                                    width = if (baseColorIndex == index) 3.dp else 0.dp,
                                    color = if (baseColorIndex == index) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { baseColorIndex = index }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Particle Speed: ${String.format("%.1f", particleSpeed)}x", fontSize = 12.sp)
                Slider(
                    value = particleSpeed,
                    onValueChange = { particleSpeed = it },
                    valueRange = 0.5f..3.0f,
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                )

                Text("Space Node Size: ${String.format("%.1f", particleSize)}dp", fontSize = 12.sp)
                Slider(
                    value = particleSize,
                    onValueChange = { particleSize = it },
                    valueRange = 2f..12f,
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                try {
                    val intent = android.content.Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                        putExtra(
                            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                            android.content.ComponentName(context, "com.example.InteractiveLiveWallpaperService")
                        )
                    }
                    context.startActivity(intent)
                    Toast.makeText(context, "Set Infinity Space Live as active Wallpaper!", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Opening Live Wallpaper settings...", Toast.LENGTH_LONG).show()
                    try {
                        val intentFallback = android.content.Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                        context.startActivity(intentFallback)
                    } catch (ex: Exception) {
                        Toast.makeText(context, "Live wallpaper selector not supported on this emulator configuration.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("apply_live_button"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Filled.Wallpaper, contentDescription = "Wallpaper")
            Spacer(modifier = Modifier.width(8.dp))
            Text("SET LIVE SYSTEM WALLPAPER", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

// ==========================================
// 4. DAILY AUTO-WALLPAPER TAB
// ==========================================
@Composable
fun DailyWallpaperTab(viewModel: WallpaperViewModel) {
    val context = LocalContext.current
    val dailySettings by viewModel.dailySettings.collectAsStateWithLifecycle()
    val categories = viewModel.categories

    var selectedCategoryIdForDaily by remember { mutableStateOf(categories.firstOrNull()?.id ?: "landscapes") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Daily Cycle Surprise",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Wake up to a brand new stunning wallpaper every single day from your selected category automatically.",
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (dailySettings?.isEnabled == true)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (dailySettings?.isEnabled == true) Icons.Filled.Verified else Icons.Filled.HourglassEmpty,
                    contentDescription = "Status icon",
                    tint = if (dailySettings?.isEnabled == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (dailySettings?.isEnabled == true) "DAILY CYCLE IS ACTIVE" else "DAILY CYCLE IS DISABLED",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (dailySettings?.isEnabled == true) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (dailySettings?.isEnabled == true) {
                    val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
                    val formattedDate = sdf.format(Date(dailySettings!!.lastUpdated))
                    Text(
                        text = "Current Category: ${dailySettings!!.categoryId.replaceFirstChar { it.uppercase() }}\nLast update triggered: $formattedDate",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    Text(
                        text = "Choose your favorite category below and active the daily wallpaper cycle.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Select Cycle Category",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { category ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedCategoryIdForDaily == category.id)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                        .border(
                            width = if (selectedCategoryIdForDaily == category.id) 2.dp else 1.dp,
                            color = if (selectedCategoryIdForDaily == category.id) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedCategoryIdForDaily = category.id }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = category.coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            category.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "${category.wallpapers.size} available cycle photos",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    RadioButton(
                        selected = selectedCategoryIdForDaily == category.id,
                        onClick = { selectedCategoryIdForDaily = category.id }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (dailySettings?.isEnabled == true) {
                OutlinedButton(
                    onClick = { viewModel.disableDailyWallpaper() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("disable_daily_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Deactivate Cycle")
                }
            }

            Button(
                onClick = { viewModel.enableDailyWallpaper(selectedCategoryIdForDaily) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("enable_daily_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Activate Cycle", fontWeight = FontWeight.Bold)
            }
        }

        if (dailySettings?.isEnabled == true) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { viewModel.triggerDailyWallpaperCycleNow(context, dailySettings!!.categoryId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("trigger_daily_now_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Filled.Sync, contentDescription = "Sync")
                Spacer(modifier = Modifier.width(8.dp))
                Text("FORCE CYCLE UPDATE NOW")
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

// ==========================================
// 5. PALETTE LAB TAB
// ==========================================
@Composable
fun PaletteLabTab(viewModel: WallpaperViewModel) {
    val context = LocalContext.current
    val customColor by viewModel.customColor.collectAsStateWithLifecycle()

    var showApplyDialog by remember { mutableStateOf(false) }

    val presetColors = listOf(
        Pair("Midnight Blue", Color(0xFF1E293B)),
        Pair("Pure Obsidian", Color(0xFF090D16)),
        Pair("Emerald Forest", Color(0xFF064E3B)),
        Pair("Crimson Velvet", Color(0xFF7F1D1D)),
        Pair("Terracotta Sun", Color(0xFFC2410C)),
        Pair("Lavender Mist", Color(0xFFDDD6FE)),
        Pair("Sage Breeze", Color(0xFFD1FAE5)),
        Pair("Solar Gold", Color(0xFFFEF08A))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Palette Lab",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Generate programmatically perfect, flat Material solid backgrounds. Eye-friendly and clean.",
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = customColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val hexString = String.format("#%06X", 0xFFFFFF and customColor.toArgb())
                    Text(
                        text = hexString,
                        color = if (customColor.red + customColor.green + customColor.blue > 1.5f) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "LIVE RENDER CANVAS",
                        color = if (customColor.red + customColor.green + customColor.blue > 1.5f) Color.Black.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Material Presets",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(presetColors) { preset ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(preset.second)
                        .border(
                            width = if (customColor == preset.second) 3.dp else 1.dp,
                            color = if (customColor == preset.second) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.updateCustomColor(preset.second) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "RGB Fine-Tuning",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Red: ${(customColor.red * 255).toInt()}", fontSize = 12.sp)
                Slider(
                    value = customColor.red,
                    onValueChange = { viewModel.updateCustomColor(Color(it, customColor.green, customColor.blue)) },
                    colors = SliderDefaults.colors(thumbColor = Color.Red, activeTrackColor = Color.Red.copy(alpha = 0.5f))
                )

                Text("Green: ${(customColor.green * 255).toInt()}", fontSize = 12.sp)
                Slider(
                    value = customColor.green,
                    onValueChange = { viewModel.updateCustomColor(Color(customColor.red, it, customColor.blue)) },
                    colors = SliderDefaults.colors(thumbColor = Color.Green, activeTrackColor = Color.Green.copy(alpha = 0.5f))
                )

                Text("Blue: ${(customColor.blue * 255).toInt()}", fontSize = 12.sp)
                Slider(
                    value = customColor.blue,
                    onValueChange = { viewModel.updateCustomColor(Color(customColor.red, customColor.green, it)) },
                    colors = SliderDefaults.colors(thumbColor = Color.Blue, activeTrackColor = Color.Blue.copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showApplyDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("apply_solid_button"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Filled.Check, contentDescription = "Apply")
            Spacer(modifier = Modifier.width(8.dp))
            Text("APPLY TO PHONE BACKGROUND", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(30.dp))
    }

    if (showApplyDialog) {
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            title = { Text("Solid Color Destination") },
            text = { Text("Choose where you want to set this elegant solid backdrop.") },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.applySolidColorWallpaper(context, customColor, WallpaperType.HOME)
                            showApplyDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().testTag("apply_solid_home")
                    ) { Text("Set on Home Screen") }

                    Button(
                        onClick = {
                            viewModel.applySolidColorWallpaper(context, customColor, WallpaperType.LOCK)
                            showApplyDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().testTag("apply_solid_lock")
                    ) { Text("Set on Lock Screen") }

                    Button(
                        onClick = {
                            viewModel.applySolidColorWallpaper(context, customColor, WallpaperType.BOTH)
                            showApplyDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().testTag("apply_solid_both")
                    ) { Text("Set on Both Screens") }

                    TextButton(
                        onClick = { showApplyDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) { Text("Cancel") }
                }
            }
        )
    }
}

// ==========================================
// 6. FAVORITES OFFLINE TAB
// ==========================================
@Composable
fun FavoritesTab(viewModel: WallpaperViewModel) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Empty",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No saved favorites yet",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Browse wallpapers and tap the heart icon to save offline favorites here.",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "My Curated Favorites (${favorites.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favorites) { favorite ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.65f)
                            .clickable {
                                viewModel.selectWallpaper(
                                    Wallpaper(
                                        id = favorite.id,
                                        url = favorite.url,
                                        thumbnailUrl = favorite.thumbnailUrl,
                                        title = favorite.title,
                                        author = favorite.author,
                                        categoryId = favorite.categoryId
                                    )
                                )
                            },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = favorite.thumbnailUrl,
                                contentDescription = favorite.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                                        )
                                    )
                            )
                            Text(
                                text = favorite.title,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. IMMERSIVE WALLPAPER PREVIEW SCREEN
// ==========================================
@Composable
fun WallpaperPreviewScreen(
    wallpaper: Wallpaper,
    viewModel: WallpaperViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var previewMode by remember { mutableStateOf("home") } // lock, home, pure
    var showDialog by remember { mutableStateOf(false) }
    val isFavorited by viewModel.isCurrentFavorited(wallpaper.id).collectAsStateWithLifecycle(initialValue = false)

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = wallpaper.url,
            contentDescription = wallpaper.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        when (previewMode) {
            "lock" -> LockScreenOverlay()
            "home" -> HomeScreenOverlay()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .testTag("preview_back_button")
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f))
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(if (previewMode == "lock") MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { previewMode = "lock" }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Lock Screen",
                            color = if (previewMode == "lock") Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(if (previewMode == "home") MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { previewMode = "home" }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Home Screen",
                            color = if (previewMode == "home") Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(if (previewMode == "pure") MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { previewMode = "pure" }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Pure View",
                            color = if (previewMode == "pure") Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.75f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = wallpaper.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Shot by ${wallpaper.author}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleFavorite(wallpaper) },
                        modifier = Modifier.testTag("preview_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorited) Color.Red else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("set_wallpaper_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Filled.Wallpaper, contentDescription = "Apply")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SET WALLPAPER NOW", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Set Wallpaper Destination") },
            text = { Text("Choose where you want to set this stunning photo.") },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (wallpaper.id == "local_picked") {
                                viewModel.applyLocalPhotoWallpaper(context, Uri.parse(wallpaper.url), WallpaperType.HOME)
                            } else {
                                viewModel.applyWallpaper(context, wallpaper.url, WallpaperType.HOME)
                            }
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().testTag("apply_home")
                    ) { Text("Set on Home Screen") }

                    Button(
                        onClick = {
                            if (wallpaper.id == "local_picked") {
                                viewModel.applyLocalPhotoWallpaper(context, Uri.parse(wallpaper.url), WallpaperType.LOCK)
                            } else {
                                viewModel.applyWallpaper(context, wallpaper.url, WallpaperType.LOCK)
                            }
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().testTag("apply_lock")
                    ) { Text("Set on Lock Screen") }

                    Button(
                        onClick = {
                            if (wallpaper.id == "local_picked") {
                                viewModel.applyLocalPhotoWallpaper(context, Uri.parse(wallpaper.url), WallpaperType.BOTH)
                            } else {
                                viewModel.applyWallpaper(context, wallpaper.url, WallpaperType.BOTH)
                            }
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().testTag("apply_both")
                    ) { Text("Set on Both Screens") }

                    TextButton(
                        onClick = { showDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) { Text("Cancel") }
                }
            }
        )
    }
}

// ==========================================
// PREVIEW OVERLAYS
// ==========================================
@Composable
fun LockScreenOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val calendar = Calendar.getInstance()
            val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())

            Text(
                text = timeFormat.format(calendar.time),
                color = Color.White,
                fontSize = 86.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-2).sp
            )
            Text(
                text = dateFormat.format(calendar.time),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Message,
                            contentDescription = "Message",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Infinity Studio • Now",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "This wallpaper layout looks incredible!",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Lock",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Swipe up to unlock",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun HomeScreenOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.SignalCellular4Bar, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Filled.Wifi, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Filled.Battery5Bar, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Search or say Hey Google", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.Mic, contentDescription = "Mic", tint = Color.White)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 150.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MockIcon(Icons.Filled.Phone, "Phone", Color(0xFF10B981))
                MockIcon(Icons.Filled.Email, "Email", Color(0xFF3B82F6))
                MockIcon(Icons.Filled.CompassCalibration, "Browser", Color(0xFFF59E0B))
                MockIcon(Icons.Filled.Camera, "Camera", Color(0xFFEC4899))
            }
        }
    }
}

@Composable
fun MockIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(tint),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}
