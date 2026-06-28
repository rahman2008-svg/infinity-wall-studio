package com.example.data

import com.example.data.models.Wallpaper
import com.example.data.models.WallpaperCategory

object WallpaperData {

    // Curated wallpaper list combined with dynamically generated high-quality wallpaper sources
    val categories: List<WallpaperCategory> by lazy {
        listOf(
            WallpaperCategory(
                id = "landscapes",
                title = "Landscapes & Nature",
                description = "Majestic mountains, dense forests, golden deserts, and whispering seas.",
                coverUrl = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=600&q=80",
                wallpapers = getCuratedLandscapes() + generateWallpapersForCategory(
                    categoryId = "landscapes",
                    adjectives = landscapesAdj,
                    nouns = landscapesNoun,
                    baseIdIndex = 10,
                    count = 170
                )
            ),
            WallpaperCategory(
                id = "space",
                title = "Earth & Cosmos",
                description = "Unveil the mystery of distant stars, galactic dust, and deep celestial bodies.",
                coverUrl = "https://images.unsplash.com/photo-1506318137071-a8e063b4bec0?auto=format&fit=crop&w=600&q=80",
                wallpapers = getCuratedSpace() + generateWallpapersForCategory(
                    categoryId = "space",
                    adjectives = spaceAdj,
                    nouns = spaceNoun,
                    baseIdIndex = 181,
                    count = 170
                )
            ),
            WallpaperCategory(
                id = "textures",
                title = "Textures & Patterns",
                description = "Abstract water movements, intricate wood designs, sand waves, and micro-patterns.",
                coverUrl = "https://images.unsplash.com/photo-1541701494587-cb58502866ab?auto=format&fit=crop&w=600&q=80",
                wallpapers = getCuratedTextures() + generateWallpapersForCategory(
                    categoryId = "textures",
                    adjectives = texturesAdj,
                    nouns = texturesNoun,
                    baseIdIndex = 351,
                    count = 170
                )
            ),
            WallpaperCategory(
                id = "cityscapes",
                title = "Cityscapes & Urban",
                description = "The vibrant pulse of modern metropolises under shimmering neon nights.",
                coverUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=600&q=80",
                wallpapers = getCuratedCityscapes() + generateWallpapersForCategory(
                    categoryId = "cityscapes",
                    adjectives = cityscapesAdj,
                    nouns = cityscapesNoun,
                    baseIdIndex = 521,
                    count = 170
                )
            ),
            WallpaperCategory(
                id = "art",
                title = "Aesthetics & Art",
                description = "Paintings, rich illustrations, pastel gradients, and vaporwave dreams.",
                coverUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?auto=format&fit=crop&w=600&q=80",
                wallpapers = getCuratedArt() + generateWallpapersForCategory(
                    categoryId = "art",
                    adjectives = artAdj,
                    nouns = artNoun,
                    baseIdIndex = 691,
                    count = 170
                )
            ),
            WallpaperCategory(
                id = "minimalist",
                title = "Minimalist & Clean",
                description = "Subtle lighting, single elements, geometric tranquility, and distraction-free layouts.",
                coverUrl = "https://images.unsplash.com/photo-1485550409059-9afb054cada4?auto=format&fit=crop&w=600&q=80",
                wallpapers = getCuratedMinimalist() + generateWallpapersForCategory(
                    categoryId = "minimalist",
                    adjectives = minimalAdj,
                    nouns = minimalNoun,
                    baseIdIndex = 861,
                    count = 170
                )
            )
        )
    }

