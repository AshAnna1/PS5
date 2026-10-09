package com.example.model

import androidx.annotation.DrawableRes
import kotlinx.serialization.Serializable

enum class GamePlatform {
    PS5,
    PS4,
    HOMEBREW
}

enum class GameFormat {
    FOLDER,
    ZARCHIVE,
    ELF
}

enum class CompatibilityGrade(val label: String) {
    PLAYABLE("Playable"),
    IN_GAME("In-Game"),
    INTRO("Intro"),
    LOADABLE("Loadable"),
    NOTHING("Nothing")
}

enum class Resolution(val display: String, val width: Int, val height: Int) {
    RES_720P("1280x720 (HD)", 1280, 720),
    RES_1080P("1920x1080 (FHD)", 1920, 1080),
    RES_1440P("2560x1440 (2K)", 2560, 1440),
    RES_4K("3840x2160 (4K UHD)", 3840, 2160),
    RES_DYNAMIC("Dynamic Native", 0, 0)
}

enum class PresentMode(val display: String) {
    FIFO("FIFO (V-Sync)"),
    MAILBOX("Mailbox (Lowest Latency)"),
    IMMEDIATE("Immediate (No Sync)")
}

enum class ShaderOptType(val display: String) {
    PERFORMANCE("Performance"),
    SIZE("Size"),
    NONE("None")
}

@Serializable
data class EmulatorConfig(
    val resolution: String = Resolution.RES_1080P.name,
    val presentMode: String = PresentMode.MAILBOX.name,
    val vblankFrequency: Int = 60,
    val fullscreen: Boolean = true,
    val tessellation: Boolean = true,
    val readbackLinearImages: Boolean = true,
    val syncRawImageBuffers: Boolean = false,
    val vulkanValidation: Boolean = false,
    val shaderValidation: Boolean = false,
    val shaderOptimization: String = ShaderOptType.PERFORMANCE.name,
    val skipNoticeScreen: Boolean = true,
    val trophyNotifications: Boolean = true,
    val amdCpuEmulation: Boolean = true,
    val redZoneProtection: Boolean = true,
    val profilerEnabled: Boolean = false,
    val controllerColor: String = "#006FCD",
    val vibrationIntensity: Float = 0.8f,
    val speakerVolume: Float = 0.7f,
    val touchControlsEnabled: Boolean = true,
    val touchControlsOpacity: Float = 0.75f,
    val userName: String = "KytyPlayer",
    val userId: Int = 100
)

data class GameItem(
    val id: String,
    val title: String,
    val publisher: String,
    val version: String,
    val sizeGb: Double,
    val platform: GamePlatform,
    val format: GameFormat,
    val engine: String,
    val compatibilityGrade: CompatibilityGrade,
    @DrawableRes val coverRes: Int? = null,
    val bannerRes: Int? = null,
    val path: String,
    val lastPlayed: Long = 0L,
    val playTimeMinutes: Int = 0,
    val isFavorite: Boolean = false,
    val trophiesEarned: Int = 0,
    val trophiesTotal: Int = 0,
    val config: EmulatorConfig = EmulatorConfig()
)

enum class TrophyType(val pointValue: Int) {
    PLATINUM(300),
    GOLD(90),
    SILVER(30),
    BRONZE(15)
}

data class TrophyItem(
    val id: Int,
    val gameId: String,
    val title: String,
    val description: String,
    val type: TrophyType,
    val isHidden: Boolean = false,
    val isEarned: Boolean = false,
    val earnedDate: String? = null,
    val rarityPercentage: Float = 12.5f
)

enum class PatchType {
    FPS_UNLOCK,
    GRAPHICS_TWEAK,
    RESOLUTION_SCALE,
    CHEAT_CODE
}

data class GamePatch(
    val id: String,
    val gameId: String,
    val title: String,
    val author: String,
    val description: String,
    val isEnabled: Boolean = false,
    val type: PatchType,
    val codeHex: String
)

data class CompatibilityEntry(
    val titleId: String,
    val title: String,
    val platform: GamePlatform,
    val grade: CompatibilityGrade,
    val testedVersion: String,
    val testedGpu: String,
    val fpsPerformance: String,
    val notes: String,
    val lastUpdated: String,
    val githubIssueId: Int = 0
)
