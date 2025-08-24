package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions

@Composable
fun AppChatItem(
    message: MessageUiModel,
    chatActions: ChatActions,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 45.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .padding(start = 5.dp, bottom = 4.dp)
        ) {
            Surface(
                onClick = { },
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomEnd = 14.dp,
                    bottomStart = 5.dp
                ),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(
                                start = 14.dp,
                                end = 15.dp,
                                top = 8.dp,
                                bottom = 3.dp
                            ),
                        text = message.content,
                        style = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 16.sp
                        )
                    )
                    Row(Modifier.align(Alignment.End)) {
                        MessageTime(
                            formattedTimeOnly = message.formattedTimeOnly,
                            modifier = Modifier,
                            textColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TaskReminderItem(message: MessageUiModel, actions: ChatActions) {
    Column {
        AppChatItem(message, actions)
    }
}

/*@Preview
@Composable
fun AppChatItemPreview() {

    MoreStuffTheme() {
        AppChatItem(
            message = MockData.Message.userNewTask.copy(contentType = ContentType.TASK_REMINDER),
            chatActions = ChatActions()
        )
    }
}*/


