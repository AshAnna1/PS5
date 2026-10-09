package com.example.data

import com.example.model.TrophyItem
import com.example.model.TrophyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TrophyRepository {

    private val sampleTrophies = listOf(
        // Astro Bot (PPSA02188)
        TrophyItem(1, "PPSA02188", "Astro-Nomical!", "Earn all trophies in Astro Bot.", TrophyType.PLATINUM, false, false, null, 8.4f),
        TrophyItem(2, "PPSA02188", "Singularity Survivor", "Rescue the stranded CPU Bots in Gorilla Nebula.", TrophyType.GOLD, false, true, "2026-10-02", 42.1f),
        TrophyItem(3, "PPSA02188", "Memory Stick Master", "Collect 100 golden puzzle pieces across the galaxy.", TrophyType.GOLD, false, true, "2026-10-03", 28.5f),
        TrophyItem(4, "PPSA02188", "Greatest Hits", "Unlock the VIP PlayStation cameo Bots.", TrophyType.GOLD, false, false, null, 18.0f),
        TrophyItem(5, "PPSA02188", "DualSense Tactile Tune", "Feel the raindrops through the haptic feedback.", TrophyType.SILVER, false, true, "2026-10-01", 76.2f),
        TrophyItem(6, "PPSA02188", "Ape Escape Specialist", "Catch the mischievous monkeys in the jungle stage.", TrophyType.SILVER, false, true, "2026-10-03", 51.4f),
        TrophyItem(7, "PPSA02188", "Secret Astro Lab", "Discover the hidden chamber beneath the Mothership.", TrophyType.SILVER, true, false, null, 9.8f),
        TrophyItem(8, "PPSA02188", "First Flight", "Take off from the PlayStation 5 crash site.", TrophyType.BRONZE, false, true, "2026-10-01", 98.6f),
        TrophyItem(9, "PPSA02188", "Spin Attack!", "Defeat 20 Spiky enemies with the spin attack.", TrophyType.BRONZE, false, true, "2026-10-01", 85.0f),
        TrophyItem(10, "PPSA02188", "Gatcha Collector", "Pull your first prize from the Gatcha machine.", TrophyType.BRONZE, false, true, "2026-10-02", 69.3f),

        // Demon's Souls (PPSA01342)
        TrophyItem(11, "PPSA01342", "Slayer of Trophies", "All Trophies Obtained.", TrophyType.PLATINUM, false, false, null, 5.2f),
        TrophyItem(12, "PPSA01342", "Return to Form", "Help a player vanquish a boss.", TrophyType.GOLD, false, false, null, 24.3f),
        TrophyItem(13, "PPSA01342", "Unwelcome Guest", "Vanquish a player as an invader.", TrophyType.GOLD, false, false, null, 19.8f),
        TrophyItem(14, "PPSA01342", "Phalanx's Trophy", "Slayer of Demon “Phalanx”.", TrophyType.SILVER, false, true, "2026-09-24", 81.5f),
        TrophyItem(15, "PPSA01342", "Tower Knight's Trophy", "Slayer of Demon “Tower Knight”.", TrophyType.SILVER, false, true, "2026-09-25", 63.2f),
        TrophyItem(16, "PPSA01342", "Fools' Idol's Trophy", "Slayer of Demon “Fools' Idol”.", TrophyType.SILVER, true, false, null, 40.7f),
        TrophyItem(17, "PPSA01342", "Seek Soul Power", "Embrace the power of the Monumental.", TrophyType.BRONZE, false, true, "2026-09-23", 94.1f),

        // Dreaming Sarah (CUSA08912)
        TrophyItem(18, "CUSA08912", "Dreamland Explorer", "Collect all trophies and wake up.", TrophyType.PLATINUM, false, true, "2026-09-18", 68.4f),
        TrophyItem(19, "CUSA08912", "Umbrella", "Found the umbrella in the forest.", TrophyType.GOLD, false, true, "2026-09-18", 92.1f),
        TrophyItem(20, "CUSA08912", "Shell Necklace", "Received the shell necklace at the beach.", TrophyType.GOLD, false, true, "2026-09-18", 88.0f),
        TrophyItem(21, "CUSA08912", "Alarm Clock", "Awoke from the deep slumber.", TrophyType.GOLD, true, true, "2026-09-18", 75.3f),

        // SILENT HILL: The Short Message (PPSA13962)
        TrophyItem(22, "PPSA13962", "You Can't Leave", "Escape the hallway labyrinth.", TrophyType.GOLD, false, true, "2026-09-15", 48.0f),
        TrophyItem(23, "PPSA13962", "Cherry Blossom Mystery", "Uncover Maya's sketchbook.", TrophyType.SILVER, true, true, "2026-09-15", 37.5f),
        TrophyItem(24, "PPSA13962", "Final Message", "Complete Anita's emotional journey.", TrophyType.GOLD, false, false, null, 21.0f),

        // Flappy Bird PS5 (PPSA00001)
        TrophyItem(25, "PPSA00001", "Kyty Master Aviator", "Reach a high score of 50 on PS5.", TrophyType.PLATINUM, false, false, null, 14.0f),
        TrophyItem(26, "PPSA00001", "First Flap", "Score your first point without hitting a pipe.", TrophyType.BRONZE, false, true, "2026-10-09", 99.0f),
        TrophyItem(27, "PPSA00001", "Bronze Wing", "Score 10 points.", TrophyType.SILVER, false, true, "2026-10-09", 74.0f),
        TrophyItem(28, "PPSA00001", "Silver Wing", "Score 25 points.", TrophyType.GOLD, false, false, null, 32.0f)
    )

    private val _trophies = MutableStateFlow(sampleTrophies)
    val trophies: StateFlow<List<TrophyItem>> = _trophies.asStateFlow()

    fun getTrophiesForGame(gameId: String): List<TrophyItem> {
        return _trophies.value.filter { it.gameId == gameId }
    }

    fun unlockTrophy(trophyId: Int): TrophyItem? {
        var unlockedItem: TrophyItem? = null
        _trophies.update { list ->
            list.map { trophy ->
                if (trophy.id == trophyId && !trophy.isEarned) {
                    val updated = trophy.copy(
                        isEarned = true,
                        earnedDate = "Just now"
                    )
                    unlockedItem = updated
                    updated
                } else trophy
            }
        }
        return unlockedItem
    }
}
