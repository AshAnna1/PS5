package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun GameConfigDialog(
    game: GameItem,
    onDismiss: () -> Unit,
    onSave: (EmulatorConfig) -> Unit
) {
    var config by remember { mutableStateOf(game.config) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val presetColors = listOf(
        "#006FCD" to "Classic PS Blue",
        "#00E5FF" to "Cyan Glow",
        "#FF5252" to "Crimson Red",
        "#00E676" to "Emerald Green",
        "#7C4DFF" to "Cosmic Purple",
        "#FF9100" to "Solar Orange",
        "#FFFFFF" to "Pure White"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, PsSurfaceHighlight, RoundedCornerShape(16.dp))
                .testTag("game_config_dialog"),
            color = PsSurfaceDark
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Game Configuration",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${game.title} (${game.id})",
                            style = MaterialTheme.typography.bodySmall,
                            color = PsAccentCyan
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = PsTextSecondary)
                    }
                }

                // Tabs: Graphics, Vulkan/Shader, Controller, CPU/System
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = PsSurfaceVariantDark,
                    contentColor = PsBluePrimary,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Display", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Vulkan/GPU", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("DualSense", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("CPU/Core", fontSize = 12.sp) }
                    )
                }

                // Tab Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            // GRAPHICS / DISPLAY
                            item {
                                Text("Screen Resolution", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Resolution.values().forEach { res ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { config = config.copy(resolution = res.name) }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        RadioButton(
                                            selected = config.resolution == res.name,
                                            onClick = { config = config.copy(resolution = res.name) }
                                        )
                                        Text(res.display, color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }

                            item {
                                Text("Present Mode", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                PresentMode.values().forEach { pm ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { config = config.copy(presentMode = pm.name) }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        RadioButton(
                                            selected = config.presentMode == pm.name,
                                            onClick = { config = config.copy(presentMode = pm.name) }
                                        )
                                        Text(pm.display, color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }

                            item {
                                Text("VBlank Frequency", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(30, 60, 120).forEach { hz ->
                                        FilterChip(
                                            selected = config.vblankFrequency == hz,
                                            onClick = { config = config.copy(vblankFrequency = hz) },
                                            label = { Text("$hz Hz") }
                                        )
                                    }
                                }
                            }

                            item {
                                SwitchRow(
                                    title = "Fullscreen Immersion",
                                    subtitle = "Hides Android status bar and system navigation bar",
                                    checked = config.fullscreen,
                                    onCheckedChange = { config = config.copy(fullscreen = it) }
                                )
                            }
                        }

                        1 -> {
                            // VULKAN & SHADERS
                            item {
                                SwitchRow(
                                    title = "GPU Tessellation",
                                    subtitle = "Translates hardware guest tessellation shaders to compute shaders",
                                    checked = config.tessellation,
                                    onCheckedChange = { config = config.copy(tessellation = it) }
                                )
                            }
                            item {
                                SwitchRow(
                                    title = "Readback Linear Images",
                                    subtitle = "Copies linear textures from guest VRAM into host staging memory",
                                    checked = config.readbackLinearImages,
                                    onCheckedChange = { config = config.copy(readbackLinearImages = it) }
                                )
                            }
                            item {
                                SwitchRow(
                                    title = "Sync Raw Image Buffers",
                                    subtitle = "Guarantees coherent frame sampling for deferred rendering",
                                    checked = config.syncRawImageBuffers,
                                    onCheckedChange = { config = config.copy(syncRawImageBuffers = it) }
                                )
                            }
                            item {
                                SwitchRow(
                                    title = "Vulkan Validation Layers",
                                    subtitle = "Enables VK_LAYER_KHRONOS_validation diagnostics (slows down execution)",
                                    checked = config.vulkanValidation,
                                    onCheckedChange = { config = config.copy(vulkanValidation = it) }
                                )
                            }
                            item {
                                SwitchRow(
                                    title = "Shader Validation",
                                    subtitle = "Validates SPIR-V bytecode with spirv-val before compiling pipelines",
                                    checked = config.shaderValidation,
                                    onCheckedChange = { config = config.copy(shaderValidation = it) }
                                )
                            }
                        }

                        2 -> {
                            // DUALSENSE & INPUT
                            item {
                                Text("DualSense Lightbar Color", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Text("Select the RGB glow color for the virtual controller and physical gamepad.", color = PsTextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    presetColors.forEach { (hex, name) ->
                                        val color = try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { Color.Blue }
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .clickable { config = config.copy(controllerColor = hex) }
                                                .border(
                                                    width = if (config.controllerColor == hex) 3.dp else 1.dp,
                                                    color = if (config.controllerColor == hex) Color.White else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                        )
                                    }
                                }
                            }

                            item {
                                Text("Vibration Intensity: ${(config.vibrationIntensity * 100).toInt()}%", color = Color.White)
                                Slider(
                                    value = config.vibrationIntensity,
                                    onValueChange = { config = config.copy(vibrationIntensity = it) },
                                    valueRange = 0f..1f,
                                    colors = SliderDefaults.colors(thumbColor = PsBluePrimary, activeTrackColor = PsBluePrimary)
                                )
                            }

                            item {
                                Text("DualSense Speaker Volume: ${(config.speakerVolume * 100).toInt()}%", color = Color.White)
                                Slider(
                                    value = config.speakerVolume,
                                    onValueChange = { config = config.copy(speakerVolume = it) },
                                    valueRange = 0f..1f,
                                    colors = SliderDefaults.colors(thumbColor = PsBluePrimary, activeTrackColor = PsBluePrimary)
                                )
                            }

                            item {
                                SwitchRow(
                                    title = "On-Screen Touch Controls",
                                    subtitle = "Display PlayStation gamepad overlay when no physical controller is attached",
                                    checked = config.touchControlsEnabled,
                                    onCheckedChange = { config = config.copy(touchControlsEnabled = it) }
                                )
                            }
                        }

                        3 -> {
                            // CPU / CORE
                            item {
                                SwitchRow(
                                    title = "AMD Zen 2 CPU Emulation",
                                    subtitle = "Optimized instruction patcher and x86-64 to ARM64 JIT register allocator",
                                    checked = config.amdCpuEmulation,
                                    onCheckedChange = { config = config.copy(amdCpuEmulation = it) }
                                )
                            }
                            item {
                                SwitchRow(
                                    title = "Red Zone Protection",
                                    subtitle = "Guards the 128-byte System V AMD64 ABI scratchpad area",
                                    checked = config.redZoneProtection,
                                    onCheckedChange = { config = config.copy(redZoneProtection = it) }
                                )
                            }
                            item {
                                SwitchRow(
                                    title = "Skip Notice & Health Screen",
                                    subtitle = "Fast boots directly into game title sequence",
                                    checked = config.skipNoticeScreen,
                                    onCheckedChange = { config = config.copy(skipNoticeScreen = it) }
                                )
                            }
                            item {
                                SwitchRow(
                                    title = "Trophy Notifications",
                                    subtitle = "Display animated popup banner and play chime when unlocking achievements",
                                    checked = config.trophyNotifications,
                                    onCheckedChange = { config = config.copy(trophyNotifications = it) }
                                )
                            }
                        }
                    }
                }

                // Footer Actions
                Divider(color = PsSurfaceHighlight)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = PsTextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(config) },
                        colors = ButtonDefaults.buttonColors(containerColor = PsBluePrimary),
                        modifier = Modifier.testTag("save_config_button")
                    ) {
                        Text("Save Configuration")
                    }
                }
            }
        }
    }
}

@Composable
fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = PsTextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PsBluePrimary,
                uncheckedTrackColor = PsSurfaceVariantDark
            )
        )
    }
}
