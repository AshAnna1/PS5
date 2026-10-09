package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.theme.*

enum class LibraryFilter {
    ALL, PS5, PS4, HOMEBREW, FAVORITES, PLAYABLE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameLibraryScreen(
    games: List<GameItem>,
    onLaunchGame: (GameItem) -> Unit,
    onOpenGameConfig: (GameItem) -> Unit,
    onOpenTrophies: (GameItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddGame: (GameItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(LibraryFilter.ALL) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredGames = remember(games, searchQuery, selectedFilter) {
        games.filter { game ->
            val matchesSearch = game.title.contains(searchQuery, ignoreCase = true) ||
                    game.id.contains(searchQuery, ignoreCase = true) ||
                    game.publisher.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                LibraryFilter.ALL -> true
                LibraryFilter.PS5 -> game.platform == GamePlatform.PS5
                LibraryFilter.PS4 -> game.platform == GamePlatform.PS4
                LibraryFilter.HOMEBREW -> game.platform == GamePlatform.HOMEBREW
                LibraryFilter.FAVORITES -> game.isFavorite
                LibraryFilter.PLAYABLE -> game.compatibilityGrade == CompatibilityGrade.PLAYABLE
            }
            matchesSearch && matchesFilter
        }
    }

    val mostRecentGame = remember(games) {
        games.maxByOrNull { it.lastPlayed }
    }

    Scaffold(
        containerColor = PsBackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PsBluePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_game_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Game")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // 1. HERO BANNER & EMULATOR STATUS
            item {
                HeroBannerSection(
                    recentGame = mostRecentGame,
                    onLaunch = { mostRecentGame?.let(onLaunchGame) }
                )
            }

            // 2. SEARCH BAR
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("library_search_input"),
                    placeholder = { Text("Search installed games or title ID...", color = PsTextMuted) },
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

            // 3. CATEGORY FILTER CHIPS
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(LibraryFilter.values()) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = PsSurfaceDark,
                                labelColor = PsTextSecondary,
                                selectedContainerColor = PsBluePrimary,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedFilter == filter,
                                borderColor = PsSurfaceHighlight,
                                selectedBorderColor = PsBluePrimary
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("filter_${filter.name.lowercase()}")
                        )
                    }
                }
            }

            // 4. HEADER: GAME COUNT
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Installed Titles (${filteredGames.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Storage: 198.4 GB",
                        style = MaterialTheme.typography.bodySmall,
                        color = PsTextMuted
                    )
                }
            }

            // 5. GAME ITEMS
            if (filteredGames.isEmpty()) {
                item {
                    EmptyLibraryView()
                }
            } else {
                items(filteredGames, key = { it.id }) { game ->
                    GameCardItem(
                        game = game,
                        onLaunch = { onLaunchGame(game) },
                        onConfig = { onOpenGameConfig(game) },
                        onTrophies = { onOpenTrophies(game) },
                        onToggleFavorite = { onToggleFavorite(game.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddGameDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newGame ->
                onAddGame(newGame)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun HeroBannerSection(recentGame: GameItem?, onLaunch: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PsSurfaceDark)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.kyty_hero_banner_1791544573523),
                contentDescription = "KytyPS5 Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xFF070B16).copy(alpha = 0.95f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PsBluePrimary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("KYTY PS5 v0.3.5", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Prospero & Orbis Engine", color = PsAccentCyan, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recentGame?.title ?: "Select a Game to Launch",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (recentGame != null) {
                    Button(
                        onClick = onLaunch,
                        colors = ButtonDefaults.buttonColors(containerColor = PsPlayableGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("quick_resume_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Resume Game", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun GameCardItem(
    game: GameItem,
    onLaunch: () -> Unit,
    onConfig: () -> Unit,
    onTrophies: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("game_card_${game.id}"),
        colors = CardDefaults.cardColors(containerColor = PsSurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PsSurfaceHighlight, PsSurfaceDark)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cover Image
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PsSurfaceVariantDark)
                    .clickable { onLaunch() }
            ) {
                if (game.coverRes != null) {
                    Image(
                        painter = painterResource(id = game.coverRes),
                        contentDescription = game.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = PsBluePrimary,
                        modifier = Modifier.size(40.dp).align(Alignment.Center)
                    )
                }

                // Platform Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            when (game.platform) {
                                GamePlatform.PS5 -> PsBluePrimary
                                GamePlatform.PS4 -> Color(0xFF003791)
                                GamePlatform.HOMEBREW -> PsAccentPurple
                            }
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = game.platform.name,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onLaunch() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "${game.id} • v${game.version} • ${game.format.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PsTextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Compatibility Status Pill
                    CompatibilityPill(grade = game.compatibilityGrade)

                    // Trophy Count
                    if (game.trophiesTotal > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onTrophies() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${game.trophiesEarned}/${game.trophiesTotal}",
                                color = PsTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "${game.sizeGb} GB",
                        color = PsTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Action Buttons
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp).testTag("fav_button_${game.id}")
                ) {
                    Icon(
                        imageVector = if (game.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (game.isFavorite) Color(0xFFFFD700) else PsTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = onConfig,
                        modifier = Modifier.size(32.dp).testTag("config_button_${game.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Config",
                            tint = PsTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onLaunch,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PsBluePrimary)
                            .testTag("launch_button_${game.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Launch",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompatibilityPill(grade: CompatibilityGrade) {
    val (bgColor, textColor) = when (grade) {
        CompatibilityGrade.PLAYABLE -> PsPlayableGreen.copy(alpha = 0.2f) to PsPlayableGreen
        CompatibilityGrade.IN_GAME -> PsInGameYellow.copy(alpha = 0.2f) to PsInGameYellow
        CompatibilityGrade.INTRO -> PsIntroOrange.copy(alpha = 0.2f) to PsIntroOrange
        CompatibilityGrade.LOADABLE -> PsLoadableRed.copy(alpha = 0.2f) to PsLoadableRed
        CompatibilityGrade.NOTHING -> PsNothingGray.copy(alpha = 0.2f) to PsNothingGray
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = grade.label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyLibraryView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = PsTextMuted,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No games found",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Text(
            text = "Try adjusting your search query or filter tags.",
            style = MaterialTheme.typography.bodySmall,
            color = PsTextSecondary
        )
    }
}

@Composable
fun AddGameDialog(
    onDismiss: () -> Unit,
    onAdd: (GameItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var titleId by remember { mutableStateOf("PPSA" + (10000..99999).random()) }
    var selectedPlatform by remember { mutableStateOf(GamePlatform.PS5) }
    var selectedFormat by remember { mutableStateOf(GameFormat.FOLDER) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Game to KytyPS5", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Game Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                OutlinedTextField(
                    value = titleId,
                    onValueChange = { titleId = it },
                    label = { Text("Title ID (e.g. PPSA01234 / CUSA01234)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Text("Platform Target:", color = PsTextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GamePlatform.values().forEach { plat ->
                        FilterChip(
                            selected = selectedPlatform == plat,
                            onClick = { selectedPlatform = plat },
                            label = { Text(plat.name, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Container Format:", color = PsTextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GameFormat.values().forEach { fmt ->
                        FilterChip(
                            selected = selectedFormat == fmt,
                            onClick = { selectedFormat = fmt },
                            label = { Text(fmt.name, fontSize = 11.sp) }
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
                            GameItem(
                                id = titleId.trim().uppercase(),
                                title = title.trim(),
                                publisher = "Custom Import",
                                version = "1.00",
                                sizeGb = 14.5,
                                platform = selectedPlatform,
                                format = selectedFormat,
                                engine = "Prospero ELF",
                                compatibilityGrade = CompatibilityGrade.IN_GAME,
                                path = "/storage/emulated/0/KytyPS5/Games/${titleId.trim().uppercase()}",
                                coverRes = R.drawable.ic_launcher_fg_1791544557377,
                                trophiesTotal = 15,
                                trophiesEarned = 0
                            )
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Add Game")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PsTextMuted)
            }
        },
        containerColor = PsSurfaceDark
    )
}
