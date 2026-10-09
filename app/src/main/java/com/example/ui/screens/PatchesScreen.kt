package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GamePatch
import com.example.model.PatchType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchesScreen(
    patches: List<GamePatch>,
    onTogglePatch: (String) -> Unit,
    onAddPatch: (GamePatch) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Game Patches & Cheats", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Runtime memory hex modifications & 60/120 FPS unlocks", color = PsTextSecondary, fontSize = 11.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PsBackgroundDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PsBluePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_patch_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Patch")
            }
        },
        containerColor = PsBackgroundDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Surface(
                    color = PsSurfaceHighlight.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = PsAccentCyan)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Patches are automatically applied at launch time by KytyPS5 GuestInstructionPatcher and CheatRepository.",
                            color = PsTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            items(patches, key = { it.id }) { patch ->
                PatchCardItem(patch = patch, onToggle = { onTogglePatch(patch.id) })
            }
        }
    }

    if (showAddDialog) {
        AddPatchDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newPatch ->
                onAddPatch(newPatch)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun PatchCardItem(patch: GamePatch, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("patch_card_${patch.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PsSurfaceDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = patch.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Target: ${patch.gameId} • By ${patch.author}",
                        color = PsAccentCyan,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                when (patch.type) {
                                    PatchType.FPS_UNLOCK -> PsPlayableGreen.copy(alpha = 0.2f)
                                    PatchType.RESOLUTION_SCALE -> PsBluePrimary.copy(alpha = 0.2f)
                                    PatchType.GRAPHICS_TWEAK -> PsAccentPurple.copy(alpha = 0.2f)
                                    PatchType.CHEAT_CODE -> PsIntroOrange.copy(alpha = 0.2f)
                                },
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = patch.type.name,
                            color = when (patch.type) {
                                PatchType.FPS_UNLOCK -> PsPlayableGreen
                                PatchType.RESOLUTION_SCALE -> PsBluePrimary
                                PatchType.GRAPHICS_TWEAK -> PsAccentPurple
                                PatchType.CHEAT_CODE -> PsIntroOrange
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = patch.description,
                    color = PsTextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = patch.codeHex,
                    color = PsTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Switch(
                checked = patch.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PsBluePrimary,
                    uncheckedTrackColor = PsSurfaceVariantDark
                ),
                modifier = Modifier.testTag("switch_patch_${patch.id}")
            )
        }
    }
}

@Composable
fun AddPatchDialog(onDismiss: () -> Unit, onAdd: (GamePatch) -> Unit) {
    var title by remember { mutableStateOf("") }
    var gameId by remember { mutableStateOf("PPSA02188") }
    var desc by remember { mutableStateOf("") }
    var codeHex by remember { mutableStateOf("0x00A00000: 0x90 0x90") }
    var selectedType by remember { mutableStateOf(PatchType.FPS_UNLOCK) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Game Patch / Cheat", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Patch Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                OutlinedTextField(
                    value = gameId,
                    onValueChange = { gameId = it },
                    label = { Text("Target Title ID") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                OutlinedTextField(
                    value = codeHex,
                    onValueChange = { codeHex = it },
                    label = { Text("Memory Offset & Hex Bytes") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Text("Patch Type:", color = PsTextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PatchType.values().take(3).forEach { pt ->
                        FilterChip(
                            selected = selectedType == pt,
                            onClick = { selectedType = pt },
                            label = { Text(pt.name.take(8), fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(
                            GamePatch(
                                id = "custom_" + System.currentTimeMillis(),
                                gameId = gameId.trim().uppercase(),
                                title = title.trim(),
                                author = "User Custom",
                                description = desc.ifBlank { "Custom user patch" },
                                isEnabled = true,
                                type = selectedType,
                                codeHex = codeHex
                            )
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Save Patch")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = PsTextMuted) }
        },
        containerColor = PsSurfaceDark
    )
}
