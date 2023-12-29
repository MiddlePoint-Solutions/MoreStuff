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
import androidx.compose.ui.window.PopupProperties
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.model.MessageUiModel

@Composable
fun ShowContextMenu(
    message: MessageUiModel,
    showMenu: Boolean,
    modifier: Modifier = Modifier,
    actions: ChatActions,
    close: () -> Unit,
) {
    val contextMenuItems =
        getContextMenuItems(message, actions, close)

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = close,
        modifier = modifier,
        properties = PopupProperties(focusable = false)
    ) {
        contextMenuItems.forEach { item ->
            DropdownMenuItem(
                text = { Text(text = item.label) },
                onClick = item.onClick,
                leadingIcon = { Icon(item.icon, contentDescription = null) },
            )
        }
    }
}

@Composable
private fun getContextMenuItems(
    message: MessageUiModel,
    actions: ChatActions,
    close: () -> Unit,
): List<ContextMenuItem> {
    val deleteAction = {
        actions.deleteMessage(message)
        close()
    }

    return if (message.messageData?.filePath != null) {
        val shareAction = if (message.isPdfMessage) {
            { actions.sharePdf(message.messageData.filePath); close() }
        } else {
            { actions.shareImage(message.messageData.filePath); close() }
        }

        listOf(
            ContextMenuItem(stringResource(R.string.delete), Icons.Default.Delete, deleteAction),
            ContextMenuItem(stringResource(R.string.share), Icons.Default.Share, shareAction)
        )
    } else {
        listOf(
            ContextMenuItem(stringResource(R.string.copy_message), Icons.Default.ContentCopy) {
                actions.copyMessage(message)
                close()
            },
            ContextMenuItem(stringResource(R.string.delete), Icons.Default.Delete, deleteAction),
            ContextMenuItem(stringResource(R.string.share), Icons.Default.Share) {
                actions.shareMessage(message)
                close()
            }
        )
    }
}



