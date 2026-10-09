package com.example.data

import com.example.model.CompatibilityEntry
import com.example.model.CompatibilityGrade
import com.example.model.GamePlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CompatibilityRepository {

    private val entries = listOf(
        CompatibilityEntry(
            titleId = "PPSA02188",
            title = "Astro Bot",
            platform = GamePlatform.PS5,
            grade = CompatibilityGrade.PLAYABLE,
            testedVersion = "KytyPS5 v0.3.5",
            testedGpu = "Vulkan 1.3 / Adreno 750 / Turnip",
            fpsPerformance = "58 - 60 FPS (V-Sync)",
            notes = "Full gameplay playable with flawless 3D rendering and DualSense haptics.",
            lastUpdated = "2026-10-04",
            githubIssueId = 842
        ),
        CompatibilityEntry(
            titleId = "CUSA08912",
            title = "Dreaming Sarah",
            platform = GamePlatform.PS4,
            grade = CompatibilityGrade.PLAYABLE,
            testedVersion = "KytyPS5 v0.3.4",
            testedGpu = "Vulkan 1.3 Generic",
            fpsPerformance = "60 FPS Constant",
            notes = "Completed from start to finish. Audio and video perfectly in sync.",
            lastUpdated = "2026-09-28",
            githubIssueId = 512
        ),
        CompatibilityEntry(
            titleId = "PPSA01844",
            title = "Neptunia ReVerse",
            platform = GamePlatform.PS5,
            grade = CompatibilityGrade.IN_GAME,
            testedVersion = "KytyPS5 v0.3.5",
            testedGpu = "Vulkan 1.3 / RDNA2 Recompiler",
            fpsPerformance = "45 - 55 FPS",
            notes = "3D dungeon exploration and battles working. Minor bloom post-processing glitch.",
            lastUpdated = "2026-10-02",
            githubIssueId = 791
        ),
        CompatibilityEntry(
            titleId = "PPSA13962",
            title = "SILENT HILL: The Short Message",
            platform = GamePlatform.PS5,
            grade = CompatibilityGrade.IN_GAME,
            testedVersion = "KytyPS5 v0.3.5",
            testedGpu = "Vulkan 1.3 with Mesh Shaders",
            fpsPerformance = "30 - 38 FPS",
            notes = "Boots intro cinematics and apartment exploration. Requires tessellation enabled.",
            lastUpdated = "2026-10-01",
            githubIssueId = 833
        ),
        CompatibilityEntry(
            titleId = "PPSA01342",
            title = "Demon's Souls",
            platform = GamePlatform.PS5,
            grade = CompatibilityGrade.IN_GAME,
            testedVersion = "KytyPS5 v0.3.5",
            testedGpu = "Vulkan 1.3 (Host Memory Tracked)",
            fpsPerformance = "30 - 45 FPS",
            notes = "Nexus and Boletaria playable. Dynamic shadows require high VRAM.",
            lastUpdated = "2026-10-06",
            githubIssueId = 855
        ),
        CompatibilityEntry(
            titleId = "PPSA09812",
            title = "EA Sports UFC 6",
            platform = GamePlatform.PS5,
            grade = CompatibilityGrade.INTRO,
            testedVersion = "KytyPS5 v0.3.3",
            testedGpu = "Vulkan 1.3",
            fpsPerformance = "30 FPS (Menus)",
            notes = "Renders EA intro video, main menu, and fighter select. Freezes on cage load.",
            lastUpdated = "2026-09-15",
            githubIssueId = 711
        ),
        CompatibilityEntry(
            titleId = "CUSA03173",
            title = "Bloodborne",
            platform = GamePlatform.PS4,
            grade = CompatibilityGrade.IN_GAME,
            testedVersion = "KytyPS5 v0.3.5",
            testedGpu = "Vulkan 1.3 (PM4 Pipeline)",
            fpsPerformance = "30 - 60 FPS (with patch)",
            notes = "Iosefka's Clinic and Central Yharnam playable with 60 FPS community patch.",
            lastUpdated = "2026-10-08",
            githubIssueId = 860
        ),
        CompatibilityEntry(
            titleId = "PPSA02225",
            title = "Ghost of Tsushima: Director's Cut",
            platform = GamePlatform.PS5,
            grade = CompatibilityGrade.INTRO,
            testedVersion = "KytyPS5 v0.3.2",
            testedGpu = "Vulkan 1.3",
            fpsPerformance = "40 FPS (Title)",
            notes = "Boots to title menu. Landscape geometry requires compute shader tessellation.",
            lastUpdated = "2026-08-30",
            githubIssueId = 640
        ),
        CompatibilityEntry(
            titleId = "PPSA01411",
            title = "Marvel's Spider-Man: Miles Morales",
            platform = GamePlatform.PS5,
            grade = CompatibilityGrade.IN_GAME,
            testedVersion = "KytyPS5 v0.3.5",
            testedGpu = "Vulkan 1.3 / SPIR-V Emitter",
            fpsPerformance = "35 - 50 FPS",
            notes = "Web-slinging in Harlem working. Ray-tracing disabled via config.",
            lastUpdated = "2026-10-07",
            githubIssueId = 859
        ),
        CompatibilityEntry(
            titleId = "PPSA05584",
            title = "Persona 5 Royal",
            platform = GamePlatform.PS5,
            grade = CompatibilityGrade.PLAYABLE,
            testedVersion = "KytyPS5 v0.3.5",
            testedGpu = "Vulkan 1.3",
            fpsPerformance = "60 FPS Locked",
            notes = "Fully playable. Audio decoders and video playback working with AJM/AVPlayer.",
            lastUpdated = "2026-09-29",
            githubIssueId = 804
        ),
        CompatibilityEntry(
            titleId = "PPSA07484",
            title = "Resident Evil 4 Remake",
            platform = GamePlatform.PS5,
            grade = CompatibilityGrade.IN_GAME,
            testedVersion = "KytyPS5 v0.3.4",
            testedGpu = "Vulkan 1.3 (RE Engine)",
            fpsPerformance = "30 - 45 FPS",
            notes = "Village chapter boots and is playable. Minor hair physics artifacts.",
            lastUpdated = "2026-10-03",
            githubIssueId = 820
        ),
        CompatibilityEntry(
            titleId = "PPSA03264",
            title = "Hades",
            platform = GamePlatform.PS5,
            grade = CompatibilityGrade.PLAYABLE,
            testedVersion = "KytyPS5 v0.3.5",
            testedGpu = "Vulkan 1.3",
            fpsPerformance = "60 FPS Locked",
            notes = "Flawless runs through Tartarus, Asphodel, and Elysium.",
            lastUpdated = "2026-10-05",
            githubIssueId = 848
        ),
        CompatibilityEntry(
            titleId = "PPSA01284",
            title = "Returnal",
            platform = GamePlatform.PS5,
            grade = CompatibilityGrade.LOADABLE,
            testedVersion = "KytyPS5 v0.3.2",
            testedGpu = "Vulkan 1.3",
            fpsPerformance = "N/A",
            notes = "ELF loads and maps memory, hangs on particle compute buffer allocation.",
            lastUpdated = "2026-08-19",
            githubIssueId = 590
        ),
        CompatibilityEntry(
            titleId = "PPSA08329",
            title = "God of War Ragnarök",
            platform = GamePlatform.PS5,
            grade = CompatibilityGrade.INTRO,
            testedVersion = "KytyPS5 v0.3.4",
            testedGpu = "Vulkan 1.3",
            fpsPerformance = "30 FPS",
            notes = "Plays Sony Santa Monica intro FMV and renders main menu sled.",
            lastUpdated = "2026-09-22",
            githubIssueId = 776
        ),
        CompatibilityEntry(
            titleId = "PPSA00001",
            title = "Flappy Bird PS5 Homebrew",
            platform = GamePlatform.HOMEBREW,
            grade = CompatibilityGrade.PLAYABLE,
            testedVersion = "KytyPS5 v0.3.5",
            testedGpu = "Vulkan 1.3 / GLES",
            fpsPerformance = "120 FPS",
            notes = "Runs at native high refresh rate. Touch controls fully supported.",
            lastUpdated = "2026-10-09",
            githubIssueId = 862
        )
    )

    private val _compatibilityList = MutableStateFlow(entries)
    val compatibilityList: StateFlow<List<CompatibilityEntry>> = _compatibilityList.asStateFlow()
}
