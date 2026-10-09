package com.example.data

import com.example.model.GamePatch
import com.example.model.PatchType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PatchesRepository {

    private val defaultPatches = listOf(
        GamePatch(
            id = "patch_01",
            gameId = "PPSA02188",
            title = "Unlock 120 FPS High Refresh Rate",
            author = "illusion",
            description = "Removes the default 60 FPS frame rate cap for devices supporting 120Hz display.",
            isEnabled = true,
            type = PatchType.FPS_UNLOCK,
            codeHex = "0x00A12480: 0x90 0x90 0x90 (NOP cap_check)"
        ),
        GamePatch(
            id = "patch_02",
            gameId = "PPSA02188",
            title = "Disable Dynamic Resolution Scaling",
            author = "KytyTeam",
            description = "Locks rendering target buffer strictly to 1080p/1440p native without downscaling.",
            isEnabled = false,
            type = PatchType.GRAPHICS_TWEAK,
            codeHex = "0x00F83100: 0x31 0xC0 0xC3 (xor eax, eax; ret)"
        ),
        GamePatch(
            id = "patch_03",
            gameId = "PPSA01342",
            title = "Demon's Souls 60 FPS Cinematic Mode",
            author = "illusion",
            description = "Enables full 60 FPS pacing when running in high-fidelity cinematic rendering mode.",
            isEnabled = true,
            type = PatchType.FPS_UNLOCK,
            codeHex = "0x0078A120: 0x01 0x00 0x00 0x00"
        ),
        GamePatch(
            id = "patch_04",
            gameId = "PPSA01342",
            title = "Infinite Stamina (Cheat)",
            author = "GoldHEN / Kyty",
            description = "Prevents stamina reduction during rolling and attacks.",
            isEnabled = false,
            type = PatchType.CHEAT_CODE,
            codeHex = "0x00418A30: 0x90 0x90 0x90 0x90 0x90"
        ),
        GamePatch(
            id = "patch_05",
            gameId = "CUSA08912",
            title = "Super Jump Velocity",
            author = "CheatCommunity",
            description = "Doubles Sarah's jump height for rapid platforming exploration.",
            isEnabled = false,
            type = PatchType.CHEAT_CODE,
            codeHex = "0x002B4104: 0x41 0x20 0x00 0x00"
        ),
        GamePatch(
            id = "patch_06",
            gameId = "PPSA13962",
            title = "Disable Film Grain & Chromatic Aberration",
            author = "KytyTeam",
            description = "Removes heavy noise filter for cleaner Vulkan rasterizer output.",
            isEnabled = true,
            type = PatchType.GRAPHICS_TWEAK,
            codeHex = "0x012F4B00: 0x00 0x00 0x00 0x00"
        ),
        GamePatch(
            id = "patch_07",
            gameId = "PPSA01844",
            title = "Unlock 60 FPS in Dungeons",
            author = "illusion",
            description = "Bypasses the 30 FPS battle locking timer.",
            isEnabled = true,
            type = PatchType.FPS_UNLOCK,
            codeHex = "0x0059C200: 0x40 0x00 0x00 0x00"
        )
    )

    private val _patches = MutableStateFlow(defaultPatches)
    val patches: StateFlow<List<GamePatch>> = _patches.asStateFlow()

    fun getPatchesForGame(gameId: String): List<GamePatch> {
        return _patches.value.filter { it.gameId == gameId }
    }

    fun togglePatch(patchId: String) {
        _patches.update { list ->
            list.map { patch ->
                if (patch.id == patchId) patch.copy(isEnabled = !patch.isEnabled) else patch
            }
        }
    }

    fun addPatch(patch: GamePatch) {
        _patches.update { it + patch }
    }
}
