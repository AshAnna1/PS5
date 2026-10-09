# KytyPS5 for Android

KytyPS5 is an Android application rewritten from the open-source PlayStation 5 & PlayStation 4 emulator project, bringing the emulation manager, game launcher, compatibility database, patch manager, and game runner to mobile and tablet Android devices with Jetpack Compose and Material 3.

## Features

- **Game Library**: Manage and launch installed PlayStation 5 and PlayStation 4 games, homebrew ELFs, and `.zar` archives. Track playtime, completion percentage, and favorites.
- **Compatibility Database**: Community-verified compatibility list categorized by status (Playable, In-Game, Intro, Loadable, Nothing) with tested versions, GPU specifications, and FPS benchmarks.
- **Game Patches & Cheats**: 60 FPS / 120 FPS frame rate unlockers, resolution scale modifications, motion blur toggles, and memory cheat codes.
- **DualSense Controller & Input**: DualSense wireless controller integration, custom RGB lightbar picker with live preview, haptic vibration feedback, and full on-screen touch gamepad controls (D-Pad, Triangle/Circle/Cross/Square, L1/R1/L2/R2, L3/R3, Touchpad).
- **Trophy System**: Built-in trophy tracker with Platinum, Gold, Silver, and Bronze achievements, hidden trophy spoiler reveal, and unlock notification sounds.
- **Interactive Emulator Runner**: Emulation execution environment with real-time Vulkan 1.3 telemetry (FPS, frame time, draw calls, VRAM usage, CPU load), save/load states, and in-game pause controls.
- **System Diagnostics**: Device Vulkan 1.3 capability verification, shader cache manager, and update checker.

## Architecture

- **Platform**: Android SDK 36 (minSdk 26)
- **UI Framework**: Jetpack Compose with Material Design 3
- **Language**: Kotlin 2.2 with Kotlin DSL Gradle build
- **Audio & Haptics**: Android MediaPlayer for PS5 trophy chime and Android Vibrator for DualSense haptics
