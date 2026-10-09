package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.model.*
import com.example.ui.emulator.EmulatorActiveScreen
import com.example.ui.emulator.EmulatorRunnerViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

enum class AppNavDestination(val label: String) {
    LIBRARY("Games"),
    COMPATIBILITY("Compatibility"),
    PATCHES("Patches"),
    CONTROLLER("Controller"),
    SETTINGS("Settings")
}

@Composable
fun MainApp(
    gameRepository: GameRepository,
    compatibilityRepository: CompatibilityRepository,
    trophyRepository: TrophyRepository,
    patchesRepository: PatchesRepository,
    emulatorViewModel: EmulatorRunnerViewModel
) {
    val games by gameRepository.games.collectAsState()
    val compatibilityList by compatibilityRepository.compatibilityList.collectAsState()
    val trophies by trophyRepository.trophies.collectAsState()
    val patches by patchesRepository.patches.collectAsState()
    val emulatorState by emulatorViewModel.state.collectAsState()

    var currentTab by remember { mutableStateOf(AppNavDestination.LIBRARY) }
    var configuringGame by remember { mutableStateOf<GameItem?>(null) }
    var viewingTrophiesForGame by remember { mutableStateOf<GameItem?>(null) }

    // If emulator is active and running a game, display Emulator Active Screen
    if (emulatorState.isRunning) {
        EmulatorActiveScreen(
            viewModel = emulatorViewModel,
            onExit = {
                // Returns to launcher
            }
        )
        return
    }

    // Trophy Viewer Sub-Screen
    viewingTrophiesForGame?.let { game ->
        val gameTrophies = remember(trophies, game.id) {
            trophies.filter { it.gameId == game.id }
        }
        TrophyViewerScreen(
            game = game,
            trophies = gameTrophies,
            onUnlockTrophy = { trophyId ->
                emulatorViewModel.triggerTrophyUnlock(trophyId)
            },
            onBack = { viewingTrophiesForGame = null }
        )
        return
    }

    Scaffold(
        containerColor = PsBackgroundDark,
        bottomBar = {
            NavigationBar(
                containerColor = PsSurfaceDark,
                contentColor = Color.White,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = currentTab == AppNavDestination.LIBRARY,
                    onClick = { currentTab = AppNavDestination.LIBRARY },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppNavDestination.LIBRARY) Icons.Filled.SportsEsports else Icons.Outlined.SportsEsports,
                            contentDescription = "Games"
                        )
                    },
                    label = { Text("Games") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = PsAccentCyan,
                        indicatorColor = PsBluePrimary,
                        unselectedIconColor = PsTextSecondary,
                        unselectedTextColor = PsTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_library")
                )

                NavigationBarItem(
                    selected = currentTab == AppNavDestination.COMPATIBILITY,
                    onClick = { currentTab = AppNavDestination.COMPATIBILITY },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppNavDestination.COMPATIBILITY) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = "Compatibility"
                        )
                    },
                    label = { Text("Compat") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = PsAccentCyan,
                        indicatorColor = PsBluePrimary,
                        unselectedIconColor = PsTextSecondary,
                        unselectedTextColor = PsTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_compat")
                )

                NavigationBarItem(
                    selected = currentTab == AppNavDestination.PATCHES,
                    onClick = { currentTab = AppNavDestination.PATCHES },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppNavDestination.PATCHES) Icons.Filled.Extension else Icons.Outlined.Extension,
                            contentDescription = "Patches"
                        )
                    },
                    label = { Text("Patches") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = PsAccentCyan,
                        indicatorColor = PsBluePrimary,
                        unselectedIconColor = PsTextSecondary,
                        unselectedTextColor = PsTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_patches")
                )

                NavigationBarItem(
                    selected = currentTab == AppNavDestination.CONTROLLER,
                    onClick = { currentTab = AppNavDestination.CONTROLLER },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppNavDestination.CONTROLLER) Icons.Filled.Gamepad else Icons.Outlined.Gamepad,
                            contentDescription = "DualSense"
                        )
                    },
                    label = { Text("Input") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = PsAccentCyan,
                        indicatorColor = PsBluePrimary,
                        unselectedIconColor = PsTextSecondary,
                        unselectedTextColor = PsTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_controller")
                )

                NavigationBarItem(
                    selected = currentTab == AppNavDestination.SETTINGS,
                    onClick = { currentTab = AppNavDestination.SETTINGS },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppNavDestination.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = PsAccentCyan,
                        indicatorColor = PsBluePrimary,
                        unselectedIconColor = PsTextSecondary,
                        unselectedTextColor = PsTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tab_fade") { tab ->
                when (tab) {
                    AppNavDestination.LIBRARY -> {
                        GameLibraryScreen(
                            games = games,
                            onLaunchGame = { game ->
                                emulatorViewModel.launchGame(game)
                            },
                            onOpenGameConfig = { game ->
                                configuringGame = game
                            },
                            onOpenTrophies = { game ->
                                viewingTrophiesForGame = game
                            },
                            onToggleFavorite = { id ->
                                gameRepository.toggleFavorite(id)
                            },
                            onAddGame = { newGame ->
                                gameRepository.addGame(newGame)
                            }
                        )
                    }

                    AppNavDestination.COMPATIBILITY -> {
                        CompatibilityScreen(entries = compatibilityList)
                    }

                    AppNavDestination.PATCHES -> {
                        PatchesScreen(
                            patches = patches,
                            onTogglePatch = { patchId ->
                                patchesRepository.togglePatch(patchId)
                            },
                            onAddPatch = { newPatch ->
                                patchesRepository.addPatch(newPatch)
                            }
                        )
                    }

                    AppNavDestination.CONTROLLER -> {
                        ControllerScreen()
                    }

                    AppNavDestination.SETTINGS -> {
                        SettingsScreen()
                    }
                }
            }
        }
    }

    // Per-Game Configuration Dialog
    configuringGame?.let { game ->
        GameConfigDialog(
            game = game,
            onDismiss = { configuringGame = null },
            onSave = { updatedConfig ->
                gameRepository.updateConfig(game.id, updatedConfig)
                configuringGame = null
            }
        )
    }
}
