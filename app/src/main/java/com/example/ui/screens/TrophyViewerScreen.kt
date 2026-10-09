package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrophyViewerScreen(
    game: GameItem,
    trophies: List<TrophyItem>,
    onUnlockTrophy: (Int) -> Unit,
    onBack: () -> Unit
) {
    var showHiddenSpoilers by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val platinumCount = remember(trophies) { trophies.count { it.type == TrophyType.PLATINUM && it.isEarned } }
    val goldCount = remember(trophies) { trophies.count { it.type == TrophyType.GOLD && it.isEarned } }
    val silverCount = remember(trophies) { trophies.count { it.type == TrophyType.SILVER && it.isEarned } }
    val bronzeCount = remember(trophies) { trophies.count { it.type == TrophyType.BRONZE && it.isEarned } }

    val earnedCount = remember(trophies) { trophies.count { it.isEarned } }
    val totalCount = remember(trophies) { trophies.size.coerceAtLeast(1) }
    val completionPercentage = remember(earnedCount, totalCount) { (earnedCount * 100) / totalCount }

    val filteredTrophies = remember(trophies, selectedFilter) {
        when (selectedFilter) {
            "EARNED" -> trophies.filter { it.isEarned }
            "LOCKED" -> trophies.filter { !it.isEarned }
            "HIDDEN" -> trophies.filter { it.isHidden }
            else -> trophies
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trophy Collection", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("trophy_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showHiddenSpoilers = !showHiddenSpoilers }) {
                        Icon(
                            imageVector = if (showHiddenSpoilers) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Hidden Trophies",
                            tint = if (showHiddenSpoilers) PsAccentCyan else PsTextMuted
                        )
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Game Header Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PsSurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PsSurfaceHighlight, PsSurfaceDark)))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (game.coverRes != null) {
                                Image(
                                    painter = painterResource(id = game.coverRes),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = game.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${game.id} • ${earnedCount}/$totalCount Trophies ($completionPercentage%)",
                                    color = PsAccentCyan,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { completionPercentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = PsAccentCyan,
                                    trackColor = PsSurfaceHighlight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = PsSurfaceHighlight)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Trophy Type Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            TrophyCountBadge(TrophyType.PLATINUM, platinumCount)
                            TrophyCountBadge(TrophyType.GOLD, goldCount)
                            TrophyCountBadge(TrophyType.SILVER, silverCount)
                            TrophyCountBadge(TrophyType.BRONZE, bronzeCount)
                        }
                    }
                }
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL", "EARNED", "LOCKED", "HIDDEN").forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 11.sp) },
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

            // Trophies List
            items(filteredTrophies, key = { it.id }) { trophy ->
                TrophyCardItem(
                    trophy = trophy,
                    showSpoiler = showHiddenSpoilers,
                    onUnlock = { onUnlockTrophy(trophy.id) }
                )
            }
        }
    }
}

@Composable
fun TrophyCountBadge(type: TrophyType, count: Int) {
    val (color, name) = when (type) {
        TrophyType.PLATINUM -> Color(0xFF00E5FF) to "Platinum"
        TrophyType.GOLD -> Color(0xFFFFD700) to "Gold"
        TrophyType.SILVER -> Color(0xFFC0C0C0) to "Silver"
        TrophyType.BRONZE -> Color(0xFFCD7F32) to "Bronze"
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "$count", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Text(text = name, color = PsTextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun TrophyCardItem(
    trophy: TrophyItem,
    showSpoiler: Boolean,
    onUnlock: () -> Unit
) {
    val isHiddenAndLocked = trophy.isHidden && !trophy.isEarned && !showSpoiler

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("trophy_item_${trophy.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (trophy.isEarned) PsSurfaceDark else Color(0xFF0D1322)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(
                    if (trophy.isEarned) PsBluePrimary.copy(alpha = 0.4f) else PsSurfaceHighlight.copy(alpha = 0.3f),
                    PsSurfaceDark
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trophy Icon / Hidden Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isHiddenAndLocked) PsSurfaceHighlight else {
                            when (trophy.type) {
                                TrophyType.PLATINUM -> Color(0xFF00E5FF)
                                TrophyType.GOLD -> Color(0xFFFFD700)
                                TrophyType.SILVER -> Color(0xFFC0C0C0)
                                TrophyType.BRONZE -> Color(0xFFCD7F32)
                            }.copy(alpha = if (trophy.isEarned) 0.3f else 0.1f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isHiddenAndLocked) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_hidden_trophy),
                        contentDescription = "Hidden Trophy",
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = when (trophy.type) {
                            TrophyType.PLATINUM -> Color(0xFF00E5FF)
                            TrophyType.GOLD -> Color(0xFFFFD700)
                            TrophyType.SILVER -> Color(0xFFE0E0E0)
                            TrophyType.BRONZE -> Color(0xFFCD7F32)
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isHiddenAndLocked) "Hidden Trophy" else trophy.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (trophy.isEarned) Color.White else PsTextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = trophy.type.name,
                        color = when (trophy.type) {
                            TrophyType.PLATINUM -> Color(0xFF00E5FF)
                            TrophyType.GOLD -> Color(0xFFFFD700)
                            TrophyType.SILVER -> Color(0xFFC0C0C0)
                            TrophyType.BRONZE -> Color(0xFFCD7F32)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (isHiddenAndLocked) "It's a mystery. Play the game to find out!" else trophy.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isHiddenAndLocked) PsTextMuted else PsTextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (trophy.isEarned) "Earned: ${trophy.earnedDate ?: "Completed"}" else "Unearned (${trophy.rarityPercentage}% players)",
                        color = if (trophy.isEarned) PsPlayableGreen else PsTextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            // Test Unlock button if not earned
            if (!trophy.isEarned) {
                IconButton(
                    onClick = onUnlock,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PsSurfaceHighlight)
                        .testTag("unlock_trophy_${trophy.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = "Test Unlock",
                        tint = PsAccentCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Earned",
                    tint = PsPlayableGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
