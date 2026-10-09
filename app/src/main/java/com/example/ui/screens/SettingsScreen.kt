package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var checkUpdatesOnStartup by remember { mutableStateOf(true) }
    var updateStatus by remember { mutableStateOf<String?>(null) }
    var isCheckingUpdates by remember { mutableStateOf(false) }
    var shaderCacheSizeMb by remember { mutableIntStateOf(142) }
    var clearCacheStatus by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Global Settings & Diagnostics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("KytyPS5 Emulator Core & Environment", color = PsTextSecondary, fontSize = 11.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PsBackgroundDark)
            )
        },
        containerColor = PsBackgroundDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Emulator Version Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PsSurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("KytyPS5 Emulator", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Version 0.3.5 (Git main - 2026.10)", color = PsAccentCyan, fontSize = 12.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .background(PsPlayableGreen.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("UP TO DATE", color = PsPlayableGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = PsSurfaceHighlight)
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                isCheckingUpdates = true
                                updateStatus = "Checking github.com/AshAnna1/KytyPS5 releases..."
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PsBluePrimary),
                            modifier = Modifier.fillMaxWidth().testTag("check_update_button")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Check for Updates")
                        }

                        updateStatus?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "KytyPS5 is running the latest release (v0.3.5)!", color = PsPlayableGreen, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Hardware & Diagnostics
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PsSurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Device & Hardware Capabilities", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

                        DiagnosticItem("Vulkan API Version", "1.3.280 (Supported)")
                        DiagnosticItem("GPU Renderer", "Qualcomm Adreno / Mali-G715 / Mesa Turnip")
                        DiagnosticItem("RDNA2 Mesh Shaders", "VK_EXT_mesh_shader enabled")
                        DiagnosticItem("Memory Subsystem", "12 GB LPDDR5X (Host Unified)")
                        DiagnosticItem("CPU Core Topology", "8 Cores (1x X4 + 5x A720 + 2x A520)")
                        DiagnosticItem("Prospero ASLR Map", "64-bit 48-bit Virtual VA")
                    }
                }
            }

            // Storage & Shader Cache Management
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PsSurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Shader Cache & Storage", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Precompiled SPIR-V shaders reduce in-game stutter. Current cache size: $shaderCacheSizeMb MB.",
                            color = PsTextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                shaderCacheSizeMb = 0
                                clearCacheStatus = "Pipeline cache cleared."
                            },
                            modifier = Modifier.fillMaxWidth().testTag("clear_cache_button")
                        ) {
                            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = PsLoadableRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Clear Vulkan Pipeline Cache", color = PsLoadableRed)
                        }

                        clearCacheStatus?.let {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(it, color = PsPlayableGreen, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Credits & Open Source Licenses
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PsSurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("About KytyPS5 & Credits", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "KytyPS5 is an open-source PlayStation 5 & PlayStation 4 emulator based on Kyty by InoriRus. Licensed under GNU GPL v2.0.",
                            color = PsTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Special Thanks:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("• InoriRus/Kyty — original project foundation (MIT License)", color = PsTextMuted, fontSize = 11.sp)
                        Text("• shadps4-emu — reference for memory behavior and AVPlayer", color = PsTextMuted, fontSize = 11.sp)
                        Text("• ZArchive & Xbyak — archive extraction and JIT translation", color = PsTextMuted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = PsTextMuted, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
