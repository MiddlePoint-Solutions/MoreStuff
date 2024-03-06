package co.softov.morestuff.android.ui.chat.items

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.items.MockData.messageUiModel
import co.softov.morestuff.android.ui.model.MessageUiModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun UserChatItem(
    message: MessageUiModel,
    actions: ChatActions,
) {
    var showMenu by remember { mutableStateOf(false) }
    val isNewUserTask by remember {
        derivedStateOf { message.contentType == ContentType.USER_NEW_TASK }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 45.dp, end = 5.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box {
            Row(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {

                if (isNewUserTask) {
                    FilledIconButton(onClick = { actions.taskChatAction(message.taskId) }) {
                        Icon(
                            imageVector = Icons.Default.Start,
                            contentDescription = ""
                        )
                    }
                }

                MessageContent(
                    message = message,
                    actions = actions,
                    showMenu = { showMenu = true }
                )
            }

            ShowContextMenu(
                message,
                showMenu = showMenu,
                actions = actions,
                close = { showMenu = false },
            )
        }
    }
}

@Composable
private fun MessageContent(
    message: MessageUiModel,
    actions: ChatActions,
    showMenu: () -> Unit
) {
    when {
        message.isPdfMessage -> {
            message.messageData?.filePath?.let { filePath ->
                PDFMessage(
                    pdfUri = Uri.parse(filePath),
                    actions = actions,
                    showMenu = showMenu,
                    message = message
                )
            }
        }

        message.isDataMessage -> {
            ImageMessage(
                message = message,
                actions = actions,
                showMenu = showMenu,
            )
        }

        else -> {
            MessageText(
                message = message,
                showMenu = showMenu
            )
        }
    }
}

@Composable
fun MessageTime(
    formattedTimeOnly: String?,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    formattedTimeOnly?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodySmall.copy(color = textColor),
            modifier = modifier.padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
        )
    }
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
@Composable
fun UserChatItemPreview() {
    MoreStuffTheme {
        UserChatItem(messageUiModel, ChatActions())
    }
}