package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.model.isPdfMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.copy_message
import morestuff.composeapp.generated.resources.delete
import morestuff.composeapp.generated.resources.edit_message
import morestuff.composeapp.generated.resources.share
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserMessageContextMenu(
    message: MessageUiModel,
    onDismissRequest: () -> Unit,
    showMenu: Boolean,
    actions: ChatActions,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        offset = DpOffset((-16).dp, 0.dp)
    ) {
        val contextMenuItems = getContextMenuItems(message, actions, onDismissRequest)

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

    return if (message.messageExtra?.url != null) {
        val shareAction = if (message.isPdfMessage) {
            {
                actions.sharePdf(message.messageExtra.url)
                close()
            }
        } else {
            {
                actions.shareImage(message.messageExtra.url)
                close()
            }
        }

        listOf(
            ContextMenuItem(stringResource(Res.string.delete), Icons.Default.Delete, deleteAction),
            ContextMenuItem(stringResource(Res.string.share), Icons.Default.Share, shareAction)
        )
    } else {
        listOf(
            ContextMenuItem(stringResource(Res.string.edit_message), Icons.Default.Edit) {
                actions.setEditingMessage(message.id)
                close()
            },
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



