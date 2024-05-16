package io.middlepoint.morestuff.android.ui.chat.items

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class ContextMenuItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)