    private fun getCuratedLandscapes() = listOf(
        Wallpaper(
            id = "land_1",
            url = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=400&q=70",
            title = "Alpine Horizon",
            author = "Eberhard Grossgasteiger",
            categoryId = "landscapes"
        ),
        Wallpaper(
            id = "land_2",
            url = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=400&q=70",
            title = "Autumn Whispers",
            author = "Jay Mantri",
            categoryId = "landscapes"
        ),
        Wallpaper(
            id = "land_3",
            url = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=400&q=70",
            title = "Malibu Sunset",
            author = "Sean Oulashin",
            categoryId = "landscapes"
        ),
        Wallpaper(
            id = "land_4",
            url = "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=400&q=70",
            title = "Sahara Silence",
            author = "Keith Hardy",
            categoryId = "landscapes"
        ),
        Wallpaper(
            id = "land_5",
            url = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=70",
            title = "Yosemite Majesty",
            author = "Ansel Adams Tribute",
            categoryId = "landscapes"
        ),
        Wallpaper(
            id = "land_6",
            url = "https://images.unsplash.com/photo-1433832597046-4f10e10ac764?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1433832597046-4f10e10ac764?auto=format&fit=crop&w=400&q=70",
            title = "Misty Peak",
            author = "Hendrik Cornelissen",
            categoryId = "landscapes"
        )
    )

    private fun getCuratedSpace() = listOf(
        Wallpaper(
            id = "space_1",
            url = "https://images.unsplash.com/photo-1506318137071-a8e063b4bec0?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1506318137071-a8e063b4bec0?auto=format&fit=crop&w=400&q=70",
            title = "Orion Nebula",
            author = "NASA Hub",
            categoryId = "space"
        ),
        Wallpaper(
            id = "space_2",
            url = "https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=400&q=70",
            title = "Milky Way Peak",
            author = "Benjamin Voros",
            categoryId = "space"
        ),
        Wallpaper(
            id = "space_3",
            url = "https://images.unsplash.com/photo-1614730321146-b6fa6a46bcb4?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1614730321146-b6fa6a46bcb4?auto=format&fit=crop&w=400&q=70",
            title = "Blue Marble Horizon",
            author = "NASA Earth Observatory",
            categoryId = "space"
        ),
        Wallpaper(
            id = "space_4",
            url = "https://images.unsplash.com/photo-1483347756197-71ef80e95f73?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1483347756197-71ef80e95f73?auto=format&fit=crop&w=400&q=70",
            title = "Aurora Borealis",
            author = "Vincent Ledvina",
            categoryId = "space"
        ),
        Wallpaper(
            id = "space_5",
            url = "https://images.unsplash.com/photo-1538370965046-79c0d6907d47?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1538370965046-79c0d6907d47?auto=format&fit=crop&w=400&q=70",
            title = "Stardust Veil",
            author = "John Fowler",
            categoryId = "space"
        )
    )

    private fun getCuratedTextures() = listOf(
        Wallpaper(
            id = "text_1",
            url = "https://images.unsplash.com/photo-1541701494587-cb58502866ab?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1541701494587-cb58502866ab?auto=format&fit=crop&w=400&q=70",
            title = "Fluid Motion Ripple",
            author = "Joel Filipe",
            categoryId = "textures"
        ),
        Wallpaper(
            id = "text_2",
            url = "https://images.unsplash.com/photo-1533090161767-e6ffed986c88?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1533090161767-e6ffed986c88?auto=format&fit=crop&w=400&q=70",
            title = "Rose Quartz Marble",
            author = "Fanny Gustafsson",
            categoryId = "textures"
        ),
        Wallpaper(
            id = "text_3",
            url = "https://images.unsplash.com/photo-1533158326339-7f3cf2404354?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1533158326339-7f3cf2404354?auto=format&fit=crop&w=400&q=70",
            title = "Golden Dunes Lines",
            author = "Karim Sakhibgareev",
            categoryId = "textures"
        ),
        Wallpaper(
            id = "text_4",
            url = "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?auto=format&fit=crop&w=400&q=70",
            title = "Symmetrical Palm Leaf",
            author = "Ren Ran",
            categoryId = "textures"
        )
    )

    private fun getCuratedCityscapes() = listOf(
        Wallpaper(
            id = "city_1",
            url = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=400&q=70",
            title = "Tokyo Neon Dreams",
            author = "Jezael Melgoza",
            categoryId = "cityscapes"
        ),
        Wallpaper(
            id = "city_2",
            url = "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?auto=format&fit=crop&w=400&q=70",
            title = "Manhattan Lights",
            author = "Michael Shainblum",
            categoryId = "cityscapes"
        ),
        Wallpaper(
            id = "city_3",
            url = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=400&q=70",
            title = "Eiffel Glow",
            author = "Chris Karidis",
            categoryId = "cityscapes"
        ),
        Wallpaper(
            id = "city_4",
            url = "https://images.unsplash.com/photo-1519501025264-65ba15a82390?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1519501025264-65ba15a82390?auto=format&fit=crop&w=400&q=70",
            title = "Shibuya Crossing",
            author = "Ryosuke Yagi",
            categoryId = "cityscapes"
        )
    )

