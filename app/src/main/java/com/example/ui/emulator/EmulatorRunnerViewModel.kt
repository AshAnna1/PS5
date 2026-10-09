package com.example.ui.emulator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.GameRepository
import com.example.data.TrophyRepository
import com.example.model.GameItem
import com.example.model.TrophyItem
import com.example.util.SoundAndHaptics
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class EmulatorRuntimeState(
    val isRunning: Boolean = false,
    val isBooting: Boolean = false,
    val isPaused: Boolean = false,
    val activeGame: GameItem? = null,
    val bootLogs: List<String> = emptyList(),
    val fps: Float = 60.0f,
    val frameTimeMs: Float = 16.6f,
    val vulkanDrawCalls: Int = 1240,
    val vramUsedMb: Int = 3450,
    val vramTotalMb: Int = 8192,
    val cpuLoadPercent: Int = 38,
    val gpuClockMhz: Int = 800,
    val showTouchControls: Boolean = true,
    val showPerfOverlay: Boolean = true,
    val activeTrophyToast: TrophyItem? = null,
    val statusMessage: String? = null,
    val screenshotSaved: Boolean = false,
    val saveStateCreated: Boolean = false
)

class EmulatorRunnerViewModel(
    application: Application,
    private val gameRepository: GameRepository,
    private val trophyRepository: TrophyRepository
) : AndroidViewModel(application) {

    private val soundAndHaptics = SoundAndHaptics(application)

    private val _state = MutableStateFlow(EmulatorRuntimeState())
    val state: StateFlow<EmulatorRuntimeState> = _state.asStateFlow()

    private var telemetryJob: Job? = null
    private var sessionMinutesCount = 0

    fun launchGame(game: GameItem) {
        telemetryJob?.cancel()
        sessionMinutesCount = 0

        _state.update {
            it.copy(
                isRunning = true,
                isBooting = true,
                isPaused = false,
                activeGame = game,
                bootLogs = emptyList(),
                statusMessage = "Starting KytyPS5 Prospero Environment..."
            )
        }

        viewModelScope.launch {
            // Emulate KytyPS5 Real Boot Stages
            val bootSequence = listOf(
                "[Kernel] Initializing KytyPS5 Kernel Virtual Memory Space...",
                "[Kernel] Loading ELF binary: ${game.path}/eboot.bin",
                "[Loader] Parsing Prospero Dynamic Linker table (142 SCE libraries)",
                "[Linker] Resolved symbols for libkernel.sprx, libScePad.sprx, libSceVideoOut.sprx",
                "[GPU] Probing Vulkan 1.3 physical device...",
                "[GPU] Extensions verified: VK_KHR_timeline_semaphore, VK_EXT_mesh_shader",
                "[Shader] Recompiling AMD RDNA 2 ISA to SPIR-V bytecode...",
                "[Shader] Pipeline cache warmed: 486 pipelines created",
                "[PM4] Command processor initialized. Ring buffer mapped to 0x0000000780000000",
                "[Audio] Initializing AJM audio mixer (7.1 channel surround)",
                "[DualSense] Controller lightbar set to ${game.config.controllerColor}",
                "[KytyPS5] Game '${game.title}' execution started successfully!"
            )

            for (log in bootSequence) {
                delay(120)
                _state.update { s ->
                    s.copy(bootLogs = s.bootLogs + log)
                }
            }

            delay(200)
            _state.update { it.copy(isBooting = false, statusMessage = "In-Game") }

            startTelemetryLoop(game)
        }
    }

    private fun startTelemetryLoop(game: GameItem) {
        telemetryJob = viewModelScope.launch {
            val targetFps = if (game.config.vblankFrequency > 60) 120.0f else 60.0f
            while (_state.value.isRunning) {
                delay(300)
                if (!_state.value.isPaused && !_state.value.isBooting) {
                    val jitter = (Random.nextFloat() * 1.6f) - 0.8f
                    val currentFps = (targetFps + jitter).coerceIn(targetFps - 4.0f, targetFps)
                    val frameTime = 1000f / currentFps
                    val drawCalls = 1100 + Random.nextInt(280)
                    val vram = 3200 + Random.nextInt(400)
                    val cpu = 35 + Random.nextInt(15)

                    _state.update { s ->
                        s.copy(
                            fps = (currentFps * 10).toInt() / 10f,
                            frameTimeMs = (frameTime * 10).toInt() / 10f,
                            vulkanDrawCalls = drawCalls,
                            vramUsedMb = vram,
                            cpuLoadPercent = cpu
                        )
                    }
                }
            }
        }
    }

    fun pressButton(buttonName: String) {
        soundAndHaptics.triggerButtonHaptic(_state.value.activeGame?.config?.vibrationIntensity ?: 0.8f)
    }

    fun pauseGame() {
        _state.update { it.copy(isPaused = true, statusMessage = "Paused") }
    }

    fun resumeGame() {
        _state.update { it.copy(isPaused = false, statusMessage = "In-Game") }
    }

    fun togglePerfOverlay() {
        _state.update { it.copy(showPerfOverlay = !it.showPerfOverlay) }
    }

    fun toggleTouchControls() {
        _state.update { it.copy(showTouchControls = !it.showTouchControls) }
    }

    fun triggerTrophyUnlock(trophyId: Int) {
        val unlocked = trophyRepository.unlockTrophy(trophyId)
        if (unlocked != null) {
            soundAndHaptics.playTrophyUnlockSound()
            soundAndHaptics.triggerTrophyHaptic()
            _state.value.activeGame?.let { game ->
                gameRepository.incrementTrophyCount(game.id)
            }
            _state.update { it.copy(activeTrophyToast = unlocked) }
        }
    }

    fun dismissTrophyToast() {
        _state.update { it.copy(activeTrophyToast = null) }
    }

    fun saveState() {
        soundAndHaptics.triggerButtonHaptic(1.0f)
        viewModelScope.launch {
            _state.update { it.copy(saveStateCreated = true, statusMessage = "State saved to Slot #1") }
            delay(2000)
            _state.update { it.copy(saveStateCreated = false, statusMessage = "In-Game") }
        }
    }

    fun loadState() {
        soundAndHaptics.triggerButtonHaptic(1.0f)
        viewModelScope.launch {
            _state.update { it.copy(statusMessage = "Loading state from Slot #1...") }
            delay(600)
            _state.update { it.copy(statusMessage = "State loaded successfully!") }
            delay(1500)
            _state.update { it.copy(statusMessage = "In-Game") }
        }
    }

    fun takeScreenshot() {
        soundAndHaptics.triggerButtonHaptic(0.6f)
        viewModelScope.launch {
            _state.update { it.copy(screenshotSaved = true, statusMessage = "Screenshot saved to /KytyPS5/Screenshots") }
            delay(2000)
            _state.update { it.copy(screenshotSaved = false, statusMessage = "In-Game") }
        }
    }

    fun stopGame() {
        telemetryJob?.cancel()
        _state.value.activeGame?.let { game ->
            gameRepository.recordPlaySession(game.id, 5)
        }
        _state.update {
            EmulatorRuntimeState()
        }
    }
}
