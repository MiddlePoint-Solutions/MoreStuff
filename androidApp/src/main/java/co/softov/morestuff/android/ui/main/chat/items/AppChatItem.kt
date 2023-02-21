package co.softov.morestuff.android.ui.main.chat.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.ui.main.chat.ChatActions
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.appChatItem

@Composable
fun AppChatItem(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 45.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.padding(start = 10.dp, top = 4.dp, bottom = 4.dp),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            color = MaterialTheme.colorScheme.appChatItem,
            contentColor = contentColorFor(MaterialTheme.colorScheme.primary)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = message.content, style = MaterialTheme.typography.bodyLarge)
                Text(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.End),
                    text = message.createTime,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun TaskReminderItem(message: Message, actions: ChatActions) {
    Column {
        AppChatItem(message)

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
                contentColor = contentColorFor(MaterialTheme.colorScheme.primary)
            ) {
                Text(modifier = Modifier.padding(8.dp), text = message.replyContent ?: "")
            }
        }
    }
}

@Preview
@Composable
fun AppChatItemPreview() {

    MoreStuffTheme(darkTheme = true) {
        AppChatItem(
            message = MockData.Message.userNewTask.copy(contentType = ContentType.TASK_REMINDER)
        )
    }
}


