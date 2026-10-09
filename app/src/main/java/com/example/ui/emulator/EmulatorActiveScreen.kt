package com.example.ui.emulator

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun EmulatorActiveScreen(
    viewModel: EmulatorRunnerViewModel,
    onExit: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    BackHandler {
        if (state.isRunning) {
            viewModel.stopGame()
            onExit()
        } else {
            onExit()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("emulator_active_screen")
    ) {
        // 1. GAME RENDERING CANVAS / BOOT LOG SCREEN
        if (state.isBooting) {
            BootSequenceView(logs = state.bootLogs, gameTitle = state.activeGame?.title ?: "PS5 Game")
        } else {
            // Live Game Graphic Canvas
            val coverRes = state.activeGame?.coverRes
            if (coverRes != null) {
                Image(
                    painter = painterResource(id = coverRes),
                    contentDescription = "Emulated Game Output",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.activeGame?.title ?: "KytyPS5 Guest Render",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }

        // 2. PAUSE OVERLAY
        if (state.isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PsSurfaceDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = null,
                            tint = PsAccentCyan,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "GAME PAUSED",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = state.activeGame?.title ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PsTextSecondary
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { viewModel.resumeGame() },
                                colors = ButtonDefaults.buttonColors(containerColor = PsBluePrimary),
                                modifier = Modifier.testTag("resume_button")
                            ) {
                                Text("Resume")
                            }
                            OutlinedButton(
                                onClick = {
                                    viewModel.stopGame()
                                    onExit()
                                },
                                modifier = Modifier.testTag("quit_game_button")
                            ) {
                                Text("Exit Game", color = PsLoadableRed)
                            }
                        }
                    }
                }
            }
        }

        // 3. TOP TELEMETRY HUD BAR
        if (state.showPerfOverlay && !state.isBooting) {
            TelemetryOverlay(state = state, modifier = Modifier.align(Alignment.TopStart))
        }

        // 4. TROPHY UNLOCK NOTIFICATION BANNER (PS5 Style Popup)
        AnimatedVisibility(
            visible = state.activeTrophyToast != null,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
        ) {
            state.activeTrophyToast?.let { trophy ->
                TrophyNotificationBanner(
                    trophy = trophy,
                    onDismiss = { viewModel.dismissTrophyToast() }
                )
            }
        }

        // 5. STATUS MESSAGE TOAST (e.g. Saved State, Screenshot)
        state.statusMessage?.let { msg ->
            if (msg != "In-Game") {
                Surface(
                    color = PsSurfaceHighlight.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 70.dp)
                ) {
                    Text(
                        text = msg,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 6. TOP QUICK CONTROL BAR
        TopQuickMenuBar(
            isPaused = state.isPaused,
            onTogglePause = { if (state.isPaused) viewModel.resumeGame() else viewModel.pauseGame() },
            onSaveState = { viewModel.saveState() },
            onLoadState = { viewModel.loadState() },
            onScreenshot = { viewModel.takeScreenshot() },
            onToggleTelemetry = { viewModel.togglePerfOverlay() },
            onTestTrophy = {
                // Test unlock next available trophy
                viewModel.triggerTrophyUnlock(1)
            },
            onExit = {
                viewModel.stopGame()
                onExit()
            },
            modifier = Modifier.align(Alignment.TopEnd)
        )

        // 7. ON-SCREEN DUALSENSE TOUCH CONTROLS
        if (state.showTouchControls && !state.isBooting) {
            DualSenseTouchOverlay(
                onButtonPress = { viewModel.pressButton(it) },
                lightbarColorHex = state.activeGame?.config?.controllerColor ?: "#006FCD",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun BootSequenceView(logs: List<String>, gameTitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF060913))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                CircularProgressIndicator(
                    color = PsAccentCyan,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Booting $gameTitle via KytyPS5...",
                    color = PsAccentCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Divider(color = PsSurfaceHighlight)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                reverseLayout = false,
                modifier = Modifier.fillMaxSize()
            ) {
                items(logs) { log ->
                    Text(
                        text = log,
                        color = when {
                            log.contains("[Error]") -> PsLoadableRed
                            log.contains("[Shader]") -> PsInGameYellow
                            log.contains("[GPU]") -> PsAccentCyan
                            log.contains("[KytyPS5]") -> PsPlayableGreen
                            else -> PsTextSecondary
                        },
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TelemetryOverlay(state: EmulatorRuntimeState, modifier: Modifier = Modifier) {
    Surface(
        color = Color.Black.copy(alpha = 0.75f),
        shape = RoundedCornerShape(bottomEnd = 12.dp),
        modifier = modifier.padding(top = 28.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "FPS: ",
                    color = PsTextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${state.fps}",
                    color = if (state.fps >= 55f) PsPlayableGreen else PsInGameYellow,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = " (${state.frameTimeMs}ms)",
                    color = PsTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = "VK: ${state.vulkanDrawCalls} calls | VRAM: ${state.vramUsedMb}MB",
                color = PsTextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "CPU: ${state.cpuLoadPercent}% | RDNA2 800MHz",
                color = PsTextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun TopQuickMenuBar(
    isPaused: Boolean,
    onTogglePause: () -> Unit,
    onSaveState: () -> Unit,
    onLoadState: () -> Unit,
    onScreenshot: () -> Unit,
    onToggleTelemetry: () -> Unit,
    onTestTrophy: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Black.copy(alpha = 0.7f),
        shape = RoundedCornerShape(bottomStart = 16.dp),
        modifier = modifier.padding(top = 28.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            IconButton(onClick = onTogglePause, modifier = Modifier.size(36.dp).testTag("quick_pause")) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onSaveState, modifier = Modifier.size(36.dp).testTag("quick_save_state")) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save State",
                    tint = PsAccentCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onLoadState, modifier = Modifier.size(36.dp).testTag("quick_load_state")) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = "Load State",
                    tint = PsInGameYellow,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onScreenshot, modifier = Modifier.size(36.dp).testTag("quick_screenshot")) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Screenshot",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onToggleTelemetry, modifier = Modifier.size(36.dp).testTag("quick_telemetry")) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Toggle HUD",
                    tint = PsPlayableGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onTestTrophy, modifier = Modifier.size(36.dp).testTag("quick_trophy_test")) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Unlock Trophy",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onExit, modifier = Modifier.size(36.dp).testTag("quick_exit")) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit Game",
                    tint = PsLoadableRed,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun TrophyNotificationBanner(
    trophy: TrophyItem,
    onDismiss: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2638)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable { onDismiss() }
            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.6f), RoundedCornerShape(24.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (trophy.type) {
                            TrophyType.PLATINUM -> Color(0xFF00E5FF)
                            TrophyType.GOLD -> Color(0xFFFFD700)
                            TrophyType.SILVER -> Color(0xFFC0C0C0)
                            TrophyType.BRONZE -> Color(0xFFCD7F32)
                        }.copy(alpha = 0.25f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = when (trophy.type) {
                        TrophyType.PLATINUM -> Color(0xFF00E5FF)
                        TrophyType.GOLD -> Color(0xFFFFD700)
                        TrophyType.SILVER -> Color(0xFFE0E0E0)
                        TrophyType.BRONZE -> Color(0xFFCD7F32)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "TROPHY UNLOCKED! (${trophy.type.name})",
                        color = Color(0xFFFFD700),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = trophy.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = trophy.description,
                    color = PsTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun DualSenseTouchOverlay(
    onButtonPress: (String) -> Unit,
    lightbarColorHex: String,
    modifier: Modifier = Modifier
) {
    val lightbarColor = try {
        Color(android.graphics.Color.parseColor(lightbarColorHex))
    } catch (_: Exception) {
        PsBluePrimary
    }

    Box(
        modifier = modifier
            .padding(bottom = 12.dp)
    ) {
        // Top Shoulder Triggers (L1, L2, R1, R2)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .align(Alignment.CenterStart)
                .offset(y = (-80).dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ShoulderButton("L2") { onButtonPress("L2") }
                ShoulderButton("L1") { onButtonPress("L1") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ShoulderButton("R1") { onButtonPress("R1") }
                ShoulderButton("R2") { onButtonPress("R2") }
            }
        }

        // Center DualSense Lightbar Glow Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
                .width(140.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(lightbarColor)
        )

        // Center Touchpad & System Buttons (Share, PS, Options)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmallSystemButton("SHARE") { onButtonPress("SHARE") }
            Surface(
                color = PsSurfaceHighlight.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(90.dp)
                    .height(44.dp)
                    .clickable { onButtonPress("TOUCHPAD") }
                    .border(1.dp, lightbarColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("TOUCHPAD", fontSize = 10.sp, color = PsTextSecondary, fontWeight = FontWeight.SemiBold)
                }
            }
            SmallSystemButton("OPTIONS") { onButtonPress("OPTIONS") }
        }

        // Left Side: D-PAD & L3 Analog
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // D-Pad
                DPadControl(onDirection = onButtonPress)
                Spacer(modifier = Modifier.height(16.dp))
                // L3 Stick Disc
                AnalogStick("L3") { onButtonPress("L3") }
            }
        }

        // Right Side: PS Action Buttons (Triangle, Circle, Cross, Square) & R3
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // PS 4-Button Cluster
                ActionButtonsCluster(onAction = onButtonPress)
                Spacer(modifier = Modifier.height(16.dp))
                // R3 Stick Disc
                AnalogStick("R3") { onButtonPress("R3") }
            }
        }
    }
}

@Composable
fun DPadControl(onDirection: (String) -> Unit) {
    Box(
        modifier = Modifier.size(130.dp),
        contentAlignment = Alignment.Center
    ) {
        // UP
        DPadButton(
            text = "▲",
            modifier = Modifier.align(Alignment.TopCenter)
        ) { onDirection("UP") }

        // DOWN
        DPadButton(
            text = "▼",
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { onDirection("DOWN") }

        // LEFT
        DPadButton(
            text = "◀",
            modifier = Modifier.align(Alignment.CenterStart)
        ) { onDirection("LEFT") }

        // RIGHT
        DPadButton(
            text = "▶",
            modifier = Modifier.align(Alignment.CenterEnd)
        ) { onDirection("RIGHT") }
    }
}

@Composable
fun DPadButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = Color(0xFF1E293B).copy(alpha = 0.8f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .size(42.dp)
            .clickable { onClick() }
            .border(1.dp, PsSurfaceHighlight, RoundedCornerShape(8.dp))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun ActionButtonsCluster(onAction: (String) -> Unit) {
    Box(
        modifier = Modifier.size(130.dp),
        contentAlignment = Alignment.Center
    ) {
        // TRIANGLE (Top)
        PsActionButton(
            symbol = "▲",
            color = PsTriangleGreen,
            modifier = Modifier.align(Alignment.TopCenter)
        ) { onAction("TRIANGLE") }

        // CROSS (Bottom)
        PsActionButton(
            symbol = "✕",
            color = PsCrossBlue,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { onAction("CROSS") }

        // SQUARE (Left)
        PsActionButton(
            symbol = "■",
            color = PsSquarePink,
            modifier = Modifier.align(Alignment.CenterStart)
        ) { onAction("SQUARE") }

        // CIRCLE (Right)
        PsActionButton(
            symbol = "●",
            color = PsCircleRed,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) { onAction("CIRCLE") }
    }
}

@Composable
fun PsActionButton(
    symbol: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0xFF1E293B).copy(alpha = 0.85f),
        shape = CircleShape,
        modifier = modifier
            .size(42.dp)
            .clickable { onClick() }
            .border(1.5.dp, color.copy(alpha = 0.8f), CircleShape)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                color = color,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ShoulderButton(label: String, onClick: () -> Unit) {
    Surface(
        color = Color(0xFF1E293B).copy(alpha = 0.8f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .width(52.dp)
            .height(36.dp)
            .clickable { onClick() }
            .border(1.dp, PsSurfaceHighlight, RoundedCornerShape(8.dp))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SmallSystemButton(label: String, onClick: () -> Unit) {
    Surface(
        color = Color(0xFF1E293B).copy(alpha = 0.7f),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = label, color = PsTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AnalogStick(label: String, onClick: () -> Unit) {
    Surface(
        color = Color(0xFF0F172A).copy(alpha = 0.85f),
        shape = CircleShape,
        modifier = Modifier
            .size(60.dp)
            .clickable { onClick() }
            .border(1.5.dp, PsSurfaceHighlight, CircleShape)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF334155))
            )
            Text(text = label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