    private fun getCuratedArt() = listOf(
        Wallpaper(
            id = "art_1",
            url = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?auto=format&fit=crop&w=400&q=70",
            title = "Surreal Flora",
            author = "Suhreon Kim",
            categoryId = "art"
        ),
        Wallpaper(
            id = "art_2",
            url = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=400&q=70",
            title = "Vaporwave Sunset",
            author = "Simeon Muller",
            categoryId = "art"
        ),
        Wallpaper(
            id = "art_3",
            url = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?auto=format&fit=crop&w=400&q=70",
            title = "Ethereal Dreamscape",
            author = "Mo",
            categoryId = "art"
        )
    )

    private fun getCuratedMinimalist() = listOf(
        Wallpaper(
            id = "min_1",
            url = "https://images.unsplash.com/photo-1485550409059-9afb054cada4?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1485550409059-9afb054cada4?auto=format&fit=crop&w=400&q=70",
            title = "Isolation Leaf",
            author = "Sarah Dorweiler",
            categoryId = "minimalist"
        ),
        Wallpaper(
            id = "min_2",
            url = "https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=400&q=70",
            title = "Minimal Slate Room",
            author = "Lachlan Gowen",
            categoryId = "minimalist"
        ),
        Wallpaper(
            id = "min_3",
            url = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1600&q=90",
            thumbnailUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=400&q=70",
            title = "Pure Architectural Curve",
            author = "Jorik Kleen",
            categoryId = "minimalist"
        )
    )

    // Generator logic
    private fun generateWallpapersForCategory(
        categoryId: String,
        adjectives: List<String>,
        nouns: List<String>,
        baseIdIndex: Int,
        count: Int
    ): List<Wallpaper> {
        val list = mutableListOf<Wallpaper>()
        for (i in 1..count) {
            val adjIndex = (i * 17 + baseIdIndex * 3) % adjectives.size
            val nounIndex = (i * 23 + baseIdIndex * 7) % nouns.size
            val authorIndex = (i * 31 + baseIdIndex * 5) % popularAuthors.size
            
            val title = "${adjectives[adjIndex]} ${nouns[nounIndex]}"
            val author = popularAuthors[authorIndex]
            
            // Unique deterministic seed string per category and index
            val seed = "${categoryId}_${i}"
            
            list.add(
                Wallpaper(
                    id = "gen_${categoryId}_$i",
                    url = "https://picsum.photos/seed/$seed/1080/1920",
                    thumbnailUrl = "https://picsum.photos/seed/$seed/400/600",
                    title = title,
                    author = author,
                    categoryId = categoryId
                )
            )
        }
        return list
    }

    // Name generation dictionaries
    private val landscapesAdj = listOf("Epic", "Majestic", "Serene", "Silent", "Savage", "Golden", "Alpine", "Nordic", "Ethereal", "Misty", "Vast", "Wild", "Emerald", "Frozen", "Tranquil", "Crystalline", "Primal", "Sunny", "Crimson", "Azure", "Luminous", "Whispering")
    private val landscapesNoun = listOf("Valley", "Peak", "Lake", "Meadow", "Ridge", "Coast", "Canyon", "Forest", "Dune", "River", "Cliff", "Glacier", "Waterfall", "Tundra", "Plains", "Horizon", "Fjord", "Estuary", "Basin", "Highlands", "Summit")

    private val spaceAdj = listOf("Cosmic", "Nebular", "Galactic", "Celestial", "Interstellar", "Stellar", "Astral", "Abyssal", "Infinite", "Solar", "Supernova", "Eclipsed", "Orion", "Void", "Aurora", "Quantum", "Spectral", "Nova", "Andromeda", "Nebulous", "Eternal")
    private val spaceNoun = listOf("Nebula", "Galaxy", "Cluster", "Horizon", "Supernova", "Stardust", "Pulsar", "Quasar", "Comet", "Orbit", "Exoplanet", "Constellation", "Void", "Wormhole", "Atmosphere", "Eclipse", "Corona", "Singularity", "Supercluster")

