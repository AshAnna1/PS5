package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityScreen(
    entries: List<CompatibilityEntry>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGrade by remember { mutableStateOf<CompatibilityGrade?>(null) }
    var selectedEntryForDetails by remember { mutableStateOf<CompatibilityEntry?>(null) }

    val filteredList = remember(entries, searchQuery, selectedGrade) {
        entries.filter { entry ->
            val matchesSearch = entry.title.contains(searchQuery, ignoreCase = true) ||
                    entry.titleId.contains(searchQuery, ignoreCase = true)
            val matchesGrade = selectedGrade == null || entry.grade == selectedGrade
            matchesSearch && matchesGrade
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Compatibility Database", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Official KytyPS5 community verified test reports", color = PsTextSecondary, fontSize = 11.sp)
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("compat_search_input"),
                    placeholder = { Text("Search by game title or Title ID...", color = PsTextMuted) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PsTextSecondary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = PsTextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PsSurfaceDark,
                        unfocusedContainerColor = PsSurfaceDark,
                        focusedBorderColor = PsBluePrimary,
                        unfocusedBorderColor = PsSurfaceHighlight,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            // Grade Filters Row
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedGrade == null,
                            onClick = { selectedGrade = null },
                            label = { Text("ALL (${entries.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PsBluePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = PsSurfaceDark,
                                labelColor = PsTextSecondary
                            )
                        )
                    }
                    items(CompatibilityGrade.values()) { grade ->
                        val count = entries.count { it.grade == grade }
                        FilterChip(
                            selected = selectedGrade == grade,
                            onClick = { selectedGrade = if (selectedGrade == grade) null else grade },
                            label = { Text("${grade.label} ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PsBluePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = PsSurfaceDark,
                                labelColor = PsTextSecondary
                            )
                        )
                    }
                }
            }

            // Compatibility Items
            items(filteredList, key = { it.titleId }) { entry ->
                CompatibilityCard(entry = entry, onClick = { selectedEntryForDetails = entry })
            }
        }
    }

    selectedEntryForDetails?.let { detail ->
        CompatibilityDetailDialog(entry = detail, onDismiss = { selectedEntryForDetails = null })
    }
}

@Composable
fun CompatibilityCard(entry: CompatibilityEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("compat_card_${entry.titleId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PsSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PsSurfaceHighlight, PsSurfaceDark)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${entry.titleId} • ${entry.platform.name}",
                        color = PsAccentCyan,
                        fontSize = 12.sp
                    )
                }
                CompatibilityPill(grade = entry.grade)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tested on: ${entry.testedVersion}",
                    color = PsTextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = "FPS: ${entry.fpsPerformance}",
                    color = PsPlayableGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = entry.notes,
                color = PsTextMuted,
                fontSize = 11.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
fun CompatibilityDetailDialog(entry: CompatibilityEntry, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(entry.title, color = Color.White, fontWeight = FontWeight.Bold)
                Text("${entry.titleId} (${entry.platform.name})", color = PsAccentCyan, fontSize = 12.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Overall Status:", color = PsTextSecondary, fontSize = 13.sp)
                    CompatibilityPill(grade = entry.grade)
                }

                Divider(color = PsSurfaceHighlight)

                DetailRow(label = "Performance Target", value = entry.fpsPerformance)
                DetailRow(label = "Tested Kyty Version", value = entry.testedVersion)
                DetailRow(label = "Graphics Subsystem", value = entry.testedGpu)
                DetailRow(label = "Last Community Verification", value = entry.lastUpdated)
                if (entry.githubIssueId > 0) {
                    DetailRow(label = "GitHub Issue Tracker", value = "#${entry.githubIssueId}")
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Test Notes & Observations:", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Surface(
                    color = PsSurfaceVariantDark,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = entry.notes,
                        color = PsTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = PsBluePrimary)) {
                Text("Close")
            }
        },
        containerColor = PsSurfaceDark
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = PsTextMuted, fontSize = 12.sp)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
