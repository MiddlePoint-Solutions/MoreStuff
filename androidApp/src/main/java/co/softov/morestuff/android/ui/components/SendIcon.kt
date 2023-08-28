package co.softov.morestuff.android.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable

@Composable
fun SendIcon(onClick: () -> Unit) {
    IconButton(
        onClick = onClick
    ) {
        Icon(Icons.Default.Send, contentDescription = null)
    }
}