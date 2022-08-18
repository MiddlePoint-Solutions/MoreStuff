package co.softov.morestuff.android.ui.main.input

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable

@Composable
fun ListIcon(listIconClick: () -> Unit) {
    IconButton(
        onClick = listIconClick
    ) {
        Icon(Icons.Default.List, contentDescription = null)
    }
}