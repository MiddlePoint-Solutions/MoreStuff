package co.softov.morestuff.android.ui.chat.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.Message

@Composable
fun ShowContextMenu(
    message: Message,
    showMenu: Boolean,
    modifier: Modifier = Modifier,
    copyMessage: (Message) -> Unit,
    deleteMessage: (Message) -> Unit,
    shareImage: (String) -> Unit,
    close: () -> Unit,
) {
    val contextMenuItems = getContextMenuItems(message, copyMessage, deleteMessage, shareImage, close)

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = close,
        modifier = modifier
    ) {
        contextMenuItems.forEach { item ->
            DropdownMenuItem(
                text = { Text(text = item.label) },
                onClick = item.onClick,
                leadingIcon = { Icon(item.icon, contentDescription = null) }
            )
        }
    }
}

@Composable
private fun getContextMenuItems(
    message: Message,
    copyMessage: (Message) -> Unit,
    deleteMessage: (Message) -> Unit,
    shareImage: (String) -> Unit,
    close: () -> Unit
): List<ContextMenuItem> {
    val deleteAction = {
        deleteMessage(message)
        close()
    }

    return if (message.messageData?.filePath != null) {
        listOf(
            ContextMenuItem(stringResource(R.string.delete), Icons.Default.Delete, deleteAction),
            ContextMenuItem(stringResource(R.string.share), Icons.Default.Share) {
                shareImage(message.messageData.filePath)
                close()
            },
        )
    } else {
        listOf(
            ContextMenuItem(stringResource(R.string.copy_message), Icons.Default.ContentCopy) {
                copyMessage(message)
                close()
            },
            ContextMenuItem(stringResource(R.string.delete), Icons.Default.Delete, deleteAction)
        )
    }
}



