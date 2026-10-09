package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.*
import com.example.ui.MainApp
import com.example.ui.emulator.EmulatorRunnerViewModel
import com.example.ui.theme.KytyPS5Theme
import com.example.ui.theme.PsBackgroundDark

class MainActivity : ComponentActivity() {

    private val gameRepository by lazy { GameRepository() }
    private val compatibilityRepository by lazy { CompatibilityRepository() }
    private val trophyRepository by lazy { TrophyRepository() }
    private val patchesRepository by lazy { PatchesRepository() }

    private val emulatorViewModel by lazy {
        EmulatorRunnerViewModel(
            application = application,
            gameRepository = gameRepository,
            trophyRepository = trophyRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KytyPS5Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PsBackgroundDark
                ) {
                    MainApp(
                        gameRepository = gameRepository,
                        compatibilityRepository = compatibilityRepository,
                        trophyRepository = trophyRepository,
                        patchesRepository = patchesRepository,
                        emulatorViewModel = emulatorViewModel
                    )
                }
            }
        }
    }
}
