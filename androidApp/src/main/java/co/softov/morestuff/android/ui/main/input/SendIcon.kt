package co.softov.morestuff.android.ui.main.input

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable

@Composable
fun SendIcon(sendIconClick: () -> Unit) {
    IconButton(
        onClick = sendIconClick
    ) {
        Icon(Icons.Default.Send, contentDescription = null)
    }
}