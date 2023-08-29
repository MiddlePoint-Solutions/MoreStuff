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
    onCopyMessage: (Message) -> Unit,
    onDeleteMessage: (Message) -> Unit,
    onShareImage: (String) -> Unit,
    showMenu: Boolean,
    onClose: () -> Unit,
    modifier: Modifier,
) {
    val contextMenuItems =
        if (message.messageData?.filePath != null) {
            listOf(
                ContextMenuItem(stringResource(R.string.delete), Icons.Default.Delete) {
                    onDeleteMessage(message)
                    onClose()
                },
                ContextMenuItem(stringResource(R.string.share), Icons.Default.Share) {
                    onShareImage(message.messageData.filePath)
                    onClose()
                },
            )
        } else {
            listOf(
                ContextMenuItem(stringResource(R.string.copy_message), Icons.Default.ContentCopy) {
                    onCopyMessage(message)
                    onClose()
                },
                ContextMenuItem(stringResource(R.string.delete), Icons.Default.Delete) {
                    onDeleteMessage(message)
                    onClose()
                }
            )
        }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onClose,
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



