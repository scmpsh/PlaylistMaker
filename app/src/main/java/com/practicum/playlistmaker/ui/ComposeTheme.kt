package com.practicum.playlistmaker.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PlaylistBlue = Color(0xFF3772E7)
val PlaylistBlueLight = Color(0xFF9FBBF3)
val PlaylistBlack = Color(0xFF1A1B22)
val PlaylistWhite = Color(0xFFFFFFFF)
val PlaylistGray = Color(0xFFAEAFB4)
val PlaylistLightGray = Color(0xFFE6E8EB)
val PlaylistTeal = Color(0xFF00D6C3)
val PlaylistTealLight = Color(0xFF76EAE0)

@Composable
fun PlaylistMakerTheme(
    isDarkEnabled: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isDarkEnabled) {
            darkColorScheme(
                primary = PlaylistBlue,
                onPrimary = PlaylistWhite,
                primaryContainer = PlaylistBlueLight,
                background = PlaylistBlack,
                onBackground = PlaylistWhite,
                surface = PlaylistBlack,
                onSurface = PlaylistWhite,
                surfaceVariant = PlaylistWhite.copy(alpha = 0.12f),
                outline = PlaylistGray,
                onSurfaceVariant = PlaylistGray
            )
        } else {
            lightColorScheme(
                primary = PlaylistTeal,
                onPrimary = PlaylistWhite,
                primaryContainer = PlaylistTealLight,
                background = PlaylistWhite,
                onBackground = PlaylistBlack,
                surface = PlaylistWhite,
                onSurface = PlaylistBlack,
                surfaceVariant = PlaylistLightGray,
                outline = PlaylistGray,
                onSurfaceVariant = PlaylistGray
            )
        },
        content = content
    )
}
