package co.softov.morestuff.android.ui.chat.items

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.model.ContextMenuItem
import co.softov.morestuff.android.domain.model.Message

@Composable
fun ShowContextMenu(
    message: Message,
    onCopyMessage: (Message) -> Unit,
    onDeleteMessage: (Message) -> Unit,
    showMenu: Boolean,
    onClose: () -> Unit,
) {
    val contextMenuItems = listOf(
        ContextMenuItem("Copy", Icons.Default.ContentCopy) {
            onCopyMessage(message)
            onClose()
        },
        ContextMenuItem("Delete", Icons.Default.Delete) {
            onDeleteMessage(message)
            onClose()
        }

    )

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onClose,
    ) {
        contextMenuItems.forEach { item ->
            DropdownMenuItem(onClick = item.onClick) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(item.icon, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = item.label)
                }
            }
        }
    }
}


