package co.softov.morestuff.android.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ContextMenuItem(val label: String, val icon: ImageVector, val onClick: () -> Unit)