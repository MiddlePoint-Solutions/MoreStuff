package io.middlepoint.morestuff.shared.ui.screen.chat.items

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
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.copy_message
import morestuff.composeapp.generated.resources.delete
import morestuff.composeapp.generated.resources.share
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShowContextMenu(
    message: MessageUiModel,
    showMenu: Boolean,
    modifier: Modifier = Modifier,
    actions: io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions,
    close: () -> Unit,
) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = close,
        modifier = modifier,
    ) {
        val contextMenuItems = getContextMenuItems(message, actions, close)

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
  actions: io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions,
  close: () -> Unit,
): List<ContextMenuItem> {
    val deleteAction = {
        actions.deleteMessage(message)
        close()
    }

    return if (message.messageData?.filePath != null) {
        val shareAction = if (message.isPdfMessage) {
            {
                actions.sharePdf(message.messageData.filePath)
                close()
            }
        } else {
            {
                actions.shareImage(message.messageData.filePath)
                close()
            }
        }

        listOf(
            ContextMenuItem(stringResource(Res.string.delete), Icons.Default.Delete, deleteAction),
            ContextMenuItem(stringResource(Res.string.share), Icons.Default.Share, shareAction)
        )
    } else {
        listOf(
            ContextMenuItem(stringResource(Res.string.copy_message), Icons.Default.ContentCopy) {
                actions.copyMessage(message)
                close()
            },
            ContextMenuItem(stringResource(Res.string.delete), Icons.Default.Delete, deleteAction),
            ContextMenuItem(stringResource(Res.string.share), Icons.Default.Share) {
                actions.shareMessage(message)
                close()
            }
        )
    }
}



