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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.SoundAndHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerScreen() {
    val context = LocalContext.current
    val soundAndHaptics = remember { SoundAndHaptics(context) }

    var lightbarColorHex by remember { mutableStateOf("#006FCD") }
    var vibrationIntensity by remember { mutableFloatStateOf(0.85f) }
    var speakerVolume by remember { mutableFloatStateOf(0.7f) }
    var deadzone by remember { mutableFloatStateOf(0.12f) }
    var touchControlsScale by remember { mutableFloatStateOf(1.0f) }

    val presetColors = listOf(
        "#006FCD" to "PlayStation Blue",
        "#00E5FF" to "Astro Cyan",
        "#FF5252" to "Kratos Red",
        "#00E676" to "Jade Green",
        "#7C4DFF" to "Cosmic Purple",
        "#FF9100" to "Sunset Amber",
        "#E0E0E0" to "Glacier White"
    )

    val lightbarColor = try {
        Color(android.graphics.Color.parseColor(lightbarColorHex))
    } catch (_: Exception) {
        PsBluePrimary
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("DualSense & Input Mapping", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Lightbar RGB, adaptive triggers & haptics", color = PsTextSecondary, fontSize = 11.sp)
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
            // Interactive DualSense Preview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PsSurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(lightbarColor.copy(alpha = 0.6f), PsSurfaceDark)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Controller Graphic Outline with Glowing Lightbar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Lightbar Glow Effect
                            Box(
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(lightbarColor)
                                    .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(9.dp))
                            )

                            // Touchpad representation
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .size(width = 110.dp, height = 50.dp)
                                    .offset(y = (-14).dp)
                                    .border(1.dp, lightbarColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SportsEsports,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "DualSense Wireless Controller",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Color: $lightbarColorHex • Bluetooth Status: Ready",
                            color = lightbarColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Lightbar RGB Palette
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PsSurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Lightbar RGB Color", style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("KytyPS5 updates the physical DualSense LED ring and on-screen HUD.", color = PsTextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            presetColors.forEach { (hex, name) ->
                                val swatchColor = try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { Color.Blue }
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(swatchColor)
                                        .clickable {
                                            lightbarColorHex = hex
                                            soundAndHaptics.triggerButtonHaptic(0.5f)
                                        }
                                        .border(
                                            width = if (lightbarColorHex == hex) 3.dp else 1.dp,
                                            color = if (lightbarColorHex == hex) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Vibration & Haptics Test
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PsSurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Vibration & Haptic Feedback", style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Intensity: ${(vibrationIntensity * 100).toInt()}%", color = PsAccentCyan, fontSize = 12.sp)

                        Slider(
                            value = vibrationIntensity,
                            onValueChange = { vibrationIntensity = it },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(thumbColor = PsBluePrimary, activeTrackColor = PsBluePrimary)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                soundAndHaptics.triggerButtonHaptic(vibrationIntensity)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PsBluePrimary),
                            modifier = Modifier.fillMaxWidth().testTag("test_vibration_button")
                        ) {
                            Icon(imageVector = Icons.Default.Vibration, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Test Haptic Vibration")
                        }
                    }
                }
            }

            // Analog Deadzones & Sound
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PsSurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Thumbstick Deadzone: ${(deadzone * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Slider(
                            value = deadzone,
                            onValueChange = { deadzone = it },
                            valueRange = 0.02f..0.30f
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Controller Speaker Volume: ${(speakerVolume * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Slider(
                            value = speakerVolume,
                            onValueChange = { speakerVolume = it },
                            valueRange = 0f..1f
                        )
                    }
                }
            }
        }
    }
}