    private val texturesAdj = listOf("Fluid", "Organic", "Geometric", "Abstract", "Symmetrical", "Granular", "Marbled", "Tactile", "Cracked", "Smooth", "Fibrous", "Metallic", "Woven", "Crystalline", "Vaporous", "Pleated", "Eroded", "Polished", "Laminated", "Velvety")
    private val texturesNoun = listOf("Pattern", "Ripple", "Surface", "Marble", "Grain", "Mesh", "Grid", "Wave", "Foil", "Fabric", "Substance", "Structure", "Vein", "Layer", "Relief", "Matrix", "Fiber", "Shell", "Slab", "Canvas")

    private val cityscapesAdj = listOf("Metropolitan", "Urban", "Neon", "Cyberpunk", "Tokyo", "Manhattan", "Futuristic", "Industrial", "Shimmering", "Twilight", "Nocturnal", "Monolithic", "Skyline", "Vibrant", "Subterranean", "Retro", "Echoing", "Sleek", "Empire", "Gotham")
    private val cityscapesNoun = listOf("Skyline", "Boulevard", "Skyscraper", "Avenue", "Tower", "Bridge", "Intersection", "Plaza", "Subway", "Alley", "District", "Megacity", "Dock", "Highway", "Station", "Viaduct", "Terminal", "Spire", "Facade")

    private val artAdj = listOf("Surreal", "Impressionist", "Abstract", "Vaporwave", "Minimal", "Expressionist", "Cubist", "Psychedelic", "Ethereal", "Baroque", "Gothic", "Neo-Classic", "Dreamlike", "Whimsical", "Retro-Futuristic", "Luminous", "Textured", "Fantasist", "Renaissance")
    private val artNoun = listOf("Dreamscape", "Canvas", "Portrait", "Sculpture", "Mural", "Concept", "Vision", "Illusion", "Composition", "Sketch", "Synthesis", "Harmony", "Symphony", "Figurative", "Collage", "Fresco", "Expression", "Ode", "Saga")

    private val minimalAdj = listOf("Clean", "Minimal", "Pure", "Zen", "Silent", "Stark", "Bare", "Tranquil", "Sleek", "Empty", "Monochrome", "Geometric", "Subtle", "Isolate", "Balanced", "Uniform", "Soft", "Raw", "Symmetric", "Subdued")
    private val minimalNoun = listOf("Space", "Line", "Form", "Shadow", "Object", "Focus", "Concept", "Contrast", "Void", "Tranquility", "Element", "Boundary", "Sphere", "Plane", "Intersection", "Accent", "Origin", "Horizon", "Aesthetic")

    private val popularAuthors = listOf(
        "Prince AR Abdur Rahman", "NexVora Lab's Ofc", "Alex Mercer", "Sofia Chen", "Elias Lindqvist", "Marcus Aurelius", "Nisha Patel", "Liam Henderson", 
        "Yuki Sato", "Amara Okafor", "Dmitri Volkov", "Elena Rostova", "Lucas Dubois", "Chloe Lefebvre", "Gabriel Garcia", 
        "Isabella Santos", "Mateo Silva", "Hans Meier", "Emma Watson", "Oliver Taylor", "Zoe Jenkins", "Ryan Reynolds",
        "Jezael Melgoza", "Eberhard Grossgasteiger", "Benjamin Voros", "Ren Ran", "Sarah Dorweiler", "Joel Filipe"
    )

    fun getWallpaperById(id: String): Wallpaper? {
        return categories.flatMap { it.wallpapers }.find { it.id == id }
    }

    fun getRandomWallpaperForCategory(categoryId: String): Wallpaper? {
        val wallpapers = categories.find { it.id == categoryId }?.wallpapers ?: emptyList()
        return if (wallpapers.isNotEmpty()) wallpapers.random() else null
    }
}
