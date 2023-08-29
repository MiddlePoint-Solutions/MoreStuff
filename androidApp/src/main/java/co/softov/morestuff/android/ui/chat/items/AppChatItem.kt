package co.softov.morestuff.android.ui.chat.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.task.MessageWithFormattedTime

@Composable
fun AppChatItem(
    messageWithFormattedTime: MessageWithFormattedTime,
    chatActions: ChatActions,
) {
    val message = messageWithFormattedTime.message
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 45.dp, bottom = 7.dp)
            .clickable { chatActions.taskChatAction(message.taskId) },
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomEnd = 14.dp,
                bottomStart = 5.dp
            ),
            tonalElevation = 10.dp,
            contentColor = contentColorFor(MaterialTheme.colorScheme.onSecondaryContainer),
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = message.content, style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                Row(Modifier.align(Alignment.End).padding(top = 3.dp)) {
                    MessageTime(
                        formattedTimeOnly = messageWithFormattedTime.formattedTimeOnly,
                        modifier = Modifier,
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun TaskReminderItem(messageWithFormattedTime: MessageWithFormattedTime, actions: ChatActions) {
    Column {
        AppChatItem(messageWithFormattedTime, actions)
        val message = messageWithFormattedTime.message
        if (message.replyType == null) {
            Row(modifier = Modifier.padding(start = 15.dp, bottom = 8.dp)) {
                Button(onClick = {
                    actions.scheduleAction(message.scheduleId, ReplyType.SNOOZE)
                }) {
                    Text(text = stringResource(id = R.string.chat_action_today))
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Button(onClick = {
                    actions.scheduleAction(message.scheduleId, ReplyType.TOMORROW)
                }) {
                    Text(text = stringResource(id = R.string.chat_action_tomorrow))
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Button(onClick = {
                    actions.scheduleAction(message.scheduleId, ReplyType.DONE)
                }) {
                    Text(text = stringResource(id = R.string.done))
                }
            }
        } else if (message.replyContent.isNullOrBlank().not()) {
            Surface(
                shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                contentColor = contentColorFor(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(modifier = Modifier.padding(8.dp), text = message.replyContent ?: "")
            }
        }
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


