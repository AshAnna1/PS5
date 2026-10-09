package com.example.data

import com.example.R
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameRepository {

    private val initialGames = listOf(
        GameItem(
            id = "PPSA02188",
            title = "Astro Bot",
            publisher = "Sony Interactive Entertainment / Team ASOBI",
            version = "1.004.000",
            sizeGb = 66.4,
            platform = GamePlatform.PS5,
            format = GameFormat.FOLDER,
            engine = "Custom Prospero / RDNA2",
            compatibilityGrade = CompatibilityGrade.PLAYABLE,
            coverRes = R.drawable.game_astro_bot,
            path = "/storage/emulated/0/KytyPS5/Games/PPSA02188",
            lastPlayed = System.currentTimeMillis() - 86400000L,
            playTimeMinutes = 142,
            isFavorite = true,
            trophiesEarned = 14,
            trophiesTotal = 27
        ),
        GameItem(
            id = "PPSA01342",
            title = "Demon's Souls",
            publisher = "Bluepoint Games / SIE",
            version = "1.003.001",
            sizeGb = 52.8,
            platform = GamePlatform.PS5,
            format = GameFormat.ZARCHIVE,
            engine = "Bluepoint Engine / Vulkan",
            compatibilityGrade = CompatibilityGrade.IN_GAME,
            coverRes = R.drawable.game_demons_souls,
            path = "/storage/emulated/0/KytyPS5/Games/DemonsSouls.zar",
            lastPlayed = System.currentTimeMillis() - 172800000L,
            playTimeMinutes = 88,
            isFavorite = true,
            trophiesEarned = 6,
            trophiesTotal = 37
        ),
        GameItem(
            id = "CUSA08912",
            title = "Dreaming Sarah",
            publisher = "Asteristic Game Studio / Ratalaika",
            version = "1.00",
            sizeGb = 1.2,
            platform = GamePlatform.PS4,
            format = GameFormat.FOLDER,
            engine = "Unity 2D / Orbis",
            compatibilityGrade = CompatibilityGrade.PLAYABLE,
            coverRes = R.drawable.game_dreaming_sarah,
            path = "/storage/emulated/0/KytyPS5/Games/CUSA08912",
            lastPlayed = System.currentTimeMillis() - 345600000L,
            playTimeMinutes = 65,
            isFavorite = false,
            trophiesEarned = 16,
            trophiesTotal = 16
        ),
        GameItem(
            id = "PPSA01844",
            title = "Neptunia ReVerse",
            publisher = "Idea Factory / Compile Heart",
            version = "1.01",
            sizeGb = 18.5,
            platform = GamePlatform.PS5,
            format = GameFormat.FOLDER,
            engine = "Orochi 4 / RDNA2",
            compatibilityGrade = CompatibilityGrade.IN_GAME,
            coverRes = R.drawable.game_neptunia,
            path = "/storage/emulated/0/KytyPS5/Games/PPSA01844",
            lastPlayed = System.currentTimeMillis() - 500000000L,
            playTimeMinutes = 35,
            isFavorite = false,
            trophiesEarned = 5,
            trophiesTotal = 35
        ),
        GameItem(
            id = "PPSA13962",
            title = "SILENT HILL: The Short Message",
            publisher = "Konami Digital Entertainment / HexaDrive",
            version = "1.00",
            sizeGb = 12.3,
            platform = GamePlatform.PS5,
            format = GameFormat.ZARCHIVE,
            engine = "Unreal Engine 5.2",
            compatibilityGrade = CompatibilityGrade.IN_GAME,
            coverRes = R.drawable.game_silent_hill,
            path = "/storage/emulated/0/KytyPS5/Games/SilentHillSM.zar",
            lastPlayed = System.currentTimeMillis() - 600000000L,
            playTimeMinutes = 20,
            isFavorite = false,
            trophiesEarned = 3,
            trophiesTotal = 16
        ),
        GameItem(
            id = "PPSA09812",
            title = "EA Sports UFC 6",
            publisher = "Electronic Arts",
            version = "1.00",
            sizeGb = 48.0,
            platform = GamePlatform.PS5,
            format = GameFormat.FOLDER,
            engine = "Frostbite 4",
            compatibilityGrade = CompatibilityGrade.INTRO,
            coverRes = R.drawable.game_ufc6,
            path = "/storage/emulated/0/KytyPS5/Games/PPSA09812",
            lastPlayed = 0L,
            playTimeMinutes = 0,
            isFavorite = false,
            trophiesEarned = 0,
            trophiesTotal = 32
        ),
        GameItem(
            id = "PPSA00001",
            title = "Flappy Bird PS5 Homebrew",
            publisher = "Kyty Community Devs",
            version = "0.9b",
            sizeGb = 0.05,
            platform = GamePlatform.HOMEBREW,
            format = GameFormat.ELF,
            engine = "SDL3 + Vulkan 1.3 Direct",
            compatibilityGrade = CompatibilityGrade.PLAYABLE,
            coverRes = R.drawable.ic_launcher_fg_1791544557377,
            path = "/storage/emulated/0/KytyPS5/Homebrew/flappy_ps5.elf",
            lastPlayed = System.currentTimeMillis() - 10000000L,
            playTimeMinutes = 15,
            isFavorite = true,
            trophiesEarned = 4,
            trophiesTotal = 7
        ),
        GameItem(
            id = "PPSA00002",
            title = "2048 PS5 Edition",
            publisher = "Open Source Homebrew",
            version = "1.2.0",
            sizeGb = 0.08,
            platform = GamePlatform.HOMEBREW,
            format = GameFormat.ZARCHIVE,
            engine = "libAgc + Vulkan Compute",
            compatibilityGrade = CompatibilityGrade.PLAYABLE,
            coverRes = R.drawable.ic_launcher_fg_1791544557377,
            path = "/storage/emulated/0/KytyPS5/Homebrew/2048_ps5.zar",
            lastPlayed = 0L,
            playTimeMinutes = 0,
            isFavorite = false,
            trophiesEarned = 1,
            trophiesTotal = 5
        )
    )

    private val _games = MutableStateFlow(initialGames)
    val games: StateFlow<List<GameItem>> = _games.asStateFlow()

    fun getGame(id: String): GameItem? {
        return _games.value.find { it.id == id }
    }

    fun updateConfig(id: String, config: EmulatorConfig) {
        _games.update { list ->
            list.map { game ->
                if (game.id == id) game.copy(config = config) else game
            }
        }
    }

    fun toggleFavorite(id: String) {
        _games.update { list ->
            list.map { game ->
                if (game.id == id) game.copy(isFavorite = !game.isFavorite) else game
            }
        }
    }

    fun addGame(game: GameItem) {
        _games.update { list ->
            if (list.any { it.id == game.id }) list else list + game
        }
    }

    fun removeGame(id: String) {
        _games.update { list ->
            list.filterNot { it.id == id }
        }
    }

    fun recordPlaySession(id: String, additionalMinutes: Int) {
        _games.update { list ->
            list.map { game ->
                if (game.id == id) {
                    game.copy(
                        lastPlayed = System.currentTimeMillis(),
                        playTimeMinutes = game.playTimeMinutes + additionalMinutes
                    )
                } else game
            }
        }
    }

    fun incrementTrophyCount(id: String) {
        _games.update { list ->
            list.map { game ->
                if (game.id == id && game.trophiesEarned < game.trophiesTotal) {
                    game.copy(trophiesEarned = game.trophiesEarned + 1)
                } else game
            }
        }
    }
}
