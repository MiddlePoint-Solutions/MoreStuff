package co.softov.morestuff.android.presentation.content.adapter.view_holder

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.presentation.compose.viewMigration
import co.softov.morestuff.android.presentation.content.ChatActions
import co.softov.morestuff.android.presentation.theme.MoreStuffTheme
import co.softov.morestuff.android.presentation.theme.appChatItem
import co.softov.morestuff.android.presentation.theme.userChatItem

class ChatItemUserViewHolder(context: Context) : BaseChatItemViewHolder(ComposeView(context)) {

    override fun bind(message: Message) {
        (itemView as ComposeView).viewMigration {
            UserChatItem(message)
        }
    }
}

class ChatItemReminderViewHolder(
    context: Context,
    private val actions: ChatActions
) : BaseChatItemViewHolder(ComposeView(context)) {

    override fun bind(message: Message) {
        (itemView as ComposeView).viewMigration {
            AppChatItem(message, actions)
        }
    }
}

@Composable
fun AppChatItem(message: Message, actions: ChatActions) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 45.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            modifier = Modifier.padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                color = MaterialTheme.colors.appChatItem,
                contentColor = contentColorFor(MaterialTheme.colors.primary)
            ) {
                Text(modifier = Modifier.padding(8.dp), text = message.content)
            }

            if (message.contentType == ContentType.TASK_REMINDER) {
                if (message.replyType == null) {
                    Row(modifier = Modifier.padding(start = 15.dp)) {
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
                        contentColor = contentColorFor(MaterialTheme.colors.primary)
                    ) {
                        Text(modifier = Modifier.padding(8.dp), text = message.replyContent ?: "")
                    }
                }
            }
        }
    }
}

@Composable
fun UserChatItem(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 45.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier.padding(end = 10.dp, top = 4.dp, bottom = 4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                color = MaterialTheme.colors.userChatItem,
                contentColor = contentColorFor(MaterialTheme.colors.primary)
            ) {
                Text(
                    modifier = Modifier.padding(8.dp), text = message.content
                )
            }
        }
    }
}

@Preview
@Composable
fun UserChatItemPreview() {
    MoreStuffTheme(darkTheme = true) {
        UserChatItem(message = MockData.Message.userNewTask)
    }
}

@Preview
@Composable
fun AppChatItemPreview() {
    val chatActions = ChatActions(confirmationAction = { _, _ -> }, scheduleAction = { _, _ -> })
    MoreStuffTheme(darkTheme = true) {
        AppChatItem(
            message = MockData.Message.userNewTask.copy(contentType = ContentType.TASK_REMINDER),
            chatActions
        )
    }
}


object MockData {

    object Message {

        val userNewTask =
            Message(
                0,
                0,
                0,
                ContentType.USER_NEW_TASK,
                "",
                null,
                content = "Hello there!",
                null,
                null,
                null
            )

    }

}

